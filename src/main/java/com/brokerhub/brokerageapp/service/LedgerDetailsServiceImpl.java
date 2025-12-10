package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.*;
import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class LedgerDetailsServiceImpl implements LedgerDetailsService{

    @Autowired
    DailyLedgerService dailyLedgerService;

    @Autowired
    LedgerDetailsRepository ledgerDetailsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Autowired
    LedgerRecordRepository ledgerRecordRepository;

    @Autowired
    BrokerRepository brokerRepository;

    @Autowired
    TenantContextService tenantContextService;

    @Autowired
    CurrentFinancialYearService currentFinancialYearService;
    
    @Autowired
    BrokerageService brokerageService;
    
    @Autowired
    BrokerageCacheService brokerageCacheService;

    public ResponseEntity<Long> createLedgerDetails(LedgerDetailsDTO ledgerDetailsDTO) {
        // Get current broker
        Broker currentBroker = tenantContextService.getCurrentBroker();
        
        // Get financial year ID - either from DTO or from current preference
        Long financialYearId = ledgerDetailsDTO.getFinancialYearId();
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBroker.getBrokerId());
            if (financialYearId == null) {
                log.error("No financial year specified and no current financial year set for broker {}", currentBroker.getBrokerId());
                return ResponseEntity.badRequest().body(null);
            }
            log.info("Using current financial year {} for broker {}", financialYearId, currentBroker.getBrokerId());
        }
        
        LocalDate date = ledgerDetailsDTO.getDate();
        DailyLedger dailyLedger = dailyLedgerService.getDailyLedgerByFinancialYear(date, financialYearId);
        Long sellerId = ledgerDetailsDTO.getFromSeller();
        User seller = null;
        Long sellerBrokerage = ledgerDetailsDTO.getSellerBrokerage() != null ? 
            Long.parseLong(ledgerDetailsDTO.getSellerBrokerage()) : 0L;
        if(null!=sellerId){
            Optional<User> sellerOptional = userRepository.findById(sellerId);
            seller = sellerOptional.orElse(null);
        }

        LedgerDetails ledgerDetails = new LedgerDetails();
        ledgerDetails.setBroker(currentBroker);
        
        // Set broker and financial year specific transaction number
        Long maxTransactionNumber = ledgerDetailsRepository.findMaxTransactionNumberByBrokerIdAndFinancialYearId(currentBroker.getBrokerId(), financialYearId);
        Long nextTransactionNumber = (maxTransactionNumber != null ? maxTransactionNumber : 0L) + 1;
        ledgerDetails.setBrokerTransactionNumber(nextTransactionNumber);
        ledgerDetails.setFinancialYearId(financialYearId);

        if(dailyLedger != null){
            ledgerDetails.setDailyLedger(dailyLedger);
        }
        if(seller != null){
            ledgerDetails.setFromSeller(seller);
        }
        List<LedgerRecordDTO> ledgerRecordDTOList = ledgerDetailsDTO.getLedgerRecordDTOList();
        Long totalBags = 0L;
        if(ledgerRecordDTOList != null && !ledgerRecordDTOList.isEmpty()) {
            for(int i=0; i<ledgerRecordDTOList.size(); i++){
            Long brokerage = ledgerRecordDTOList.get(i).getBrokerage();
//            if(brokerage<=0){brokerage= 1L;}
            Long quantity = ledgerRecordDTOList.get(i).getQuantity();
            Long productCost = ledgerRecordDTOList.get(i).getProductCost();
            LedgerRecord ledgerRecord = new LedgerRecord();
            ledgerRecord.setLedgerDetails(ledgerDetails);
            ledgerRecord.setBroker(currentBroker);
            ledgerRecord.setBrokerage((long) brokerage);

            Product product = productRepository.findById(ledgerRecordDTOList.get(i).getProductId()).get();
            if(product != null) {
                ledgerRecord.setProduct(product);
            }

            User buyer = userRepository.findByFirmName(ledgerRecordDTOList.get(i).getBuyerName()).get();
            if(buyer != null) {
                ledgerRecord.setToBuyer(buyer);
            }
            ledgerRecord.setQuantity(quantity);
            ledgerRecord.setProductCost(productCost);
            ledgerRecord.setTotalBrokerage(brokerage*quantity);
            ledgerRecord.setTotalProductsCost(productCost*quantity);
            totalBags+=quantity;
            BigDecimal totalBrokerage = BigDecimal.valueOf(quantity*brokerage);
            buyer.setTotalBagsBought(buyer.getTotalBagsBought()+quantity);
            buyer.setPayableAmount(buyer.getPayableAmount()+quantity*productCost);
            buyer.setTotalPayableBrokerage(buyer.getTotalPayableBrokerage().add(totalBrokerage));
            seller.setReceivableAmount(seller.getReceivableAmount()+quantity*productCost);
            currentBroker.setTotalBrokerage(currentBroker.getTotalBrokerage().add(totalBrokerage));
            userRepository.save(seller);
            userRepository.save(buyer);
            ledgerDetailsRepository.save(ledgerDetails);
            ledgerRecordRepository.save(ledgerRecord);
            }
        }
        if(seller != null) {
            seller.setTotalBagsSold(seller.getTotalBagsSold()+totalBags);
            seller.setTotalPayableBrokerage(seller.getTotalPayableBrokerage().add(BigDecimal.valueOf(totalBags*sellerBrokerage)));
            userRepository.save(seller);
        }
        if(currentBroker != null) {
            currentBroker.setTotalBrokerage(currentBroker.getTotalBrokerage().add(BigDecimal.valueOf(totalBags*sellerBrokerage)));
        }
        ledgerDetailsRepository.save(ledgerDetails);
        
        // Clear brokerage cache after transaction creation
        brokerageCacheService.evictBrokerageCache(financialYearId);
        
        log.info("Transaction {} created successfully for broker {}", nextTransactionNumber, currentBroker.getBrokerId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nextTransactionNumber);
    }


    public List<LedgerDetails> getAllLedgerDetails() {
        log.info("Fetching all ledger details");

        try {
            // Use the broker-aware optimized query that eagerly fetches records
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            List<LedgerDetails> ledgerDetails = ledgerDetailsRepository.findAllWithRecordsByBrokerId(currentBrokerId);

            if (ledgerDetails != null && !ledgerDetails.isEmpty()) {
                log.info("Successfully fetched {} ledger details with records", ledgerDetails.size());
                return ledgerDetails;
            } else {
                log.info("No ledger details found");
                return new ArrayList<>(); // Return empty list instead of null
            }
        } catch (Exception e) {
            log.error("Error fetching all ledger details", e);
            throw new RuntimeException("Failed to fetch all ledger details", e);
        }
    }


    public LedgerDetails getLedgerDetailById(Long ledgerDetailId, Long brokerId) {
        log.info("Fetching ledger details by ID: {} for broker: {}", ledgerDetailId, brokerId);

        if (ledgerDetailId == null) {
            log.error("Ledger detail ID cannot be null");
            throw new IllegalArgumentException("Ledger detail ID cannot be null");
        }

        try {
            // Use the broker-aware query that eagerly fetches all relations including records
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            Optional<LedgerDetails> ledgerOptional = ledgerDetailsRepository.findByBrokerIdAndIdWithAllRelations(currentBrokerId, ledgerDetailId);

            if (ledgerOptional.isPresent()) {
                LedgerDetails ledgerDetails = ledgerOptional.get();
                log.debug("Found ledger details with ID: {} and {} records",
                         ledgerDetailId,
                         ledgerDetails.getRecords() != null ? ledgerDetails.getRecords().size() : 0);

                // Force initialization of the records collection to ensure it's loaded
                if (ledgerDetails.getRecords() != null) {
                    ledgerDetails.getRecords().size();
                }

                return ledgerDetails;
            } else {
                log.warn("No ledger details found with ID: {}", ledgerDetailId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching ledger details by ID: {}", ledgerDetailId, e);
            throw new RuntimeException("Failed to fetch ledger details for ID: " + ledgerDetailId, e);
        }
    }

    public LedgerDetails getLedgerDetailByTransactionNumber(Long transactionNumber, Long brokerId, Long financialYearId) {
        log.info("Fetching ledger details by transaction number: {} for broker: {} in financial year: {}", transactionNumber, brokerId, financialYearId);

        if (transactionNumber == null) {
            log.error("Transaction number cannot be null");
            throw new IllegalArgumentException("Transaction number cannot be null");
        }
        
        // Use current financial year if not provided
        if (financialYearId == null) {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
            if (financialYearId == null) {
                log.error("No financial year specified and no current financial year set for broker {}", currentBrokerId);
                throw new IllegalArgumentException("Financial year ID is required. Please set current financial year first.");
            }
            log.info("Using current financial year {} for broker {}", financialYearId, currentBrokerId);
        }

        try {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            Optional<LedgerDetails> ledgerOptional = ledgerDetailsRepository.findByBrokerIdAndTransactionNumberAndFinancialYearIdWithAllRelations(currentBrokerId, transactionNumber, financialYearId);

            if (ledgerOptional.isPresent()) {
                LedgerDetails ledgerDetails = ledgerOptional.get();
                log.debug("Found ledger details with transaction number: {} and {} records",
                         transactionNumber,
                         ledgerDetails.getRecords() != null ? ledgerDetails.getRecords().size() : 0);

                if (ledgerDetails.getRecords() != null) {
                    ledgerDetails.getRecords().size();
                }

                return ledgerDetails;
            } else {
                log.warn("No ledger details found with transaction number: {}", transactionNumber);
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching ledger details by transaction number: {}", transactionNumber, e);
            throw new RuntimeException("Failed to fetch ledger details for transaction number: " + transactionNumber, e);
        }
    }

    @Override
    public OptimizedLedgerDetailsDTO getOptimizedLedgerDetailById(Long ledgerDetailId, Long brokerId) {
        log.info("Fetching optimized ledger details by ID: {} for broker: {}", ledgerDetailId, brokerId);

        if (ledgerDetailId == null) {
            log.error("Ledger detail ID cannot be null");
            throw new IllegalArgumentException("Ledger detail ID cannot be null");
        }

        try {
            // Use the broker-aware query that eagerly fetches all relations including records
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            Optional<LedgerDetails> ledgerOptional = ledgerDetailsRepository.findByBrokerIdAndIdWithAllRelations(currentBrokerId, ledgerDetailId);

            if (ledgerOptional.isPresent()) {
                LedgerDetails ledgerDetails = ledgerOptional.get();
                log.debug("Found ledger details with ID: {} and {} records",
                         ledgerDetailId,
                         ledgerDetails.getRecords() != null ? ledgerDetails.getRecords().size() : 0);

                // Convert to optimized DTO
                return convertToOptimizedLedgerDetailsDTO(ledgerDetails);
            } else {
                log.warn("No ledger details found with ID: {}", ledgerDetailId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching optimized ledger details by ID: {}", ledgerDetailId, e);
            throw new RuntimeException("Failed to fetch optimized ledger details for ID: " + ledgerDetailId, e);
        }
    }

    public List<DisplayLedgerDetailDTO> getAllLedgerDetailsOnDate(LocalDate date, Long brokerId, Long financialYearId) {
        List<DisplayLedgerDetailDTO> ledgerDetailsDTOList = new ArrayList<>();

        // Use current broker context instead of passed brokerId for security
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        List<Object[]> rawResults = ledgerDetailsRepository.findLedgersOnDateByBrokerIdRaw(currentBrokerId, date);
        
        // Convert Object[] to DTOs
        List<DateLedgerRecordDTO> ledgerRecordsOnDate = rawResults.stream()
                .map(row -> DateLedgerRecordDTO.builder()
                        .sellerId(((Number) row[0]).longValue())
                        .ledgerDetailsId(((Number) row[1]).longValue())
                        .buyerId(((Number) row[2]).longValue())
                        .productId(((Number) row[3]).longValue())
                        .quantity(((Number) row[4]).longValue())
                        .brokerage(((Number) row[5]).longValue())
                        .productCost(((Number) row[6]).longValue())
                        .transactionNumber(((Number) row[7]).longValue())
                        .build())
                .collect(Collectors.toList());

        for(DateLedgerRecordDTO dateLedgerRecord : ledgerRecordsOnDate){
            String sellerName = userRepository.findById(dateLedgerRecord.getSellerId()).get().getFirmName();
            DisplayLedgerDetailDTO existingLedgerDetail = checkTransactionExists(ledgerDetailsDTOList, sellerName, dateLedgerRecord.getTransactionNumber());
            if(null==existingLedgerDetail){
                DisplayLedgerDetailDTO ledgerDetailsDTO = new DisplayLedgerDetailDTO();

                ledgerDetailsDTO.setDate(date);
                ledgerDetailsDTO.setTransactionNumber(dateLedgerRecord.getTransactionNumber());
                ledgerDetailsDTO.setFinancialYearId(financialYearId);
                ledgerDetailsDTO.setBrokerId(currentBrokerId);
                ledgerDetailsDTO.setSellerName(userRepository.findById(dateLedgerRecord.getSellerId()).get().getFirmName());

                List<DisplayLedgerRecordDTO> ledgerRecordDTOList = new ArrayList<>();
                DisplayLedgerRecordDTO ledgerRecordDTO = new DisplayLedgerRecordDTO();
                ledgerRecordDTO.setBrokerage(dateLedgerRecord.getBrokerage());
                User buyer = userRepository.findById(dateLedgerRecord.getBuyerId()).get();
                ledgerRecordDTO.setBuyerName(buyer.getFirmName());
                ledgerRecordDTO.setLocation(buyer.getAddress() != null ? buyer.getAddress().getCity() : null);
                ledgerRecordDTO.setQuantity(dateLedgerRecord.getQuantity());
                ledgerRecordDTO.setProductName(productRepository.findById(dateLedgerRecord.getProductId()).get().getProductName());
                ledgerRecordDTO.setProductCost(dateLedgerRecord.getProductCost());
                ledgerRecordDTO.setTotal(dateLedgerRecord.getQuantity()*dateLedgerRecord.getProductCost());
                ledgerRecordDTOList.add(ledgerRecordDTO);

                ledgerDetailsDTO.setDisplayLedgerRecordDTOList(ledgerRecordDTOList);
                ledgerDetailsDTOList.add(ledgerDetailsDTO);
            }else{

                DisplayLedgerRecordDTO ledgerRecordDTO = new DisplayLedgerRecordDTO();
                ledgerRecordDTO.setBrokerage(dateLedgerRecord.getBrokerage());
                User buyer = userRepository.findById(dateLedgerRecord.getBuyerId()).get();
                ledgerRecordDTO.setBuyerName(buyer.getFirmName());
                ledgerRecordDTO.setLocation(buyer.getAddress() != null ? buyer.getAddress().getCity() : null);
                ledgerRecordDTO.setQuantity(dateLedgerRecord.getQuantity());
                ledgerRecordDTO.setProductName(productRepository.findById(dateLedgerRecord.getProductId()).get().getProductName());
                ledgerRecordDTO.setProductCost(dateLedgerRecord.getProductCost());
                ledgerRecordDTO.setTotal(dateLedgerRecord.getQuantity()*dateLedgerRecord.getProductCost());

                existingLedgerDetail.getDisplayLedgerRecordDTOList().add(ledgerRecordDTO);
            }
        }
        return ledgerDetailsDTOList;
    }

    @Override
    public List<LedgerDetailsDTO> getAllLedgerDetailsBySeller(Long sellerId, Long brokerId) {
        return null;
    }

//    @Override
//    public List<LedgerDetailsDTO> getAllLedgerDetailsBySeller(Long sellerId, Long brokerId) {
//        List<LedgerDetailsDTO> LedgerDetailsBySeller = ledgerDetailsRepository.findByFromSeller(sellerId);
//    }

    private DisplayLedgerDetailDTO checkTransactionExists(List<DisplayLedgerDetailDTO> ledgerDetailsDTOList, String sellerName, Long transactionNumber) {
        return ledgerDetailsDTOList.stream()
                .filter(ld -> ld.getSellerName().equalsIgnoreCase(sellerName) && ld.getTransactionNumber().equals(transactionNumber))
                .findFirst().orElse(null);
    }

    /**
     * Helper method to convert LedgerDetails entity to OptimizedLedgerDetailsDTO
     */
    private OptimizedLedgerDetailsDTO convertToOptimizedLedgerDetailsDTO(LedgerDetails ledgerDetails) {
        OptimizedLedgerDetailsDTO dto = OptimizedLedgerDetailsDTO.builder()
                .ledgerDetailsId(ledgerDetails.getLedgerDetailsId())
                .brokerTransactionNumber(ledgerDetails.getBrokerTransactionNumber())
                .build();

        // Set transaction date
        if (ledgerDetails.getDailyLedger() != null) {
            dto.setTransactionDate(ledgerDetails.getDailyLedger().getDate());
        }

        // Convert seller info (basic info only)
        if (ledgerDetails.getFromSeller() != null) {
            User seller = ledgerDetails.getFromSeller();
            OptimizedUserDTO sellerDTO = OptimizedUserDTO.builder()
                    .userId(seller.getUserId())
                    .firmName(seller.getFirmName())
                    .addressId(seller.getAddress() != null ? seller.getAddress().getAddressId() : null)
                    .build();
            dto.setFromSeller(sellerDTO);
        }

        // Convert records
        if (ledgerDetails.getRecords() != null && !ledgerDetails.getRecords().isEmpty()) {
            List<OptimizedLedgerRecordDTO> optimizedRecords =
                    ledgerDetails.getRecords().stream()
                            .map(this::convertToOptimizedLedgerRecordDTO)
                            .collect(Collectors.toList());
            dto.setRecords(optimizedRecords);

            // Calculate transaction summary
            dto.setTransactionSummary(calculateTransactionSummary(ledgerDetails.getRecords()));
        }

        return dto;
    }

    /**
     * Helper method to convert LedgerRecord entity to OptimizedLedgerRecordDTO
     */
    private OptimizedLedgerRecordDTO convertToOptimizedLedgerRecordDTO(LedgerRecord record) {
        OptimizedLedgerRecordDTO dto = OptimizedLedgerRecordDTO.builder()
                .ledgerRecordId(record.getLedgerRecordId())
                .quantity(record.getQuantity())
                .brokerage(record.getBrokerage())
                .productCost(record.getProductCost())
                .totalProductsCost(record.getTotalProductsCost())
                .totalBrokerage(record.getTotalBrokerage())
                .build();

        // Convert buyer info
        if (record.getToBuyer() != null) {
            OptimizedUserDTO buyerDTO = OptimizedUserDTO.builder()
                    .userId(record.getToBuyer().getUserId())
                    .firmName(record.getToBuyer().getFirmName())
                    .addressId(record.getToBuyer().getAddress() != null ?
                            record.getToBuyer().getAddress().getAddressId() : null)
                    .build();
            dto.setToBuyer(buyerDTO);
        }

        // Convert product info
        if (record.getProduct() != null) {
            OptimizedProductDTO productDTO = OptimizedProductDTO.builder()
                    .productId(record.getProduct().getProductId())
                    .productName(record.getProduct().getProductName())
                    .build();
            dto.setProduct(productDTO);
        }

        return dto;
    }

    /**
     * Calculate transaction summary for this specific ledger
     */
    private OptimizedLedgerDetailsDTO.TransactionSummaryDTO calculateTransactionSummary(List<LedgerRecord> records) {
        Long totalBags = records.stream()
                .mapToLong(record -> record.getQuantity() != null ? record.getQuantity() : 0L)
                .sum();

        BigDecimal totalBrokerage = records.stream()
                .map(record -> record.getTotalBrokerage() != null ?
                        BigDecimal.valueOf(record.getTotalBrokerage()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalReceivableAmount = records.stream()
                .mapToLong(record -> record.getTotalProductsCost() != null ? record.getTotalProductsCost() : 0L)
                .sum();

        BigDecimal averageBrokeragePerBag = totalBags > 0 ?
                totalBrokerage.divide(BigDecimal.valueOf(totalBags), 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;

        Integer numberOfProducts = (int) records.stream()
                .map(record -> record.getProduct() != null ? record.getProduct().getProductId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        Integer numberOfBuyers = (int) records.stream()
                .map(record -> record.getToBuyer() != null ? record.getToBuyer().getUserId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        return OptimizedLedgerDetailsDTO.TransactionSummaryDTO.builder()
                .totalBagsSoldInTransaction(totalBags)
                .totalBrokerageInTransaction(totalBrokerage)
                .totalReceivableAmountInTransaction(totalReceivableAmount)
                .averageBrokeragePerBag(averageBrokeragePerBag)
                .numberOfProducts(numberOfProducts)
                .numberOfBuyers(numberOfBuyers)
                .build();
    }

    @Override
    public OptimizedLedgerDetailsDTO getOptimizedLedgerDetailByTransactionNumber(Long transactionNumber, Long brokerId, Long financialYearId) {
        if (transactionNumber == null) {
            throw new IllegalArgumentException("Transaction number cannot be null");
        }

        // Use current broker if brokerId not provided
        Long currentBrokerId = (brokerId != null) ? brokerId : tenantContextService.getCurrentBrokerId();
        
        // Use current financial year if not provided or validate if provided
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
            if (financialYearId == null) {
                throw new IllegalArgumentException("Financial year ID is required. Please set current financial year first.");
            }
            log.info("Using current financial year {} for broker {}", financialYearId, currentBrokerId);
        } else {
            // Validate if provided financial year exists
            Long currentActiveFinancialYear = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
            if (currentActiveFinancialYear == null) {
                throw new IllegalArgumentException("No active financial year found for broker. Please set current financial year first.");
            }
        }

        log.info("Fetching optimized ledger details by transaction number: {} for broker: {} in financial year: {}", transactionNumber, currentBrokerId, financialYearId);

        try {
            Optional<LedgerDetails> ledgerOptional = ledgerDetailsRepository.findByBrokerIdAndTransactionNumberAndFinancialYearIdWithAllRelations(currentBrokerId, transactionNumber, financialYearId);

            if (ledgerOptional.isPresent()) {
                LedgerDetails ledgerDetails = ledgerOptional.get();
                log.debug("Found ledger details with transaction number: {} and {} records",
                         transactionNumber,
                         ledgerDetails.getRecords() != null ? ledgerDetails.getRecords().size() : 0);

                return convertToOptimizedLedgerDetailsDTO(ledgerDetails);
            } else {
                log.warn("Transaction does not exist with transaction number: {}", transactionNumber);
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching optimized ledger details by transaction number: {}", transactionNumber, e);
            throw new RuntimeException("Failed to fetch optimized ledger details for transaction number: " + transactionNumber, e);
        }
    }

    @Override
    public ResponseEntity<String> updateLedgerDetailByTransactionNumber(Long transactionNumber, Long brokerId, Long financialYearId, LedgerDetailsDTO ledgerDetailsDTO) {
        log.info("Updating ledger details by transaction number: {} for broker: {} in financial year: {}", transactionNumber, brokerId, financialYearId);

        if (transactionNumber == null) {
            throw new IllegalArgumentException("Transaction number cannot be null");
        }
        
        // Use current financial year if not provided
        if (financialYearId == null) {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
            if (financialYearId == null) {
                log.error("No financial year specified and no current financial year set for broker {}", currentBrokerId);
                throw new IllegalArgumentException("Financial year ID is required. Please set current financial year first.");
            }
            log.info("Using current financial year {} for broker {}", financialYearId, currentBrokerId);
        }

        try {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            Optional<LedgerDetails> existingLedgerOptional = ledgerDetailsRepository.findByBrokerIdAndTransactionNumberAndFinancialYearIdWithAllRelations(currentBrokerId, transactionNumber, financialYearId);

            if (!existingLedgerOptional.isPresent()) {
                log.warn("No ledger details found with transaction number: {}", transactionNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ledger details not found with transaction number: " + transactionNumber);
            }

            LedgerDetails existingLedger = existingLedgerOptional.get();
            Broker currentBroker = existingLedger.getBroker();
            User oldSeller = existingLedger.getFromSeller();
            
            // STEP 1: Reverse old transaction balances
            Long oldTotalBags = 0L;
            BigDecimal oldTotalSellerBrokerage = BigDecimal.ZERO;
            BigDecimal oldTotalBrokerBrokerage = BigDecimal.ZERO;
            
            if (existingLedger.getRecords() != null && !existingLedger.getRecords().isEmpty()) {
                for (LedgerRecord record : existingLedger.getRecords()) {
                    User buyer = record.getToBuyer();
                    Long quantity = record.getQuantity();
                    Long productCost = record.getProductCost();
                    Long brokerage = record.getBrokerage();
                    
                    if (buyer != null && quantity != null && productCost != null && brokerage != null) {
                        // Reverse old buyer updates
                        buyer.setTotalBagsBought(buyer.getTotalBagsBought() - quantity);
                        buyer.setPayableAmount(buyer.getPayableAmount() - (quantity * productCost));
                        BigDecimal recordBrokerage = BigDecimal.valueOf(quantity * brokerage);
                        buyer.setTotalPayableBrokerage(buyer.getTotalPayableBrokerage().subtract(recordBrokerage));
                        
                        // Reverse old seller receivable amount
                        if (oldSeller != null) {
                            oldSeller.setReceivableAmount(oldSeller.getReceivableAmount() - (quantity * productCost));
                        }
                        
                        oldTotalBags += quantity;
                        oldTotalBrokerBrokerage = oldTotalBrokerBrokerage.add(recordBrokerage);
                        
                        // Store buyer for later saving
                        
                        userRepository.save(buyer);
                    }
                }
            }
            
            // Reverse old seller totals
            if (oldSeller != null && oldTotalBags > 0) {
                oldSeller.setTotalBagsSold(oldSeller.getTotalBagsSold() - oldTotalBags);
                if (existingLedger.getRecords() != null && !existingLedger.getRecords().isEmpty()) {
                    Long sellerBrokerageRate = existingLedger.getRecords().get(0).getBrokerage();
                    oldTotalSellerBrokerage = BigDecimal.valueOf(oldTotalBags * sellerBrokerageRate);
                }
                oldSeller.setTotalPayableBrokerage(oldSeller.getTotalPayableBrokerage().subtract(oldTotalSellerBrokerage));
            }
            
            // Reverse old broker total brokerage
            if (currentBroker != null) {
                BigDecimal oldTotalBrokerageToReverse = oldTotalBrokerBrokerage.add(oldTotalSellerBrokerage);
                currentBroker.setTotalBrokerage(currentBroker.getTotalBrokerage().subtract(oldTotalBrokerageToReverse));
            }
            
            // Delete old records and clear the collection
            if (existingLedger.getRecords() != null) {
                ledgerRecordRepository.deleteAll(existingLedger.getRecords());
                existingLedger.getRecords().clear(); // Clear the collection to avoid stale references
            }
            
            // STEP 2: Update ledger details
            User newSeller = oldSeller;
            if (ledgerDetailsDTO.getFromSeller() != null) {
                Optional<User> sellerOptional = userRepository.findById(ledgerDetailsDTO.getFromSeller());
                if (sellerOptional.isPresent()) {
                    newSeller = sellerOptional.get();
                    existingLedger.setFromSeller(newSeller);
                }
            }

            if (ledgerDetailsDTO.getDate() != null) {
                DailyLedger dailyLedger = dailyLedgerService.getDailyLedgerByFinancialYear(ledgerDetailsDTO.getDate(), financialYearId);
                if (dailyLedger != null) {
                    existingLedger.setDailyLedger(dailyLedger);
                }
            }
            
            // STEP 3: Apply new transaction balances
            Long newTotalBags = 0L;
            Long newSellerBrokerage = ledgerDetailsDTO.getBrokerage();
            
            if (ledgerDetailsDTO.getLedgerRecordDTOList() != null && !ledgerDetailsDTO.getLedgerRecordDTOList().isEmpty()) {
                for (LedgerRecordDTO recordDTO : ledgerDetailsDTO.getLedgerRecordDTOList()) {
                    LedgerRecord newRecord = new LedgerRecord();
                    newRecord.setLedgerDetails(existingLedger);
                    newRecord.setBroker(currentBroker);
                    newRecord.setBrokerage(recordDTO.getBrokerage());
                    newRecord.setQuantity(recordDTO.getQuantity());
                    newRecord.setProductCost(recordDTO.getProductCost());
                    newRecord.setTotalBrokerage(recordDTO.getBrokerage() * recordDTO.getQuantity());
                    newRecord.setTotalProductsCost(recordDTO.getProductCost() * recordDTO.getQuantity());
                    
                    if (recordDTO.getProductId() != null) {
                        Optional<Product> productOptional = productRepository.findById(recordDTO.getProductId());
                        productOptional.ifPresent(newRecord::setProduct);
                    }
                    
                    User buyer = null;
                    if (recordDTO.getBuyerName() != null) {
                        Optional<User> buyerOptional = userRepository.findByFirmName(recordDTO.getBuyerName());
                        if (buyerOptional.isPresent()) {
                            buyer = buyerOptional.get();
                            newRecord.setToBuyer(buyer);
                        }
                    }
                    
                    // Apply new buyer updates
                    if (buyer != null) {
                        Long quantity = recordDTO.getQuantity();
                        Long productCost = recordDTO.getProductCost();
                        Long brokerage = recordDTO.getBrokerage();
                        
                        buyer.setTotalBagsBought(buyer.getTotalBagsBought() + quantity);
                        buyer.setPayableAmount(buyer.getPayableAmount() + (quantity * productCost));
                        BigDecimal recordBrokerage = BigDecimal.valueOf(quantity * brokerage);
                        buyer.setTotalPayableBrokerage(buyer.getTotalPayableBrokerage().add(recordBrokerage));
                        
                        // Apply new seller receivable amount
                        if (newSeller != null) {
                            newSeller.setReceivableAmount(newSeller.getReceivableAmount() + (quantity * productCost));
                        }
                        
                        currentBroker.setTotalBrokerage(currentBroker.getTotalBrokerage().add(recordBrokerage));
                        newTotalBags += quantity;
                    }
                    
                    ledgerRecordRepository.save(newRecord);
                }
            }
            
            // Apply new seller totals
            if (newSeller != null && newTotalBags > 0) {
                newSeller.setTotalBagsSold(newSeller.getTotalBagsSold() + newTotalBags);
                newSeller.setTotalPayableBrokerage(newSeller.getTotalPayableBrokerage().add(BigDecimal.valueOf(newTotalBags * newSellerBrokerage)));
            }
            
            // Apply new broker total brokerage
            if (currentBroker != null) {
                currentBroker.setTotalBrokerage(currentBroker.getTotalBrokerage().add(BigDecimal.valueOf(newTotalBags * newSellerBrokerage)));
                brokerRepository.save(currentBroker);
            }

            // Flush and clear session to avoid stale references
            ledgerDetailsRepository.flush();
            
            // Save all affected users at the end
            if (oldSeller != null) {
                userRepository.save(oldSeller);
            }
            if (newSeller != null && !newSeller.equals(oldSeller)) {
                userRepository.save(newSeller);
            }
            
            ledgerDetailsRepository.save(existingLedger);
            
            // Clear brokerage cache after transaction update
            brokerageCacheService.evictBrokerageCache(financialYearId);
            
            log.info("Successfully updated ledger details with transaction number: {} and balanced all accounts", transactionNumber);
            return ResponseEntity.ok("Ledger details updated successfully");

        } catch (Exception e) {
            log.error("Error updating ledger details for transaction number: {}", transactionNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update ledger details: " + e.getMessage());
        }
    }

    @Override
    public Long getNextTransactionNumber(Long financialYearId) {
        Broker currentBroker = tenantContextService.getCurrentBroker();
        
        // Use provided financial year or current one
        if (financialYearId == null) {
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBroker.getBrokerId());
            if (financialYearId == null) {
                throw new IllegalArgumentException("Financial year ID is required. Please set current financial year first.");
            }
        }
        
        Long maxTransactionNumber = ledgerDetailsRepository.findMaxTransactionNumberByBrokerIdAndFinancialYearId(currentBroker.getBrokerId(), financialYearId);
        return (maxTransactionNumber != null ? maxTransactionNumber : 0L) + 1;
    }

    @Override
    public ResponseEntity<Long> createLedgerDetailsFromNames(NewLedgerRequestDTO newLedgerRequestDTO) {
        log.info("Creating ledger details from names for broker: {}", newLedgerRequestDTO.getBrokerId());
        
        try {
            // Convert new format to existing format
            LedgerDetailsDTO ledgerDetailsDTO = convertNewRequestToLedgerDetailsDTO(newLedgerRequestDTO);
            
            // Call existing service method
            return createLedgerDetails(ledgerDetailsDTO);
            
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating ledger details from names", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    private LedgerDetailsDTO convertNewRequestToLedgerDetailsDTO(NewLedgerRequestDTO newRequest) {
        Broker currentBroker = tenantContextService.getCurrentBroker();
        
        // Validate seller name and get seller ID (case-insensitive)
        Optional<User> sellerOptional = userRepository.findByBrokerBrokerIdAndFirmNameIgnoreCase(currentBroker.getBrokerId(), newRequest.getSeller_name());
        if (!sellerOptional.isPresent()) {
            // Try exact match as fallback
            sellerOptional = userRepository.findByBrokerBrokerIdAndFirmName(currentBroker.getBrokerId(), newRequest.getSeller_name());
            if (!sellerOptional.isPresent()) {
                log.error("Seller validation failed. Seller name '{}' not found for broker {}", newRequest.getSeller_name(), currentBroker.getBrokerId());
                throw new IllegalArgumentException("Seller name not found: " + newRequest.getSeller_name() + ". Please ensure the seller is registered in the system.");
            }
        }
        User seller = sellerOptional.get();
        
        // Parse date - expecting d/M/yyyy format (day/month/year)
        LocalDate date;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            date = LocalDate.parse(newRequest.getOrder_date(), formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected d/M/yyyy (day/month/year), got: " + newRequest.getOrder_date());
        }
        
        // Create seller products list
        List<SellerProductDTO> sellerProducts = new ArrayList<>();
        for (NewLedgerRequestDTO.ProductListDTO productDto : newRequest.getProduct_list()) {
            // Find product by name (case-insensitive)
            List<Product> products = productRepository.findByBrokerBrokerIdAndProductNameIgnoreCase(currentBroker.getBrokerId(), productDto.getProduct_name());
            if (products.isEmpty()) {
                // Try exact match as fallback
                products = productRepository.findByBrokerBrokerIdAndProductName(currentBroker.getBrokerId(), productDto.getProduct_name());
                if (products.isEmpty()) {
                    log.error("Product validation failed. Product name '{}' not found for broker {}", productDto.getProduct_name(), currentBroker.getBrokerId());
                    throw new IllegalArgumentException("Product not found: " + productDto.getProduct_name() + ". Please ensure the product is registered in the system.");
                }
            }
            Product product = products.get(0); // Take first match
            
            SellerProductDTO sellerProduct = SellerProductDTO.builder()
                    .productId(String.valueOf(product.getProductId()))
                    .productCost(String.valueOf(productDto.getPrice()))
                    .build();
            sellerProducts.add(sellerProduct);
        }
        
        // Create ledger records list
        List<LedgerRecordDTO> ledgerRecords = new ArrayList<>();
        for (NewLedgerRequestDTO.BuyerDTO buyerDto : newRequest.getBuyers()) {
            // Validate buyer name (case-insensitive)
            Optional<User> buyerOptional = userRepository.findByBrokerBrokerIdAndFirmNameIgnoreCase(currentBroker.getBrokerId(), buyerDto.getBuyer_name());
            if (!buyerOptional.isPresent()) {
                // Try exact match as fallback
                buyerOptional = userRepository.findByBrokerBrokerIdAndFirmName(currentBroker.getBrokerId(), buyerDto.getBuyer_name());
                if (!buyerOptional.isPresent()) {
                    log.error("Buyer validation failed. Buyer name '{}' not found for broker {}", buyerDto.getBuyer_name(), currentBroker.getBrokerId());
                    throw new IllegalArgumentException("Buyer name not found: " + buyerDto.getBuyer_name() + ". Please ensure the buyer is registered in the system.");
                }
            }
            
            for (NewLedgerRequestDTO.BuyerProductDTO buyerProduct : buyerDto.getProducts()) {
                // Find product by name (case-insensitive)
                List<Product> products = productRepository.findByBrokerBrokerIdAndProductNameIgnoreCase(currentBroker.getBrokerId(), buyerProduct.getProduct_name());
                if (products.isEmpty()) {
                    // Try exact match as fallback
                    products = productRepository.findByBrokerBrokerIdAndProductName(currentBroker.getBrokerId(), buyerProduct.getProduct_name());
                    if (products.isEmpty()) {
                        log.error("Product validation failed. Product name '{}' not found for broker {}", buyerProduct.getProduct_name(), currentBroker.getBrokerId());
                        throw new IllegalArgumentException("Product not found: " + buyerProduct.getProduct_name() + ". Please ensure the product is registered in the system.");
                    }
                }
                Product product = products.get(0); // Take first match
                
                LedgerRecordDTO ledgerRecord = LedgerRecordDTO.builder()
                        .buyerName(buyerDto.getBuyer_name())
                        .productId(product.getProductId())
                        .quantity(buyerProduct.getQuantity())
                        .brokerage(buyerDto.getBuyerBrokerage())
                        .productCost(buyerProduct.getPrice())
                        .build();
                ledgerRecords.add(ledgerRecord);
            }
        }
        
        // Calculate total brokerage (sum of all buyer brokerages * quantities)
        Long totalBrokerage = ledgerRecords.stream()
                .mapToLong(record -> record.getBrokerage() * record.getQuantity())
                .sum();
        
        return LedgerDetailsDTO.builder()
                .brokerId(newRequest.getBrokerId())
                .financialYearId(newRequest.getFinancialYearId())
                .sellerBrokerage(String.valueOf(newRequest.getSellerBrokerage()))
                .brokerage(totalBrokerage)
                .fromSeller(seller.getUserId())
                .date(date)
                .sellerProducts(sellerProducts)
                .ledgerRecordDTOList(ledgerRecords)
                .build();
    }

    @Override
    public ResponseEntity<String> deleteLedgerDetailByTransactionNumber(Long transactionNumber, Long brokerId, Long financialYearId) {
        log.info("Deleting ledger details by transaction number: {} for broker: {} in financial year: {}", transactionNumber, brokerId, financialYearId);

        if (transactionNumber == null) {
            throw new IllegalArgumentException("Transaction number cannot be null");
        }
        
        // Use current financial year if not provided
        if (financialYearId == null) {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            financialYearId = currentFinancialYearService.getCurrentFinancialYearId(currentBrokerId);
            if (financialYearId == null) {
                log.error("No financial year specified and no current financial year set for broker {}", currentBrokerId);
                throw new IllegalArgumentException("Financial year ID is required. Please set current financial year first.");
            }
            log.info("Using current financial year {} for broker {}", financialYearId, currentBrokerId);
        }

        try {
            Long currentBrokerId = tenantContextService.getCurrentBrokerId();
            Optional<LedgerDetails> existingLedgerOptional = ledgerDetailsRepository.findByBrokerIdAndTransactionNumberAndFinancialYearIdWithAllRelations(currentBrokerId, transactionNumber, financialYearId);

            if (!existingLedgerOptional.isPresent()) {
                log.warn("No ledger details found with transaction number: {}", transactionNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ledger details not found with transaction number: " + transactionNumber);
            }

            LedgerDetails existingLedger = existingLedgerOptional.get();
            Broker currentBroker = existingLedger.getBroker();
            User seller = existingLedger.getFromSeller();
            
            // Calculate totals to reverse BEFORE deletion
            Long totalBags = 0L;
            BigDecimal totalSellerBrokerage = BigDecimal.ZERO;
            BigDecimal totalBrokerBrokerage = BigDecimal.ZERO;
            
            // Reverse all buyer and seller updates from each record
            if (existingLedger.getRecords() != null && !existingLedger.getRecords().isEmpty()) {
                for (LedgerRecord record : existingLedger.getRecords()) {
                    User buyer = record.getToBuyer();
                    Long quantity = record.getQuantity();
                    Long productCost = record.getProductCost();
                    Long brokerage = record.getBrokerage();
                    
                    if (buyer != null && quantity != null && productCost != null && brokerage != null) {
                        // Reverse buyer updates
                        buyer.setTotalBagsBought(buyer.getTotalBagsBought() - quantity);
                        buyer.setPayableAmount(buyer.getPayableAmount() - (quantity * productCost));
                        BigDecimal recordBrokerage = BigDecimal.valueOf(quantity * brokerage);
                        buyer.setTotalPayableBrokerage(buyer.getTotalPayableBrokerage().subtract(recordBrokerage));
                        
                        // Reverse seller receivable amount
                        if (seller != null) {
                            seller.setReceivableAmount(seller.getReceivableAmount() - (quantity * productCost));
                        }
                        
                        totalBags += quantity;
                        totalBrokerBrokerage = totalBrokerBrokerage.add(recordBrokerage);
                        
                        userRepository.save(buyer);
                    }
                }
            }
            
            // Reverse seller totals
            if (seller != null && totalBags > 0) {
                seller.setTotalBagsSold(seller.getTotalBagsSold() - totalBags);
                // Calculate seller brokerage from ledger details brokerage field
                if (existingLedger.getRecords() != null && !existingLedger.getRecords().isEmpty()) {
                    // Get seller brokerage from first record or calculate from total
                    Long sellerBrokerageRate = existingLedger.getRecords().get(0).getBrokerage(); // Assuming same rate
                    totalSellerBrokerage = BigDecimal.valueOf(totalBags * sellerBrokerageRate);
                }
                seller.setTotalPayableBrokerage(seller.getTotalPayableBrokerage().subtract(totalSellerBrokerage));
                userRepository.save(seller);
            }
            
            // Reverse broker total brokerage
            if (currentBroker != null) {
                BigDecimal totalBrokerageToReverse = totalBrokerBrokerage.add(totalSellerBrokerage);
                currentBroker.setTotalBrokerage(currentBroker.getTotalBrokerage().subtract(totalBrokerageToReverse));
                brokerRepository.save(currentBroker);
            }
            
            // Delete associated ledger records
            if (existingLedger.getRecords() != null && !existingLedger.getRecords().isEmpty()) {
                ledgerRecordRepository.deleteAll(existingLedger.getRecords());
            }

            // Delete the ledger details
            ledgerDetailsRepository.delete(existingLedger);
            
            // Clear brokerage cache after transaction deletion
            brokerageCacheService.evictBrokerageCache(financialYearId);
            
            log.info("Successfully deleted ledger details with transaction number: {} and reversed all balances", transactionNumber);
            return ResponseEntity.ok("Ledger details deleted successfully");

        } catch (Exception e) {
            log.error("Error deleting ledger details for transaction number: {}", transactionNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete ledger details: " + e.getMessage());
        }
    }

}
