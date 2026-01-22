package com.zetra.econsig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class EconsigApplication {

	public static void main(String[] args) {
		SpringApplication.run(EconsigApplication.class, args);
	}
}
