package com.sarvam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class SarvamApplication {

	public static void main(String[] args) {
		SpringApplication.run(SarvamApplication.class, args);
	}

}
