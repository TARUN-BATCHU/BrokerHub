package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.*;
import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.repository.DailyLedgerRepository;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

@Service
@Transactional
@Slf4j
public class DailyLedgerServiceImpl implements DailyLedgerService{

    @Autowired
    DailyLedgerRepository dailyLedgerRepository;

    @Autowired
    FinancialYearRepository financialYearRepository;

    public Long createDailyLedger(Long financialYearId, LocalDate date){
        if(null != financialYearId && null!= date) {
            Optional<FinancialYear> financialYear = financialYearRepository.findById(financialYearId);
            if (financialYear.isPresent() && (date.isEqual(financialYear.get().getStart()) || date.isEqual(financialYear.get().getEnd()) || (date.isAfter(financialYear.get().getStart()) && date.isBefore(financialYear.get().getEnd())))) {
                if (null == dailyLedgerRepository.findByDate(date)) {
                    DailyLedger dailyLedger = new DailyLedger();
                    dailyLedger.setDate(date);
                    dailyLedger.setFinancialYear(financialYear.get());
                    dailyLedgerRepository.save(dailyLedger);
                }
                return dailyLedgerRepository.findByDate(date).getDailyLedgerId();
            }
        }
        return null;
    }


    public Long getDailyLedgerId(LocalDate date) {
        log.info("Getting daily ledger ID for date: {}", date);

        if (date == null) {
            log.error("Date parameter cannot be null");
            throw new IllegalArgumentException("Date parameter cannot be null");
        }

        try {
            DailyLedger existingLedger = dailyLedgerRepository.findByDate(date);
            if (existingLedger != null) {
                log.debug("Found existing daily ledger with ID: {} for date: {}",
                         existingLedger.getDailyLedgerId(), date);
                return existingLedger.getDailyLedgerId();
            } else {
                // Auto-create daily ledger if it doesn't exist
                log.info("Daily ledger not found for date: {}. Creating new daily ledger.", date);
                DailyLedger newLedger = createDailyLedgerForDate(date);
                return newLedger.getDailyLedgerId();
            }
        } catch (Exception e) {
            log.error("Error getting daily ledger ID for date: {}", date, e);
            throw new RuntimeException("Failed to get daily ledger ID for date: " + date, e);
        }
    }

    public DailyLedger getDailyLedger(LocalDate date) {
        log.info("Fetching daily ledger for date: {}", date);

        if (date == null) {
            log.error("Date parameter cannot be null");
            throw new IllegalArgumentException("Date parameter cannot be null");
        }

        try {
            // Use the new method that eagerly fetches ledgerDetails
            Optional<DailyLedger> dailyLedgerOpt = dailyLedgerRepository.findByDateWithLedgerDetails(date);

            if (dailyLedgerOpt.isPresent()) {
                DailyLedger dailyLedger = dailyLedgerOpt.get();
                log.debug("Found existing daily ledger with ID: {} for date: {}",
                         dailyLedger.getDailyLedgerId(), date);

                // Initialize the records collection for each ledger detail to avoid lazy loading
                if (dailyLedger.getLedgerDetails() != null) {
                    for (LedgerDetails ledgerDetail : dailyLedger.getLedgerDetails()) {
                        // Force initialization of the records collection
                        if (ledgerDetail.getRecords() != null) {
                            ledgerDetail.getRecords().size();
                        }
                    }
                }
                return dailyLedger;
            } else {
                // Auto-create daily ledger if it doesn't exist
                log.info("Daily ledger not found for date: {}. Creating new daily ledger.", date);
                return createDailyLedgerForDate(date);
            }
        } catch (Exception e) {
            log.error("Error fetching daily ledger for date: {}", date, e);
            throw new RuntimeException("Failed to fetch daily ledger for date: " + date, e);
        }
    }

