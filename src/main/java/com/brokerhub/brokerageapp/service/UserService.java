package com.brokerhub.brokerageapp.service;

import com.brokerhub.brokerageapp.dto.UserDTO;
import com.brokerhub.brokerageapp.entity.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity createUser(UserDTO userDTO);
}
