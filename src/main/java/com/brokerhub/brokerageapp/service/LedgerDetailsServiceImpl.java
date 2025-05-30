package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.*;
import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    BrokerRepository brokerRepository;

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
        List<LedgerDetails> ledgerDetails = ledgerDetailsRepository.findAll();
        if(ledgerDetails.size()>0){
            return ledgerDetails;
        }
        return null;
    }


    public LedgerDetails getLedgerDetailById(Long ledgerDetailId, Long brokerId) {
        Optional<LedgerDetails> ledgerOptional = ledgerDetailsRepository.findById(ledgerDetailId);
        if(ledgerOptional.isPresent()){
            return ledgerOptional.get();
        }
        return null;
    }

    public List<DisplayLedgerDetailDTO> getAllLedgerDetailsOnDate(LocalDate date, Long brokerId) {
        List<DisplayLedgerDetailDTO> ledgerDetailsDTOList = new ArrayList<>();

        List<DateLedgerRecordDTO> ledgerRecordsOnDate = ledgerDetailsRepository.findLedgersOnDate(date);

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

}
