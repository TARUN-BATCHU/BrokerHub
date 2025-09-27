package com.brokerhub.brokerageapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.awt.*;
import java.net.URI;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BrokerageAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrokerageAppApplication.class, args);
	}
	@EventListener(ApplicationReadyEvent.class)
	public void openBrowser() {
		try {
			String url = "http://localhost:8080";

			if (Desktop.isDesktopSupported()) {
				System.out.println("✅ Using Desktop API to open browser...");
				Desktop.getDesktop().browse(new URI(url));
			} else {
				System.out.println("⚡ Desktop not supported, using Windows command fallback...");
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
