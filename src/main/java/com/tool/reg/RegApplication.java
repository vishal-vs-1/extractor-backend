package com.tool.reg;

import org.apache.tika.Tika;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RegApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegApplication.class, args);

	}

	@Bean
	public Tika tika(){
		return new Tika();
	}
}
