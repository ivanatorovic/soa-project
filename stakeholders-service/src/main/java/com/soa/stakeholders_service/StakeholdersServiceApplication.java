package com.soa.stakeholders_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class StakeholdersServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StakeholdersServiceApplication.class, args);
	}
}