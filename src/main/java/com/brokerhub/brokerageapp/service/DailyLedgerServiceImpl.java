package com.brokerhub.brokerageapp.service;

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

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Logger;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

@Service
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
        Long dailyLedgerId = getDailyLedgerId(date);
        if(null != dailyLedgerId){
            Optional<DailyLedger> dailyLedger =  dailyLedgerRepository.findById(dailyLedgerId);
            return dailyLedger.get();
        }
        else{
            //TODO if daily ledger not exists then create one.
            return null;
        }
    }

    public DailyLedger getDailyLedgerOnDate(LocalDate date) throws FileNotFoundException {
        String fileName = date.toString()+" Records";
        String filePath = "C:\\Users\\HP\\Desktop\\Pdfs\\"+fileName+".pdf";
        Long dailyLedgerId = getDailyLedgerId(date);
//        if(null != dailyLedgerId){
            Optional<DailyLedger> dailyLedger =  dailyLedgerRepository.findById(dailyLedgerId);
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
}
