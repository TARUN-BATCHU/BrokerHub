package com.brokerhub.brokerageapp.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpUtil {

    public Integer generateOtp(){
        Random random = new Random();
        Integer randomNumber = random.nextInt(100000,999999);
        return randomNumber;
    }
}