    /**
     * Helper method to create a daily ledger for a given date
     * Automatically finds the appropriate financial year for the date
     */
    private DailyLedger createDailyLedgerForDate(LocalDate date) {
        log.info("Creating new daily ledger for date: {}", date);

        try {
            // Find the financial year that contains this date
            FinancialYear financialYear = findFinancialYearForDate(date);

            if (financialYear == null) {
                log.error("No financial year found for date: {}", date);
                throw new RuntimeException("No financial year found for date: " + date);
            }

            // Create new daily ledger
            DailyLedger dailyLedger = DailyLedger.builder()
                    .date(date)
                    .financialYear(financialYear)
                    .build();

            DailyLedger savedLedger = dailyLedgerRepository.save(dailyLedger);
            log.info("Successfully created daily ledger with ID: {} for date: {}",
                    savedLedger.getDailyLedgerId(), date);

            return savedLedger;
        } catch (Exception e) {
            log.error("Error creating daily ledger for date: {}", date, e);
            throw new RuntimeException("Failed to create daily ledger for date: " + date, e);
        }
    }

    /**
     * Helper method to find the financial year for a given date
     */
    private FinancialYear findFinancialYearForDate(LocalDate date) {
        log.debug("Finding financial year for date: {}", date);

        List<FinancialYear> allFinancialYears = financialYearRepository.findAll();

        for (FinancialYear fy : allFinancialYears) {
            if (isDateInFinancialYear(date, fy)) {
                log.debug("Found financial year: {} for date: {}", fy.getFinancialYearName(), date);
                return fy;
            }
        }

        log.warn("No financial year found for date: {}", date);
        return null;
    }

    /**
     * Helper method to check if a date falls within a financial year
     */
    private boolean isDateInFinancialYear(LocalDate date, FinancialYear financialYear) {
        return (date.isEqual(financialYear.getStart()) || date.isEqual(financialYear.getEnd()) ||
                (date.isAfter(financialYear.getStart()) && date.isBefore(financialYear.getEnd())));
    }

    public DailyLedger getDailyLedgerOnDate(LocalDate date) throws FileNotFoundException {
        String fileName = date.toString()+" Records";
        String filePath = "C:\\Users\\HP\\Desktop\\Pdfs\\"+fileName+".pdf";

        // Use the new method that eagerly fetches ledgerDetails and records
        Optional<DailyLedger> dailyLedger = dailyLedgerRepository.findByDateWithLedgerDetails(date);

        if(!dailyLedger.isPresent()) {
            return null;
        }
//        try {
//            PdfWriter pdfWriter = new PdfWriter(filePath);
//            PdfDocument pdf = new PdfDocument(pdfWriter);
//            pdf.addNewPage();
//            Document document = new Document(pdf);
//            StringBuilder ledgerInfo = new StringBuilder();
//            ledgerInfo.append()
//            document.add(new Paragraph(dailyLedger.get().toString()));
//            document.close();
//        }
//        catch(FileNotFoundException e){
//            e.printStackTrace();
//        }
//        }
//        else{
//            return null;
//        }
//        return new DailyLedger();
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Header table with Date and Financial Year
            Table headerTable = new Table(2);
            headerTable.setWidthPercent(100);
            Cell dateCell = new Cell();
            dateCell.add(new Paragraph("Date: " + date.toString()).setBold().setFontSize(12));
            dateCell.setTextAlignment(TextAlignment.LEFT);
            headerTable.addCell(dateCell);

            Cell financialYearCell = new Cell();
            financialYearCell.add(new Paragraph("Financial Year: " + dailyLedger.get().getFinancialYear().getFinancialYearName()).setBold().setFontSize(12));
            financialYearCell.setTextAlignment(TextAlignment.RIGHT);
            headerTable.addCell(financialYearCell);

            document.add(headerTable);

            // Loop through each ledger detail and create a separate table
            for (LedgerDetails ledgerDetail : dailyLedger.get().getLedgerDetails()) {
                Table ledgerTable = new Table(new float[] { 100, 150, 200, }); // Adjust column widths as needed
                //ledgerTable.setBorderColor(Color.BLACK);

                // Ledger Detail heading (Firm Name)
                Cell firmNameCell = new Cell().setBackgroundColor(Color.WHITE);
                firmNameCell.add(new Paragraph("Firm Name: " + ledgerDetail.getFromSeller().getFirmName()).setBold());
                ledgerTable.addCell(firmNameCell);


                // Table headers (ID, Name, Product, Quantity, Brokerage, Product Cost, Total Cost, Total Brokerage)
                Cell idCell = new Cell().setBackgroundColor(Color.DARK_GRAY);
                idCell.add(new Paragraph("ID"));
                ledgerTable.addCell(idCell);
                ledgerTable.addCell(new Cell().setBackgroundColor(Color.GRAY).add(new Paragraph("Name")));
                ledgerTable.addCell(new Cell().setBackgroundColor(Color.GRAY).add(new Paragraph("Product")));
                ledgerTable.addCell(new Cell().setBackgroundColor(Color.GRAY).add(new Paragraph("Quantity")));
                ledgerTable.addCell(new Cell().setBackgroundColor(Color.GRAY).add(new Paragraph("Brokerage")));
                ledgerTable.addCell(new Cell().setBackgroundColor(Color.GRAY).add(new Paragraph("Product Cost")));
                ledgerTable.addCell(new Cell().setBackgroundColor(Color.GRAY).add(new Paragraph("Total Cost")));
                ledgerTable.addCell(new Cell().setBackgroundColor(Color.GRAY).add(new Paragraph("Total Brokerage")));

                // Loop through each record in the ledger detail and add data to the table
                double totalBrokerage = 0;
                for (LedgerRecord record : ledgerDetail.getRecords()) {
                    ledgerTable.addCell(new Cell().add(new Paragraph(String.valueOf(record.getLedgerRecordId()))));
                    ledgerTable.addCell(new Cell().add(new Paragraph(record.getToBuyer().getFirmName())));
                    ledgerTable.addCell(new Cell().add(new Paragraph(record.getProduct().getProductName())));
                    ledgerTable.addCell(new Cell().add(new Paragraph(String.valueOf(record.getQuantity()))));
                }
            }
            return new DailyLedger();
    }

