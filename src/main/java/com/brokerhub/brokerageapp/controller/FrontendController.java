package com.brokerhub.brokerageapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @RequestMapping(value = {
        "/",
        "/login",
        "/dashboard",
        "/profile",
        "/settings/**"
    })
    public String forward() {
        // Serve React app for specific frontend routes only
        return "forward:/index.html";
    }
}
