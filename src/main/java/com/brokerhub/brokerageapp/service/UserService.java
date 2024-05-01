package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.UserDTO;
import com.brokerhub.brokerageapp.entity.Address;
import com.brokerhub.brokerageapp.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface UserService {
    ResponseEntity createUser(UserDTO userDTO);

    User updateUser(User user);

    ResponseEntity<String> deleteUser(Long id);

    List<User> getAllUsers(Pageable pageable);

    Object getAllUsersByCity(String city);

    Optional<User> getUserById(Long userId);

    List<User> getAllUsersHavingBrokerageMoreThan(int brokerage);

    List<User> getAllUsersHavingBrokerageInRange(int min, int max);

    Object getUserByProperty(String property, String value);

    List<HashMap<String, Long>> getUserNamesAndIds();

    List<String> getUserNames();
}
