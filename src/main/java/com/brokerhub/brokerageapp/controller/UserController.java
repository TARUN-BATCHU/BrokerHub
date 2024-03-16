package com.brokerhub.brokerageapp.controller;



import com.brokerhub.brokerageapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BrokerHub/User")
public class UserController {

    @Autowired
    UserService userService;

}