    @Override
    public OptimizedDailyLedgerDTO getOptimizedDailyLedger(LocalDate date) {
        // Use the same method that eagerly fetches ledgerDetails
        Optional<DailyLedger> dailyLedgerOpt = dailyLedgerRepository.findByDateWithLedgerDetails(date);

        if (dailyLedgerOpt.isPresent()) {
            DailyLedger dailyLedger = dailyLedgerOpt.get();

            // Convert to optimized DTO
            OptimizedDailyLedgerDTO optimizedDTO = OptimizedDailyLedgerDTO.builder()
                    .dailyLedgerId(dailyLedger.getDailyLedgerId())
                    .date(dailyLedger.getDate())
                    .financialYearId(dailyLedger.getFinancialYear() != null ?
                            dailyLedger.getFinancialYear().getYearId() : null)
                    .build();

            // Convert ledger details
            if (dailyLedger.getLedgerDetails() != null) {
                List<OptimizedLedgerDetailsDTO> optimizedLedgerDetails =
                        dailyLedger.getLedgerDetails().stream()
                                .map(this::convertToOptimizedLedgerDetailsDTO)
                                .collect(Collectors.toList());
                optimizedDTO.setLedgerDetails(optimizedLedgerDetails);
            }

            return optimizedDTO;
        }

        return null;
    }

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

        // Convert records and calculate transaction-specific data
        if (ledgerDetails.getRecords() != null && !ledgerDetails.getRecords().isEmpty()) {
            // Force initialization of the records collection
            ledgerDetails.getRecords().size();

            List<OptimizedLedgerRecordDTO> optimizedRecords =
                    ledgerDetails.getRecords().stream()
                            .map(this::convertToOptimizedLedgerRecordDTO)
                            .collect(Collectors.toList());
            dto.setRecords(optimizedRecords);

            // Calculate transaction summary (delegate to LedgerDetailsServiceImpl logic)
            dto.setTransactionSummary(calculateTransactionSummaryForDailyLedger(ledgerDetails.getRecords()));
        }

