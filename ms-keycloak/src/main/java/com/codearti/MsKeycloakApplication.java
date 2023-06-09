package com.codearti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MsKeycloakApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsKeycloakApplication.class, args);
	}

}
