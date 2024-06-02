package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.UserDTO;
import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.BankDetails;
import com.brokerhub.brokerageapp.entity.Miller;
import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.mapper.UserDTOMapper;
import com.brokerhub.brokerageapp.repository.AddressRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import com.brokerhub.brokerageapp.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserDTOMapper userDTOMapper;

    @Autowired
    BankDetailsService bankDetailsService;

    @Autowired
    AddressService addressService;



    public ResponseEntity createUser(UserDTO userDTO) {
        String firmName = userDTO.getFirmName();
        String GSTNumber = userDTO.getGstNumber();
        if(!checkUserFirmExists(firmName) || !checkUserGSTNumberExists(GSTNumber)) {
            User user = userDTOMapper.convertUserDTOtoUser(userDTO);
            //User user = new User();
            user.setPayableAmount(0L);
            user.setReceivableAmount(0L);
            user.setTotalBagsBought(0L);
            user.setTotalBagsSold(0L);
            user.setTotalPayableBrokerage(BigDecimal.valueOf(0));
            if(userDTO.getUserType().equalsIgnoreCase(Constants.USER_TYPE_MILLER)){
                user.setUserType("MILLER");
                Miller miller = new Miller();
                miller.setByProduct(userDTO.getByProduct());
            }
            else{
                user.setUserType("TRADER");
            }
            //Address address = findAddressByCityArea(userDTO.getCity(),userDTO.getArea());
            Address address = addressService.findAddressByPincode(userDTO.getPincode());
            if(null != address) {
                user.setAddress(address);
            }
            linkBankDetailsToUser(userDTO,user);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User already exists");
        }

    }


    public User updateUser(User user) {
        Address address = addressService.findAddressByPincode(user.getAddress().getPincode());
        user.setAddress(address);
        BankDetails bankDetails = bankDetailsService.getBankDetailsByAccountNumber(user.getBankDetails().getAccountNumber());
        user.setBankDetails(bankDetails);
        return userRepository.save(user);
    }

    @Override
    public ResponseEntity<String> deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(!user.isEmpty()) {
            String firmName = user.get().getFirmName();
            userRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(firmName+" - deleted successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no user found");
        }
    }

    public List<User> getAllUsers(Pageable pageable) {
        List<User> allUsers = userRepository.findAll(pageable).getContent();
        if(allUsers.size()>=1){
            return allUsers;
        }
        else{
            return null;
        }
    }

    public List<User> getAllUserDetails() {
        List<User> allUsers = userRepository.findAll();
        if(allUsers.size()>=1){
            return allUsers;
        }
        else{
            return null;
        }
    }

    public Object getAllUsersByCity(String city) {
        boolean isCityExists = addressService.isCityExists(city);
        if (!isCityExists) {
            return "City does not exist";
        }

        List<User> usersInCity = userRepository.findByAddressCity(city);
        return usersInCity.isEmpty() ? Collections.emptyList() : usersInCity;
    }

    public Optional<User> getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if(!user.isEmpty()){
            return user;
        }
        return null;
    }

    public List<User> getAllUsersHavingBrokerageMoreThan(int brokerage) {
        List<User> usersHavingBrokerage = userRepository.findByTotalPayableBrokerageGreaterThanEqual(brokerage);
        if(usersHavingBrokerage.size()>=1){
            return usersHavingBrokerage;
        }
        return null;
    }

    public List<User> getAllUsersHavingBrokerageInRange(int min, int max) {
        List<User> usersHavingBrokerageInRange = userRepository.findByTotalPayableBrokerageBetween(min,max);
        if(usersHavingBrokerageInRange.size()>=1){
            return usersHavingBrokerageInRange;
        }
        return null;
    }

    public Object getUserByProperty(String property, String value) {
        if(property.equalsIgnoreCase(Constants.USER_PROPERTY_FIRM_NAME)){
            Optional<User> user = userRepository.findByFirmName(value);
            return user.isEmpty()? null : user;
        }
        else if(property.equalsIgnoreCase(Constants.USER_PROPERTY_GST_NUMBER)){
            Optional<User> user = userRepository.findByGstNumber(value);
            return user.isEmpty()? null : user;
        }
        else{
            return HttpStatus.NOT_FOUND;
        }
    }

    public List<HashMap<String, Long>> getUserNamesAndIds() {
        //TODO
        // need to optimise this everytime when user clicks search bar we are taking all users and iterating all which will take lot of time
        List<HashMap<String,Long>> UserNamesAndIds = null;
        List<User> allUsers = userRepository.findAll();
        for(int i=0; i<allUsers.size(); i++){
            HashMap<String,Long> UserInfo = new HashMap<>();
            UserInfo.put(allUsers.get(i).getFirmName(),allUsers.get(i).getUserId());
            UserNamesAndIds.add(UserInfo);
        }
        return UserNamesAndIds;
    }

    public List<String> getUserNames() {
        //TODO
        // need to optimise this everytime when user clicks search bar we are taking all users and iterating all which will take lot of time
        List<String> UserNames = null;
        List<User> allUsers = userRepository.findAll();
        for(int i=0; i<allUsers.size(); i++){
            UserNames.add(allUsers.get(i).getFirmName());
        }
        return UserNames;
    }


    private boolean checkUserGSTNumberExists(String gstNumber) {
        Optional<User> user = userRepository.findByGstNumber(gstNumber);
        if(user.isEmpty()){
            return false;
        }
        return true;
    }

    private void linkBankDetailsToUser(UserDTO userDTO,User user) {
        if(bankDetailsService.ifBankDetailsExists(userDTO.getAccountNumber())) {
            BankDetails bankAccount = bankDetailsService.getBankDetailsByAccountNumber(userDTO.getAccountNumber());
            user.setBankDetails(bankAccount);
        }
        else {
            BankDetails bankDetails = new BankDetails();
            bankDetails.setBankName(userDTO.getBankName());
            bankDetails.setAccountNumber(userDTO.getAccountNumber());
            bankDetails.setBranch(userDTO.getBranch());
            bankDetails.setIfscCode(userDTO.getIfscCode());
            bankDetailsService.createBankDetails(bankDetails);
            user.setBankDetails(bankDetails);
        }
    }

    private Long findAddressIDByCityArea(String city, String area) {
        Long addressID = addressRepository.findAddressIdByCityAndArea(city,area);
        return addressID;
    }

    private Address findAddressByCityArea(String city, String area) {
        Address address = addressRepository.findByCityAndArea(city,area);
        return address;
    }

    public boolean checkUserFirmExists(String firmName){
        Optional<User> user = userRepository.findByFirmName(firmName);
        System.out.println(user);
        if(user.isEmpty()){
            return false;
        }
        return true;

    }
}
