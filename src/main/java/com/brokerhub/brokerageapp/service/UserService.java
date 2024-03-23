package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.UserDTO;
import com.brokerhub.brokerageapp.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    ResponseEntity createUser(UserDTO userDTO);

    User updateUser(User user);

    ResponseEntity<String> deleteUser(Long id);

    List<User> getAllUsers();

    Object getAllUsersByCity(String city);

    Optional<User> getUserById(Long userId);

    List<User> getAllUsersHavingBrokerageMoreThan(int brokerage);

    List<User> getAllUsersHavingBrokerageInRange(int min, int max);
}
