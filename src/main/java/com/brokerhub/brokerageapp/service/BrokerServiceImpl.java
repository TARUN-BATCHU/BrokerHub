package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BrokerServiceImpl implements BrokerService{

    @Autowired
    BrokerRepository brokerRepository;

    @Autowired
    UserRepository userRepository;

    public ResponseEntity createBroker(Broker broker) {
        String brokerFirmName = broker.getBrokerageFirmName();
        String brokerEmail = broker.getEmail();
        String brokerPhoneNumber = broker.getPhoneNumber();
        if(!brokerRepository.findByBrokerageFirmName(brokerFirmName).isPresent() && !brokerRepository.findByEmail(brokerEmail).isPresent() && !brokerRepository.findByPhoneNumber(brokerPhoneNumber).isPresent()) {
            brokerRepository.save(broker);
            return ResponseEntity.status(HttpStatus.CREATED).body("Broker account successfully");
        }
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Broker already exists");
    }

    public Broker updateBroker(Broker broker) {
        return brokerRepository.save(broker);
    }

    public ResponseEntity deleteBroker(Long brokerId) {
        Boolean brokerExists = brokerRepository.findById(brokerId).isPresent();
        if(brokerExists){
            brokerRepository.deleteById(brokerId);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no broker exists");
    }

    public Optional<Broker> findBrokerById(Long brokerId) {
        Optional<Broker> broker = brokerRepository.findById(brokerId);
        if(broker.isPresent()){
            return broker;
        }
        return null;
    }

    public BigDecimal calculateTotalBrokerage(Long brokerId) {
        //TODO
        //after implementation of daily ledger
        return null;
    }

    public BigDecimal getTotalBrokerage(Long brokerId) {
        BigDecimal brokerageAmount = brokerRepository.findById(brokerId).get().getTotalBrokerage();
        return brokerageAmount;
    }

    public BigDecimal getTotalBrokerageFromCity(Long brokerId, String city) {
        BigDecimal totalBrokerageFromCity = BigDecimal.valueOf(0);
        //currently checking for only 1 broker so not considering brokerId
        totalBrokerageFromCity = brokerRepository.findTotalBrokerageOfCity(city);
        return totalBrokerageFromCity;
    }

    public BigDecimal getTotalBrokerageOfUser(Long brokerId, Long userId) {
        if(null != brokerId && null!=userId && brokerRepository.findById(brokerId).isPresent() && userRepository.findById(userId).isPresent()){
            //currently checking for only one broker
            // we need to take the brokerage of particular broker then we will consider brokerId also
            BigDecimal totalPayableBrokerageByUser = userRepository.findById(userId).get().getTotalPayableBrokerage();
            return totalPayableBrokerageByUser;
        }
        return null;
    }

    public BigDecimal findBrokerageFromProduct(Long brokerId, Long productId) {
        //TODO
        //after implementation of ledger
        return null;
    }
}
