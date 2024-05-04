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

import java.math.BigDecimal;
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
        User seller = null;
        if(null!=sellerId){
            seller = userRepository.findById(sellerId).get();
        }

        LedgerDetails ledgerDetails = new LedgerDetails();
        if(dailyLedger != null){
            ledgerDetails.setDailyLedger(dailyLedger);
        }
        if(seller != null){
            ledgerDetails.setFromSeller(seller);
        }
        List<LedgerRecordDTO> ledgerRecordDTOList = ledgerDetailsDTO.getLedgerRecordDTOList();
        Long totalBags = 0L;
        for(int i=0; i<ledgerRecordDTOList.size(); i++){
            Long brokerage = ledgerRecordDTOList.get(i).getBrokerage();
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
            //TODO
            // need to get broker and update brokerage
            userRepository.save(seller);
            userRepository.save(buyer);
            ledgerDetailsRepository.save(ledgerDetails);
            ledgerRecordRepository.save(ledgerRecord);
        }
        seller.setTotalBagsSold(seller.getTotalBagsSold()+totalBags);
        userRepository.save(seller);
        ledgerDetailsRepository.save(ledgerDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully");
    }
}
