package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.*;
import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.helper.RazorPayHelper;
import com.brokerhub.brokerageapp.mapper.BrokerDTOMapper;
import com.brokerhub.brokerageapp.repository.BrokerRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import com.brokerhub.brokerageapp.repository.AddressRepository;
import com.brokerhub.brokerageapp.utils.OtpUtil;
import com.brokerhub.brokerageapp.utils.EmailUtil;
import com.brokerhub.brokerageapp.constants.Constants;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BrokerServiceImpl implements BrokerService{

    @Autowired
    BrokerRepository brokerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    BrokerBankDetailsService brokerBankDetailsService;

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

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    RazorPayHelper razorPayHelper;

    public ResponseEntity createBroker(BrokerDTO brokerDTO) throws IOException, InterruptedException {
        String brokerFirmName = brokerDTO.getBrokerageFirmName();
        String brokerEmail = brokerDTO.getEmail();
        String brokerPhoneNumber = brokerDTO.getPhoneNumber();
        if(!brokerRepository.existsByBrokerageFirmName(brokerFirmName) && !brokerRepository.findByEmail(brokerEmail).isPresent() && !brokerRepository.findByPhoneNumber(brokerPhoneNumber).isPresent()) {
            Broker broker = brokerDTOMapper.convertBrokerDTOtoBroker(brokerDTO);
            broker.setTotalBrokerage(BigDecimal.valueOf(0));
            BrokersAddress address = addressService.findBrokersAddressByPincode(brokerDTO.getPincode());
            if(null != address){
                broker.setAddress(address);
            }
            if(null != brokerDTO.getAccountNumber() && null!=brokerDTO.getIfscCode()){
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
        if(brokerBankDetailsService.ifBrokerBankDetailsExists(brokerDTO.getAccountNumber())) {
            BrokerBankDetails bankAccount = brokerBankDetailsService.getBrokerBankDetailsByAccountNumber(brokerDTO.getAccountNumber());
            broker.setBankDetails(bankAccount);
        }
        else {
            BrokerBankDetails bankDetails = new BrokerBankDetails();
            BankDetailsDTO bankDetailsDTO;
            try{
                bankDetailsDTO = razorPayHelper.fetchBankDetails(brokerDTO.getIfscCode());
            }catch (Exception e){
                bankDetailsDTO = null;
            }
            if(null != bankDetailsDTO) {
                mapBankDetails(bankDetailsDTO, bankDetails, brokerDTO.getAccountNumber(), brokerDTO.getIfscCode());
            }else{
                if (StringUtils.hasText(brokerDTO.getAccountNumber())) {
                    bankDetails.setAccountNumber(brokerDTO.getAccountNumber());
                }

                if (StringUtils.hasText(brokerDTO.getIfscCode())) {
                    bankDetails.setIfscCode(brokerDTO.getIfscCode());
                }
            }
            brokerBankDetailsService.createBrokerBankDetails(bankDetails);
            broker.setBankDetails(bankDetails);
        }
    }

    public void mapBankDetails(BankDetailsDTO bankDetailsDTO, BrokerBankDetails bankDetails, String accountNumber, String ifscCode) {
        // Set required fields (with basic null/empty validation)
        if (StringUtils.hasText(accountNumber)) {
            bankDetails.setAccountNumber(accountNumber);
        }

        if (StringUtils.hasText(ifscCode)) {
            bankDetails.setIfscCode(ifscCode);
        }

        if (StringUtils.hasText(bankDetailsDTO.getBankName())) {
            bankDetails.setBankName(bankDetailsDTO.getBankName());
        }

        if (StringUtils.hasText(bankDetailsDTO.getBankAddress())) {
            bankDetails.setBankAddress(bankDetailsDTO.getBankAddress());
        }

        if (StringUtils.hasText(bankDetailsDTO.getBankCode())) {
            bankDetails.setBankCode(bankDetailsDTO.getBankCode());
        }

        if (StringUtils.hasText(bankDetailsDTO.getBankContact())) {
            bankDetails.setBankContact(bankDetailsDTO.getBankContact());
        }

        if (StringUtils.hasText(bankDetailsDTO.getBranch())) {
            bankDetails.setBranch(bankDetailsDTO.getBranch());
        }

        if (StringUtils.hasText(bankDetailsDTO.getMICR())) {
            bankDetails.setMICR(bankDetailsDTO.getMICR());
        }

        // Set Boolean flags (no null check needed unless you want to preserve existing values)
        if (bankDetailsDTO.getIMPS() != null) {
            bankDetails.setIMPS(bankDetailsDTO.getIMPS());
        }

        if (bankDetailsDTO.getNEFT() != null) {
            bankDetails.setNEFT(bankDetailsDTO.getNEFT());
        }

        if (bankDetailsDTO.getRTGS() != null) {
            bankDetails.setRTGS(bankDetailsDTO.getRTGS());
        }

        if (bankDetailsDTO.getUPI() != null) {
            bankDetails.setUPI(bankDetailsDTO.getUPI());
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
        List<User> users = userService.getAllUserDetails();
        BigDecimal total = BigDecimal.valueOf(0);
        for(User user : users){
            total.add(user.getTotalPayableBrokerage());
        }
        return total;
        //TODO is it fine or should we get from after iterating all daily ledgers
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

    public String verifyAccount(String userName, Integer otp) {
        boolean brokerExists = brokerRepository.findByUserName(userName).isPresent();
        if(brokerExists){
            Optional<Broker> brokerObj = brokerRepository.findByUserName(userName);
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
                    brokerRepository.save(broker);
                    return "OTP verified you can change password now";
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
            emailUtil.verifyEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        broker.setOtp(otp);
        broker.setOtpGeneratedTime(LocalDateTime.now());
        brokerRepository.save(broker);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email sent... please verify account within 1 minute");
    }

    public ResponseEntity<String> changePassword(ResetPasswordDTO resetPasswordDTO) {
        Broker broker = brokerRepository.findByEmail(resetPasswordDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Broker not found with this email: " + resetPasswordDTO.getEmail()));
        String oldPassword = resetPasswordDTO.getOldPassword();
        String newPassword = resetPasswordDTO.getPassword();
        Boolean isPasswordCorrect = passwordEncoder.matches(oldPassword, broker.getPassword());
        if(isPasswordCorrect){
                broker.setPassword(passwordEncoder.encode(newPassword));
                brokerRepository.save(broker);
               return  ResponseEntity.status(HttpStatus.CREATED).body("Password changed successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is wrong");
        }
    }

    public ResponseEntity<String> createPassword(CreatePasswordDTO createPasswordDTO) {
        Broker broker = brokerRepository.findByEmail(createPasswordDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Broker not found with this email: " + createPasswordDTO.getEmail()));
        String newPassword = createPasswordDTO.getPassword();
        broker.setPassword(passwordEncoder.encode(newPassword));
        brokerRepository.save(broker);
        return ResponseEntity.status(HttpStatus.CREATED).body("Password created");
    }

    @Autowired
    private com.brokerhub.brokerageapp.security.JwtUtil jwtUtil;

    @Autowired
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    public ResponseEntity<?> login(BrokerLoginDTO brokerLoginDTO){
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    brokerLoginDTO.getUserName(), 
                    brokerLoginDTO.getPassword()
                )
            );

            // Get broker details
            Broker broker = brokerRepository.findByUserName(brokerLoginDTO.getUserName())
                .orElseThrow(() -> new RuntimeException("Broker not found"));

            // Generate JWT token
            String token = jwtUtil.generateToken(broker.getUserName(), broker.getBrokerId());

            // Create response
            AuthResponseDTO response = AuthResponseDTO.builder()
                .token(token)
                .username(broker.getUserName())
                .brokerId(broker.getBrokerId())
                .brokerName(broker.getBrokerName())
                .message("Login successful")
                .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
        }
    }

    public String generatePasswordHash(String password) {
        return passwordEncoder.encode(password);
    }

    public ResponseEntity<String> resetAdminPassword(String newPassword) {
        try {
            Broker admin = brokerRepository.findByUserName("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

            String hashedPassword = passwordEncoder.encode(newPassword);
            admin.setPassword(hashedPassword);
            brokerRepository.save(admin);

            return ResponseEntity.ok("Admin password reset successfully. New password: " + newPassword);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to reset password: " + e.getMessage());
        }
    }

    private Address findOrCreateAddressForBroker(String pincode, Broker broker) {
        // During broker creation, we can't use tenant context, so we'll find any address with this pincode
        // or return null to let the system handle it
        return addressRepository.findByPincode(pincode);
    }

    public Boolean findBrokerUserNameAvailability(String userName){
        return brokerRepository.existsByUserName(userName);
    }

    public Boolean findBrokerFirmNameAvailability(String FirmName){
        return brokerRepository.existsByBrokerageFirmName(FirmName);
    }

}
