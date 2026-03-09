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
			String os = System.getProperty("os.name").toLowerCase();

			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				System.out.println("✅ Using Desktop API to open browser...");
				Desktop.getDesktop().browse(new URI(url));
			} else if (os.contains("mac")) {
				Runtime.getRuntime().exec("open " + url);
			} else if (os.contains("win")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else if (os.contains("nix") || os.contains("nux")) {
				Runtime.getRuntime().exec("xdg-open " + url);
			}
		} catch (Exception e) {
			System.err.println("⚠️ Could not open browser automatically. Please open: http://localhost:8080");
		}
	}

}