        return dto;
    }

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

    @Override
    public DailyLedger getDailyLedgerWithPagination(LocalDate date, Pageable pageable) {
        log.info("Fetching daily ledger with pagination for date: {}, page: {}, size: {}",
                date, pageable.getPageNumber(), pageable.getPageSize());

        if (date == null) {
            log.error("Date parameter cannot be null");
            throw new IllegalArgumentException("Date parameter cannot be null");
        }

        if (pageable == null) {
            log.error("Pageable parameter cannot be null");
            throw new IllegalArgumentException("Pageable parameter cannot be null");
        }

        try {
            // First, get or create the daily ledger
            Optional<DailyLedger> dailyLedgerOpt = dailyLedgerRepository.findByDateWithFinancialYear(date);
            DailyLedger dailyLedger;

            if (dailyLedgerOpt.isPresent()) {
                dailyLedger = dailyLedgerOpt.get();
                log.debug("Found existing daily ledger with ID: {} for date: {}",
                         dailyLedger.getDailyLedgerId(), date);
            } else {
                // Auto-create daily ledger if it doesn't exist
                log.info("Daily ledger not found for date: {}. Creating new daily ledger.", date);
                dailyLedger = createDailyLedgerForDate(date);
            }

            // Get paginated ledger details
            Page<LedgerDetails> ledgerDetailsPage = dailyLedgerRepository
                    .findLedgerDetailsByDateWithPagination(date, pageable);

            // Set the paginated ledger details to the daily ledger
            dailyLedger.setLedgerDetails(ledgerDetailsPage.getContent());

            // Initialize the records collection for each ledger detail to avoid lazy loading
            for (LedgerDetails ledgerDetail : dailyLedger.getLedgerDetails()) {
                if (ledgerDetail.getRecords() != null) {
                    ledgerDetail.getRecords().size();
                }
            }

            log.info("Successfully fetched daily ledger with {} ledger details for date: {}",
                    ledgerDetailsPage.getNumberOfElements(), date);

            return dailyLedger;
        } catch (Exception e) {
            log.error("Error fetching daily ledger with pagination for date: {}", date, e);
            throw new RuntimeException("Failed to fetch daily ledger with pagination for date: " + date, e);
        }
    }

    @Override
    public OptimizedDailyLedgerDTO getDailyLedgerOptimizedWithPagination(LocalDate date, Pageable pageable) {
        log.info("Fetching optimized daily ledger with pagination for date: {}, page: {}, size: {}",
                date, pageable.getPageNumber(), pageable.getPageSize());

        if (date == null) {
            log.error("Date parameter cannot be null");
            throw new IllegalArgumentException("Date parameter cannot be null");
        }

        if (pageable == null) {
            log.error("Pageable parameter cannot be null");
            throw new IllegalArgumentException("Pageable parameter cannot be null");
        }

        try {
            // First, get or create the daily ledger
            Optional<DailyLedger> dailyLedgerOpt = dailyLedgerRepository.findByDateWithFinancialYear(date);
            DailyLedger dailyLedger;

            if (dailyLedgerOpt.isPresent()) {
                dailyLedger = dailyLedgerOpt.get();
                log.debug("Found existing daily ledger with ID: {} for date: {}",
                         dailyLedger.getDailyLedgerId(), date);
            } else {
                // Auto-create daily ledger if it doesn't exist
                log.info("Daily ledger not found for date: {}. Creating new daily ledger.", date);
                dailyLedger = createDailyLedgerForDate(date);
            }

            // Convert to optimized DTO
            OptimizedDailyLedgerDTO optimizedDTO = OptimizedDailyLedgerDTO.builder()
                    .dailyLedgerId(dailyLedger.getDailyLedgerId())
                    .date(dailyLedger.getDate())
                    .financialYearId(dailyLedger.getFinancialYear() != null ?
                            dailyLedger.getFinancialYear().getYearId() : null)
                    .build();

            // Get paginated ledger details
            Page<LedgerDetails> ledgerDetailsPage = dailyLedgerRepository
                    .findLedgerDetailsByDateWithPagination(date, pageable);

            // Convert paginated ledger details to optimized DTOs
            List<OptimizedLedgerDetailsDTO> optimizedLedgerDetails = ledgerDetailsPage.getContent()
                    .stream()
                    .map(this::convertToOptimizedLedgerDetailsDTO)
                    .collect(Collectors.toList());

            optimizedDTO.setLedgerDetails(optimizedLedgerDetails);

            log.info("Successfully fetched optimized daily ledger with {} ledger details for date: {}",
                    ledgerDetailsPage.getNumberOfElements(), date);

            return optimizedDTO;
        } catch (Exception e) {
            log.error("Error fetching optimized daily ledger with pagination for date: {}", date, e);
            throw new RuntimeException("Failed to fetch optimized daily ledger with pagination for date: " + date, e);
        }
    }

    /**
     * Calculate transaction summary for daily ledger (same logic as LedgerDetailsServiceImpl)
     */
    private OptimizedLedgerDetailsDTO.TransactionSummaryDTO calculateTransactionSummaryForDailyLedger(List<LedgerRecord> records) {
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
