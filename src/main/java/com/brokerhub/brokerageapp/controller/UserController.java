package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.BulkUploadResponseDTO;
import com.brokerhub.brokerageapp.dto.UserDTO;
import com.brokerhub.brokerageapp.dto.UserSummaryDTO;
import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.service.UserService;
import com.brokerhub.brokerageapp.utils.ExcelTemplateGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/BrokerHub/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDTO userDTO){
        return userService.createUser(userDTO);
    }

    @PostMapping(value = "/bulkUpload", consumes = "multipart/form-data")
    public ResponseEntity<BulkUploadResponseDTO> bulkUploadUsers(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                BulkUploadResponseDTO response = BulkUploadResponseDTO.builder()
                        .totalRecords(0)
                        .successfulRecords(0)
                        .failedRecords(0)
                        .errorMessages(List.of("Please select a file to upload"))
                        .message("No file selected")
                        .build();
                return ResponseEntity.badRequest().body(response);
            }

            BulkUploadResponseDTO response = userService.bulkUploadUsers(file);

            if (response.getSuccessfulRecords() > 0) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            BulkUploadResponseDTO errorResponse = BulkUploadResponseDTO.builder()
                    .totalRecords(0)
                    .successfulRecords(0)
                    .failedRecords(0)
                    .errorMessages(List.of("Server error: " + e.getMessage()))
                    .message("Upload failed")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/downloadTemplate")
    public ResponseEntity<ByteArrayResource> downloadBulkUploadTemplate() {
        return ExcelTemplateGenerator.generateTemplate();
    }

    @PutMapping("/updateUser")
    public User updateUser(@Valid @RequestBody User user){
        return userService.updateUser(user);
    }

    @DeleteMapping("/deleteUser/")
    public ResponseEntity<String> deleteUser(@RequestParam Long Id){
        return userService.deleteUser(Id);
    }

    @GetMapping("/allUsers")
    public List<User> getAllUsers(Pageable pageable){
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/allUsers/")
    public Object getAllUsersByCity(@RequestParam String city){
        return userService.getAllUsersByCity(city);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId){
        Optional<User> user = userService.getUserById(userId);
        if(user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/brokerageMoreThan/")
    public List<User> getUsersHavingBrokerageMoreThan(@RequestParam int brokerage){
        return userService.getAllUsersHavingBrokerageMoreThan(brokerage);
    }

    @GetMapping("/brokerageInRange/")
    public List<User> getUsersHavingBrokerageInRange(@RequestParam int min, int max){
        return userService.getAllUsersHavingBrokerageInRange(min,max);
    }

    @GetMapping("/")
    public Object getUserByProperty(@RequestParam String property, String value){
        return userService.getUserByProperty(property,value);
    }

    //for search bar we will fetch all usernames and ids and in searchbar we can give drop down and when he selects a user we can send id to backend
    @GetMapping("/getUserNamesAndIds")
    public List<HashMap<String,Long>> getUserNamesAndIds(){
        return userService.getUserNamesAndIds();
    }

    @GetMapping("/getUserNames")
    public List<String> getUserNames(){
        return userService.getUserNames();
    }

    @GetMapping("/getFirmNamesIdsAndCities")
    public List<HashMap<String,Object>> getFirmNamesIdsAndCities(){
        return userService.getFirmNamesIdsAndCities();
    }

    @GetMapping("/getUserSummary")
    public Page<UserSummaryDTO> getUserSummary(Pageable pageable){
        return userService.getUserSummary(pageable);
    }

//    @PutMapping("/updateBrokerage")
//    public void updateUserBrokerage(Long UserId, int brokerage){
//        return userService.updateBrokerage();
//    }


}
