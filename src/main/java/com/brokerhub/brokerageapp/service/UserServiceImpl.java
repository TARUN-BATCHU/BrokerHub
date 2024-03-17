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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
            user.setTotalPayableBrokerage(0L);
            if(userDTO.getUserType().equalsIgnoreCase(Constants.USER_TYPE_MILLER)){
                user.setUserType(Constants.USER_TYPE_MILLER);
                Miller miller = new Miller();
                miller.setByProduct(userDTO.getByProduct());
            }
            else{
                user.setUserType(Constants.USER_TYPE_TRADER);
            }
            Address address = findAddressByCityArea(userDTO.getCity(),userDTO.getArea());
            user.setAddress(address);
            linkBankDetailsToUser(userDTO,user);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User already exists");
        }

    }

    private boolean checkUserGSTNumberExists(String gstNumber) {
        Optional<User> user = userRepository.findByGstNumber(gstNumber);
        if(user.isEmpty()){
            return false;
        }
           return true;
    }

    private ResponseEntity<String> linkBankDetailsToUser(UserDTO userDTO,User user) {
        BankDetails bankDetails = new BankDetails();
        bankDetails.setBankName(userDTO.getBankName());
        bankDetails.setAccountNumber(userDTO.getAccountNumber());
        bankDetails.setBranch(userDTO.getBranch());
        bankDetails.setIsfcCode(userDTO.getIsfcCode());
        ResponseEntity<String> responseEntity = bankDetailsService.createBankDetails(bankDetails);
        user.setBankDetails(bankDetails);
        return responseEntity;
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
