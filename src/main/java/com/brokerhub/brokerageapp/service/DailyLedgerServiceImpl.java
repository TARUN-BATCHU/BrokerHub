package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.*;
import com.brokerhub.brokerageapp.entity.DailyLedger;
import com.brokerhub.brokerageapp.entity.FinancialYear;
import com.brokerhub.brokerageapp.entity.LedgerDetails;
import com.brokerhub.brokerageapp.entity.LedgerRecord;
import com.brokerhub.brokerageapp.repository.DailyLedgerRepository;
import com.brokerhub.brokerageapp.repository.FinancialYearRepository;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

@Service
@Transactional
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
        if(null != dailyLedgerRepository.findByDate(date)){
            return dailyLedgerRepository.findByDate(date).getDailyLedgerId();
        }
        else{
            //TODO if daily ledger not exists then create one.
            return null;
        }
    }

    public DailyLedger getDailyLedger(LocalDate date) {
        // Use the new method that eagerly fetches ledgerDetails
        Optional<DailyLedger> dailyLedgerOpt = dailyLedgerRepository.findByDateWithLedgerDetails(date);
        if(dailyLedgerOpt.isPresent()){
            DailyLedger dailyLedger = dailyLedgerOpt.get();
            // Initialize the records collection for each ledger detail to avoid lazy loading
            if(dailyLedger.getLedgerDetails() != null) {
                for(LedgerDetails ledgerDetail : dailyLedger.getLedgerDetails()) {
                    // Force initialization of the records collection
                    if(ledgerDetail.getRecords() != null) {
                        ledgerDetail.getRecords().size();
                    }
                }
            }
            return dailyLedger;
        }
        else{
            //TODO if daily ledger not exists then create one.
            return null;
        }
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

        // Convert seller info
        if (ledgerDetails.getFromSeller() != null) {
            OptimizedUserDTO sellerDTO = OptimizedUserDTO.builder()
                    .userId(ledgerDetails.getFromSeller().getUserId())
                    .firmName(ledgerDetails.getFromSeller().getFirmName())
                    .addressId(ledgerDetails.getFromSeller().getAddress() != null ?
                            ledgerDetails.getFromSeller().getAddress().getAddressId() : null)
                    .build();
            dto.setFromSeller(sellerDTO);
        }

        // Convert records
        if (ledgerDetails.getRecords() != null) {
            // Force initialization of the records collection
            ledgerDetails.getRecords().size();

            List<OptimizedLedgerRecordDTO> optimizedRecords =
                    ledgerDetails.getRecords().stream()
                            .map(this::convertToOptimizedLedgerRecordDTO)
                            .collect(Collectors.toList());
            dto.setRecords(optimizedRecords);
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
}
