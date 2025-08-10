package com.smart.q.smartq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.smart.q.smartq")
public class SmartqwebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartqwebApplication.class, args);
	}

}

