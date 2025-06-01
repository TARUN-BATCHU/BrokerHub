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

    public ResponseEntity<String> createLedgerDetails(LedgerDetailsDTO ledgerDetailsDTO) {
        LocalDate date = ledgerDetailsDTO.getDate();
        DailyLedger dailyLedger = dailyLedgerService.getDailyLedger(date);
        Long sellerId = ledgerDetailsDTO.getFromSeller();
        User seller = null;
        Long sellerBrokerage = ledgerDetailsDTO.getBrokerage();
//        if(sellerBrokerage<=0){sellerBrokerage= 1L;}
        Long brokerId = ledgerDetailsDTO.getBrokerId();
        Optional<Broker> brokerOptional = brokerRepository.findById(brokerId);
        Broker broker = brokerOptional.orElse(null);
        if(null!=sellerId){
            Optional<User> sellerOptional = userRepository.findById(sellerId);
            seller = sellerOptional.orElse(null);
        }

        LedgerDetails ledgerDetails = new LedgerDetails();

        // Set broker for multi-tenant isolation
        Broker currentBroker = tenantContextService.getCurrentBroker();
        ledgerDetails.setBroker(currentBroker);

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
            broker.setTotalBrokerage(broker.getTotalBrokerage().add(totalBrokerage));
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
        if(broker != null) {
            broker.setTotalBrokerage(broker.getTotalBrokerage().add(BigDecimal.valueOf(totalBags*sellerBrokerage)));
        }
        ledgerDetailsRepository.save(ledgerDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully");
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

    public List<DisplayLedgerDetailDTO> getAllLedgerDetailsOnDate(LocalDate date, Long brokerId) {
        List<DisplayLedgerDetailDTO> ledgerDetailsDTOList = new ArrayList<>();

        // Use current broker context instead of passed brokerId for security
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        List<DateLedgerRecordDTO> ledgerRecordsOnDate = ledgerDetailsRepository.findLedgersOnDateByBrokerId(currentBrokerId, date);

        for(DateLedgerRecordDTO dateLedgerRecord : ledgerRecordsOnDate){
            DisplayLedgerDetailDTO existingLedgerDetail = checkSellerExists(ledgerDetailsDTOList,userRepository.findById(dateLedgerRecord.getSellerId()).get().getFirmName());
            if(null==existingLedgerDetail){
                DisplayLedgerDetailDTO ledgerDetailsDTO = new DisplayLedgerDetailDTO();

                ledgerDetailsDTO.setDate(date);
                //ledgerDetailsDTO.setBrokerage();
                //ledgerDetailsDTO.setBrokerId();
                ledgerDetailsDTO.setSellerName(userRepository.findById(dateLedgerRecord.getSellerId()).get().getFirmName());

                List<DisplayLedgerRecordDTO> ledgerRecordDTOList = new ArrayList<>();
                DisplayLedgerRecordDTO ledgerRecordDTO = new DisplayLedgerRecordDTO();
                ledgerRecordDTO.setBrokerage(dateLedgerRecord.getBrokerage());
                ledgerRecordDTO.setBuyerName(userRepository.findById(dateLedgerRecord.getBuyerId()).get().getFirmName());
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
                ledgerRecordDTO.setBuyerName(userRepository.findById(dateLedgerRecord.getBuyerId()).get().getFirmName());
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

    private DisplayLedgerDetailDTO checkSellerExists(List<DisplayLedgerDetailDTO> ledgerDetailsDTOList,String sellerName) {
        return ledgerDetailsDTOList.stream().filter(ld -> ld.getSellerName().equalsIgnoreCase(sellerName)).findFirst().orElse(null);
    }

    /**
     * Helper method to convert LedgerDetails entity to OptimizedLedgerDetailsDTO
     */
    private OptimizedLedgerDetailsDTO convertToOptimizedLedgerDetailsDTO(LedgerDetails ledgerDetails) {
        OptimizedLedgerDetailsDTO dto = OptimizedLedgerDetailsDTO.builder()
                .ledgerDetailsId(ledgerDetails.getLedgerDetailsId())
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



}
