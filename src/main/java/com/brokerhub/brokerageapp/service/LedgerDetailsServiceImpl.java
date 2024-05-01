package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.LedgerDetailsDTO;
import com.brokerhub.brokerageapp.dto.LedgerRecordDTO;
import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.repository.LedgerDetailsRepository;
import com.brokerhub.brokerageapp.repository.LedgerRecordRepository;
import com.brokerhub.brokerageapp.repository.ProductRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
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

    public ResponseEntity<String> createLedgerDetails(LedgerDetailsDTO ledgerDetailsDTO) {
        LocalDate date = ledgerDetailsDTO.getDate();
        DailyLedger dailyLedger = dailyLedgerService.getDailyLedger(date);
        Long sellerId = ledgerDetailsDTO.getFromSeller();
        Optional<User> seller = null;
        if(null!=sellerId){
            seller = userRepository.findById(sellerId);
        }

        LedgerDetails ledgerDetails = new LedgerDetails();
        if(dailyLedger != null){
            ledgerDetails.setDailyLedger(dailyLedger);
        }
        if(seller != null){
            ledgerDetails.setFromSeller(seller.get());
        }
        List<LedgerRecordDTO> ledgerRecordDTOList = ledgerDetailsDTO.getLedgerRecordDTOList();
        for(int i=0; i<ledgerRecordDTOList.size(); i++){
            int brokerage = ledgerRecordDTOList.get(i).getBrokerage();
            int quantity = ledgerRecordDTOList.get(i).getQuantity();
            int productCost = ledgerRecordDTOList.get(i).getProductCost();
            LedgerRecord ledgerRecord = new LedgerRecord();
            ledgerRecord.setLedgerDetails(ledgerDetails);
            ledgerRecord.setBrokerage(brokerage);

            String productName = ledgerRecordDTOList.get(i).getProductName();
            Integer productQuantity = ledgerRecordDTOList.get(i).getProductQuantity();
            String productQuality = ledgerRecordDTOList.get(i).getProductQuality();
            Product product = (Product) productRepository.findByProductNameAndQualityAndQuantity(productName,productQuality,productQuantity);
            if(product != null) {
                ledgerRecord.setProduct(product);
            }

            Optional<User> buyer = userRepository.findByFirmName(ledgerRecordDTOList.get(i).getBuyerName());
            if(buyer != null) {
                ledgerRecord.setToBuyer(buyer.get());
            }
            ledgerRecord.setQuantity(quantity);
            ledgerRecord.setProductCost(productCost);
            ledgerRecord.setTotalBrokerage(brokerage*quantity);
            ledgerRecord.setTotalProductsCost(productCost*quantity);
            ledgerRecordRepository.save(ledgerRecord);
        }

        ledgerDetailsRepository.save(ledgerDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully");
    }
}
