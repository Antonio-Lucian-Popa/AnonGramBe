package com.asusoftware.AnonGram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AnonGramApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnonGramApplication.class, args);
	}

}
