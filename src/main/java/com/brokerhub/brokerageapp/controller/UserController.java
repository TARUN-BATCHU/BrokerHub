package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.dto.UserDTO;
import com.brokerhub.brokerageapp.entity.User;
import com.brokerhub.brokerageapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Optional<User> getUserById(@PathVariable Long userId){
        return userService.getUserById(userId);
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


}
