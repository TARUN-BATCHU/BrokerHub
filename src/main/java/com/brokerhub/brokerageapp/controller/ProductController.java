package com.brokerhub.brokerageapp.controller;

import com.brokerhub.brokerageapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BrokerHub/Product")
public class ProductController {

    @Autowired
    ProductService productService;
}
