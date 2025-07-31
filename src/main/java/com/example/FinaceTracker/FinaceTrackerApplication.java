package com.example.FinaceTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.FinaceTracker")
public class FinaceTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinaceTrackerApplication.class, args);
	}

}

