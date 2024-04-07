package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BrokerDTO;
import com.brokerhub.brokerageapp.dto.UpdateBrokerDTO;
import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BankDetails;
import com.brokerhub.brokerageapp.entity.Broker;
import com.brokerhub.brokerageapp.mapper.BrokerDTOMapper;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import com.brokerhub.brokerageapp.utils.OtpUtil;
import com.brokerhub.brokerageapp.utils.EmailUtil;
import com.brokerhub.brokerageapp.constants.Constants;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BrokerServiceImpl implements BrokerService{

    @Autowired
    BrokerRepository brokerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    BankDetailsService bankDetailsService;

    @Autowired
    BrokerDTOMapper brokerDTOMapper;

    @Autowired
    AddressService addressService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OtpUtil otpUtil;

    @Autowired
    EmailUtil emailUtil;

    public ResponseEntity createBroker(BrokerDTO brokerDTO) {
        String brokerFirmName = brokerDTO.getBrokerageFirmName();
        String brokerEmail = brokerDTO.getEmail();
        String brokerPhoneNumber = brokerDTO.getPhoneNumber();
        if(!brokerRepository.findByBrokerageFirmName(brokerFirmName).isPresent() && !brokerRepository.findByEmail(brokerEmail).isPresent() && !brokerRepository.findByPhoneNumber(brokerPhoneNumber).isPresent()) {
            Broker broker = brokerDTOMapper.convertBrokerDTOtoBroker(brokerDTO);
            broker.setTotalBrokerage(BigDecimal.valueOf(0));
            Address address = addressService.findAddressByPincode(brokerDTO.getPincode());
            if(null != address){
                broker.setAddress(address);
            }
            if(null != brokerDTO.getAccountNumber() && null!= brokerDTO.getBranch() && null!=brokerDTO.getBankName() && null!=brokerDTO.getIfscCode()){
                linkBankDetailsToBroker(brokerDTO,broker);
            }
            broker.setPassword(passwordEncoder.encode(brokerDTO.getPassword()));
            broker.setOtp(null);
            broker.setOtpGeneratedTime(null);
            brokerRepository.save(broker);
            return ResponseEntity.status(HttpStatus.CREATED).body("Broker account successfully created");
        }
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Broker already exists");
    }

    private void linkBankDetailsToBroker(BrokerDTO brokerDTO, Broker broker) {
        if(bankDetailsService.ifBankDetailsExists(brokerDTO.getAccountNumber())) {
            BankDetails bankAccount = bankDetailsService.getBankDetailsByAccountNumber(brokerDTO.getAccountNumber());
            broker.setBankDetails(bankAccount);
        }
        else {
            BankDetails bankDetails = new BankDetails();
            bankDetails.setBankName(brokerDTO.getBankName());
            bankDetails.setAccountNumber(brokerDTO.getAccountNumber());
            bankDetails.setBranch(brokerDTO.getBranch());
            bankDetails.setIfscCode(brokerDTO.getIfscCode());
            bankDetailsService.createBankDetails(bankDetails);
            broker.setBankDetails(bankDetails);
        }
    }

    public ResponseEntity updateBroker(UpdateBrokerDTO updateBrokerDTO) {
        Optional<Broker> broker = brokerRepository.findById(updateBrokerDTO.getBrokerId());
        if(broker.isPresent()){
            broker.get().setBrokerName(updateBrokerDTO.getBrokerName());
            broker.get().setTotalBrokerage(updateBrokerDTO.getTotalBrokerage());
            broker.get().setEmail(updateBrokerDTO.getEmail());
            broker.get().setBrokerageFirmName(updateBrokerDTO.getBrokerageFirmName());
            broker.get().setPhoneNumber(updateBrokerDTO.getPhoneNumber());
            broker.get().setUserName(updateBrokerDTO.getUserName());
            brokerRepository.save(broker.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("broker details updated");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no broker found");
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
        if (null != brokerId && brokerRepository.findById(brokerId).isPresent() && null != city && addressService.isCityExists(city)) {
            BigDecimal totalBrokerageFromCity = BigDecimal.valueOf(0);
            //currently checking for only 1 broker so not considering brokerId
            totalBrokerageFromCity = brokerRepository.findTotalBrokerageOfCity(city);
            return totalBrokerageFromCity;
        }
        return null;
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

    public ResponseEntity<String> forgetPassword(String userName) {
        Optional<Broker> brokerObj = brokerRepository.findByUserName(userName);
        if(brokerObj.isPresent()){
            Broker broker = brokerObj.get();
            Integer OTP = otpUtil.generateOtp();
            broker.setOtp(OTP);
            broker.setOtpGeneratedTime(LocalDateTime.now());
            try{
                emailUtil.sendOtpToEmail(broker.getEmail(),OTP);
            }catch (MessagingException e){
                throw new RuntimeException("Unable to send otp please try again");
            }
            brokerRepository.save(broker);
            return ResponseEntity.ok().body("OTP send successfully");
        }
        return ResponseEntity.badRequest().body("No user found with provided user name");
    }

    public String verifyAccount(String email, Integer otp) {
        boolean brokerExists = brokerRepository.findByEmail(email).isPresent();
        if(brokerExists){
            Optional<Broker> brokerObj = brokerRepository.findByEmail(email);
            Broker broker = brokerObj.get();
//            Integer otpInDatabase = broker.getOtp();
//            Integer otpProvided = otp;
//            LocalDateTime timeOtpCreated = broker.getOtpGeneratedTime();
//            LocalDateTime timeNow = LocalDateTime.now();
//            Long seconds = Duration.between(broker.getOtpGeneratedTime(),LocalDateTime.now()).getSeconds();
//            Boolean otpAlive = Duration.between(broker.getOtpGeneratedTime(),LocalDateTime.now()).getSeconds()<(Constants.OTP_SPAN*60);
            if (broker.getOtp().toString().equalsIgnoreCase(otp.toString())) {
                if (Duration.between(broker.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < (Constants.OTP_SPAN * 60)) {
                    broker.setOtp(null);
                    broker.setOtpGeneratedTime(null);
                    return "OTP verified you can login";
                }
                else {
                    return "Please regenerate OTP";
                }
            } else {
                return "OTP wrong";
            }
        }
        return "Broker not exists";
    }

    public ResponseEntity<String> regenerateOTP(String email) {
        Broker broker = brokerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Broker not found with this email: " + email));
        Integer otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpToEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        broker.setOtp(otp);
        broker.setOtpGeneratedTime(LocalDateTime.now());
        brokerRepository.save(broker);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email sent... please verify account within 1 minute");

    }
}
