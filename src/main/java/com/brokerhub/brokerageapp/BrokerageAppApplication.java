package com.brokerhub.brokerageapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BrokerageAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrokerageAppApplication.class, args);
	}

}
