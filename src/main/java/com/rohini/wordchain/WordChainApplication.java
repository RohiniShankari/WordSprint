package com.rohini.wordchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WordChainApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordChainApplication.class, args);
	}

}
