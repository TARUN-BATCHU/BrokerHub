package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.BulkUploadResponseDTO;
import com.brokerhub.brokerageapp.dto.UserDTO;
import com.brokerhub.brokerageapp.dto.UserSummaryDTO;
import com.brokerhub.brokerageapp.entity.*;
import com.brokerhub.brokerageapp.mapper.UserDTOMapper;
import com.brokerhub.brokerageapp.repository.AddressRepository;
import com.brokerhub.brokerageapp.repository.UserRepository;
import com.brokerhub.brokerageapp.constants.Constants;
import com.brokerhub.brokerageapp.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserDTOMapper userDTOMapper;

    @Autowired
    MerchantBankDetailsService merchantBankDetailsService;

    @Autowired
    AddressService addressService;

    @Autowired
    UserCacheService userCacheService;

    @Autowired
    TenantContextService tenantContextService;

    public ResponseEntity createUser(UserDTO userDTO) {
        String firmName = userDTO.getFirmName();
        String GSTNumber = userDTO.getGstNumber();

        // Get current broker for multi-tenant isolation
        Broker currentBroker = tenantContextService.getCurrentBroker();

        if(!checkUserFirmExists(firmName) && !checkUserGSTNumberExists(GSTNumber)) {
            User user = userDTOMapper.convertUserDTOtoUser(userDTO);
            //User user = new User();
            if(user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to convert UserDTO to User");
            }

            // Set the broker for multi-tenant isolation
            user.setBroker(currentBroker);

            user.setPayableAmount(0L);
            user.setReceivableAmount(0L);
            user.setTotalBagsBought(0L);
            user.setTotalBagsSold(0L);
            user.setTotalPayableBrokerage(BigDecimal.valueOf(0));

            // Handle address creation - create address if city is provided (mandatory field)
            if(userDTO.getCity() != null && !userDTO.getCity().trim().isEmpty()) {
                Address address = null;
                
                // Try to find existing address by pincode if provided
                if(userDTO.getPincode() != null && !userDTO.getPincode().trim().isEmpty()) {
                    address = addressService.findAddressByPincode(userDTO.getPincode());
                }
                
                // If no existing address found, create new one
                if(address == null) {
                    address = new Address();
                    address.setCity(userDTO.getCity().trim());
                    if(userDTO.getArea() != null && !userDTO.getArea().trim().isEmpty()) {
                        address.setArea(userDTO.getArea().trim());
                    }
                    if(userDTO.getPincode() != null && !userDTO.getPincode().trim().isEmpty()) {
                        address.setPincode(userDTO.getPincode().trim());
                    }
                    // Save the address first
                    address = addressService.saveAddress(address);
                }
                user.setAddress(address);
            }

            // Save user first to get the ID
            user = userRepository.save(user);
            
            // Handle bank details creation after user is saved
            if(userDTO.getAccountNumber() != null && !userDTO.getAccountNumber().trim().isEmpty()) {
                MerchantBankDetails bankDetails = merchantBankDetailsService.getMerchantBankDetailsByAccountNumber(userDTO.getAccountNumber());
                if(bankDetails == null) {
                    // Create new bank details if not found
                    bankDetails = new MerchantBankDetails();
                    bankDetails.setAccountNumber(userDTO.getAccountNumber());
                    if(userDTO.getBankName() != null) bankDetails.setBankName(userDTO.getBankName());
                    if(userDTO.getIfscCode() != null) bankDetails.setIfscCode(userDTO.getIfscCode());
                    if(userDTO.getBranch() != null) bankDetails.setBranch(userDTO.getBranch());
                    // CRITICAL FIX: Set the user reference
                    bankDetails.setUser(user);
                    // Save the bank details
                    bankDetails = merchantBankDetailsService.saveMerchantBankDetails(bankDetails);
                }
                user.setBankDetails(bankDetails);
                // Update user with bank details reference
                user = userRepository.save(user);
            }

            if(userDTO.getUserType() != null && userDTO.getUserType().equalsIgnoreCase(Constants.USER_TYPE_MILLER)){
                user.setUserType("MILLER");
                Miller miller = new Miller();
                miller.setByProduct(userDTO.getByProduct());
            }
            else{
                user.setUserType("TRADER");
            }
            // Remove duplicate address handling - already handled above
            // linkBankDetailsToUser is now handled above
            // userRepository.save(user) is now handled above

            // Clear user caches after creating new user
            userCacheService.clearUserCaches();

            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User already exists");
        }

    }


    public User updateUser(User user) {
        if(user.getAddress() != null && user.getAddress().getPincode() != null) {
            Address address = addressService.findAddressByPincode(user.getAddress().getPincode());
            user.setAddress(address);
        }
        if(user.getBankDetails() != null && user.getBankDetails().getAccountNumber() != null) {
            MerchantBankDetails bankDetails = merchantBankDetailsService.getMerchantBankDetailsByAccountNumber(user.getBankDetails().getAccountNumber());
            user.setBankDetails(bankDetails);
        }

        User updatedUser = userRepository.save(user);

        // Clear user caches after updating user
        userCacheService.clearUserCaches();

        return updatedUser;
    }

    @Override
    public ResponseEntity<String> deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(!user.isEmpty()) {
            String firmName = user.get().getFirmName();
            userRepository.deleteById(id);

            // Clear user caches after deleting user
            userCacheService.clearUserCaches();

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
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        List<User> allUsers = userRepository.findByBrokerBrokerId(currentBrokerId);
        if(allUsers.size()>=1){
            return allUsers;
        }
        else{
            return null;
        }
    }

    public Object getAllUsersByCity(String city) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        boolean isCityExists = addressService.isCityExists(city);
        if (!isCityExists) {
            return "City does not exist";
        }

        List<User> usersInCity = userRepository.findByBrokerBrokerIdAndAddressCity(currentBrokerId, city);
        return usersInCity.isEmpty() ? Collections.emptyList() : usersInCity;
    }

    public Optional<User> getUserById(Long userId) {
        Optional<User> user = userRepository.findByIdWithDetails(userId);
        if(!user.isEmpty()){
            return user;
        }
        return Optional.empty();
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
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        if(property.equalsIgnoreCase(Constants.USER_PROPERTY_FIRM_NAME)){
            Optional<User> user = userRepository.findByBrokerBrokerIdAndFirmName(currentBrokerId, value);
            return user.isEmpty()? null : user;
        }
        else if(property.equalsIgnoreCase(Constants.USER_PROPERTY_GST_NUMBER)){
            Optional<User> user = userRepository.findByBrokerBrokerIdAndGstNumber(currentBrokerId, value);
            return user.isEmpty()? null : user;
        }
        else{
            return HttpStatus.NOT_FOUND;
        }
    }

    public List<HashMap<String, Long>> getUserNamesAndIds() {
        // Use optimized cache service instead of fetching all users
        return userCacheService.getUserNamesAndIds();
    }

    public List<String> getUserNames() {
        // Optimized: Use cache service with Redis caching (1 hour TTL)
        // This fetches only firm names from database instead of full User entities
        return userCacheService.getAllUserNames();
    }

    public List<HashMap<String, Object>> getFirmNamesIdsAndCities() {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        List<Object[]> results = userRepository.findUserIdsAndFirmNamesAndCitiesByBrokerId(currentBrokerId);
        
        return results.stream()
                .map(row -> {
                    HashMap<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", (Long) row[0]);
                    userInfo.put("firmName", (String) row[1]);
                    userInfo.put("city", (String) row[2]);
                    return userInfo;
                })
                .collect(Collectors.toList());
    }


    private boolean checkUserGSTNumberExists(String gstNumber) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        Optional<User> user = userRepository.findByBrokerBrokerIdAndGstNumber(currentBrokerId, gstNumber);
        if(user.isEmpty()){
            return false;
        }
        return true;
    }

    private void linkBankDetailsToUser(UserDTO userDTO, User user) {
        // Only create bank details if account number is provided
        if (userDTO.getAccountNumber() == null || userDTO.getAccountNumber().trim().isEmpty()) {
            return;
        }
        
        if(merchantBankDetailsService.ifMerchantBankDetailsExists(userDTO.getAccountNumber())) {
            MerchantBankDetails bankAccount = merchantBankDetailsService.getMerchantBankDetailsByAccountNumber(userDTO.getAccountNumber());
            user.setBankDetails(bankAccount);
        }
        else {
            MerchantBankDetails bankDetails = new MerchantBankDetails();
            bankDetails.setBankName(userDTO.getBankName());
            bankDetails.setAccountNumber(userDTO.getAccountNumber());
            bankDetails.setBranch(userDTO.getBranch());
            bankDetails.setIfscCode(userDTO.getIfscCode());
            // CRITICAL FIX: Set the user reference before saving
            bankDetails.setUser(user);
            // Set broker will be handled in the service
            merchantBankDetailsService.createMerchantBankDetails(bankDetails);
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
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        Optional<User> user = userRepository.findByBrokerBrokerIdAndFirmName(currentBrokerId, firmName);
        System.out.println(user);
        if(user.isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public BulkUploadResponseDTO bulkUploadUsers(MultipartFile file) {
        List<String> errorMessages = new ArrayList<>();
        int totalRecords = 0;
        int successfulRecords = 0;
        int failedRecords = 0;

        try {
            // Validate file format
            if (!ExcelUtil.hasExcelFormat(file)) {
                return BulkUploadResponseDTO.builder()
                        .totalRecords(0)
                        .successfulRecords(0)
                        .failedRecords(0)
                        .errorMessages(Arrays.asList("Please upload a valid Excel file (.xlsx)"))
                        .message("Invalid file format")
                        .build();
            }

            // Parse Excel file
            List<UserDTO> userDTOs = ExcelUtil.excelToUserDTOs(file.getInputStream());
            totalRecords = userDTOs.size();

            if (totalRecords == 0) {
                return BulkUploadResponseDTO.builder()
                        .totalRecords(0)
                        .successfulRecords(0)
                        .failedRecords(0)
                        .errorMessages(Arrays.asList("No valid user records found in the Excel file"))
                        .message("No data to process")
                        .build();
            }

            // Process each user
            for (int i = 0; i < userDTOs.size(); i++) {
                UserDTO userDTO = userDTOs.get(i);
                int rowNumber = i + 2; // +2 because Excel rows start from 1 and we skip header

                try {
                    // Validate mandatory fields with detailed logging
                    List<String> missingFields = new ArrayList<>();
                    if (userDTO.getUserType() == null || userDTO.getUserType().trim().isEmpty()) {
                        missingFields.add("userType (value: '" + userDTO.getUserType() + "')");
                    }
                    if (userDTO.getGstNumber() == null || userDTO.getGstNumber().trim().isEmpty()) {
                        missingFields.add("gstNumber (value: '" + userDTO.getGstNumber() + "')");
                    }
                    if (userDTO.getFirmName() == null || userDTO.getFirmName().trim().isEmpty()) {
                        missingFields.add("firmName (value: '" + userDTO.getFirmName() + "')");
                    }
                    if (userDTO.getCity() == null || userDTO.getCity().trim().isEmpty()) {
                        missingFields.add("city (value: '" + userDTO.getCity() + "')");
                    }
                    if (userDTO.getPhoneNumbers() == null || userDTO.getPhoneNumbers().isEmpty()) {
                        missingFields.add("phoneNumbers (value: " + userDTO.getPhoneNumbers() + ")");
                    }
                    if (userDTO.getBrokerageRate() == null) {
                        missingFields.add("brokerageRate (value: " + userDTO.getBrokerageRate() + ")");
                    }
                    
                    if (!missingFields.isEmpty()) {
                        errorMessages.add("Row " + rowNumber + ": Missing mandatory fields: " + String.join(", ", missingFields));
                        failedRecords++;
                        continue;
                    }
                    
                    // Validate email format if provided (optional)
                    if (userDTO.getEmail() != null && !userDTO.getEmail().trim().isEmpty()) {
                        if (!userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                            errorMessages.add("Row " + rowNumber + ": Invalid email format");
                            failedRecords++;
                            continue;
                        }
                    }
                    
                    // Validate and normalize user type if provided
                    if (userDTO.getUserType() != null && !userDTO.getUserType().trim().isEmpty()) {
                        String userType = userDTO.getUserType().trim().toUpperCase();
                        if (!userType.equals("TRADER") && !userType.equals("MILLER")) {
                            // Set to default instead of failing
                            userDTO.setUserType("TRADER");
                        } else {
                            userDTO.setUserType(userType);
                        }
                    }

                    // Check if user already exists
                    if (checkUserFirmExists(userDTO.getFirmName()) ||
                        (userDTO.getGstNumber() != null && checkUserGSTNumberExists(userDTO.getGstNumber()))) {
                        errorMessages.add("Row " + rowNumber + ": User with firm name '" +
                                        userDTO.getFirmName() + "' or GST number already exists");
                        failedRecords++;
                        continue;
                    }

                    // Set default values if not provided
                    if (userDTO.getUserType() == null || userDTO.getUserType().trim().isEmpty()) {
                        userDTO.setUserType("TRADER");
                    }

                    // Create user
                    ResponseEntity response = createUser(userDTO);
                    if (response.getStatusCode() == HttpStatus.CREATED) {
                        successfulRecords++;
                    } else {
                        errorMessages.add("Row " + rowNumber + ": " + response.getBody());
                        failedRecords++;
                    }

                } catch (Exception e) {
                    errorMessages.add("Row " + rowNumber + ": Error processing user - " + e.getMessage());
                    failedRecords++;
                }
            }

        } catch (IOException e) {
            return BulkUploadResponseDTO.builder()
                    .totalRecords(0)
                    .successfulRecords(0)
                    .failedRecords(0)
                    .errorMessages(Arrays.asList("Error reading Excel file: " + e.getMessage()))
                    .message("File processing failed")
                    .build();
        } catch (Exception e) {
            return BulkUploadResponseDTO.builder()
                    .totalRecords(totalRecords)
                    .successfulRecords(successfulRecords)
                    .failedRecords(failedRecords)
                    .errorMessages(Arrays.asList("Unexpected error: " + e.getMessage()))
                    .message("Bulk upload failed")
                    .build();
        }

        // Clear user caches if any users were successfully created
        if (successfulRecords > 0) {
            userCacheService.clearUserCaches();
        }

        // Prepare response message
        String message;
        if (failedRecords == 0) {
            message = "All users uploaded successfully";
        } else if (successfulRecords == 0) {
            message = "No users were uploaded";
        } else {
            message = "Partial success: " + successfulRecords + " users uploaded, " + failedRecords + " failed";
        }

        return BulkUploadResponseDTO.builder()
                .totalRecords(totalRecords)
                .successfulRecords(successfulRecords)
                .failedRecords(failedRecords)
                .errorMessages(errorMessages)
                .message(message)
                .build();
    }

    @Override
    public Page<UserSummaryDTO> getUserSummary(Pageable pageable) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        Page<User> users = userRepository.findByBrokerBrokerId(currentBrokerId, pageable);
        
        List<UserSummaryDTO> userSummaries = users.getContent().stream()
                .map(user -> {
                    // Calculate total payable brokerage: (totalBagsSold + totalBagsBought) * brokerageRate
                    Long totalBags = (user.getTotalBagsSold() != null ? user.getTotalBagsSold() : 0L) + 
                                   (user.getTotalBagsBought() != null ? user.getTotalBagsBought() : 0L);
                    BigDecimal brokerageRate = user.getBrokerageRate() != null ? BigDecimal.valueOf(user.getBrokerageRate()) : BigDecimal.ZERO;
                    BigDecimal calculatedBrokerage = brokerageRate.multiply(BigDecimal.valueOf(totalBags));
                    
                    return UserSummaryDTO.builder()
                            .userId(user.getUserId())
                            .firmName(user.getFirmName())
                            .city(user.getAddress() != null ? user.getAddress().getCity() : null)
                            .totalBagsSold(user.getTotalBagsSold() != null ? user.getTotalBagsSold() : 0L)
                            .totalBagsBought(user.getTotalBagsBought() != null ? user.getTotalBagsBought() : 0L)
                            .brokeragePerBag(brokerageRate)
                            .totalPayableBrokerage(calculatedBrokerage)
                            .build();
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(userSummaries, pageable, users.getTotalElements());
    }
    
    @Override
    public Page<UserSummaryDTO> getUserSummaryByFinancialYear(Long financialYearId, Pageable pageable) {
        Long currentBrokerId = tenantContextService.getCurrentBrokerId();
        Page<User> users = userRepository.findUsersByBrokerIdAndFinancialYear(currentBrokerId, financialYearId, pageable);
        
        // Get actual bag counts and brokerage for the specific financial year
        List<Object[]> bagCountsAndBrokerage = userRepository.findUserBagCountsAndBrokerageByFinancialYear(currentBrokerId, financialYearId);
        
        // Create a map for quick lookup of financial year specific data
        Map<Long, Object[]> userDataMap = bagCountsAndBrokerage.stream()
                .collect(Collectors.toMap(
                    row -> (Long) row[0], // userId
                    row -> row
                ));
        
        List<UserSummaryDTO> userSummaries = users.getContent().stream()
                .map(user -> {
                    Object[] userData = userDataMap.get(user.getUserId());
                    
                    Long bagsSold = 0L;
                    Long bagsBought = 0L;
                    BigDecimal actualBrokerage = BigDecimal.ZERO;
                    
                    if (userData != null) {
                        bagsSold = userData[1] != null ? ((Number) userData[1]).longValue() : 0L;
                        bagsBought = userData[2] != null ? ((Number) userData[2]).longValue() : 0L;
                        actualBrokerage = userData[3] != null ? new BigDecimal(userData[3].toString()) : BigDecimal.ZERO;
                    }
                    
                    BigDecimal brokerageRate = user.getBrokerageRate() != null ? BigDecimal.valueOf(user.getBrokerageRate()) : BigDecimal.ZERO;
                    
                    return UserSummaryDTO.builder()
                            .userId(user.getUserId())
                            .firmName(user.getFirmName())
                            .city(user.getAddress() != null ? user.getAddress().getCity() : null)
                            .totalBagsSold(bagsSold)
                            .totalBagsBought(bagsBought)
                            .brokeragePerBag(brokerageRate)
                            .totalPayableBrokerage(actualBrokerage)
                            .build();
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(userSummaries, pageable, users.getTotalElements());
    }
}
