package com.service.logistservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//@EnableFeignClients
public class LogistServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogistServiceApplication.class, args);
	}

}
