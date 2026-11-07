package com.example.Spring.Authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;




@SpringBootApplication(scanBasePackages = "com.example")
@EntityScan(basePackages = "com.example")
@EnableJpaRepositories(basePackages = "com.example")
public class SpringAuthenticationApplication {

	public static void main(String[] args){
		SpringApplication.run(SpringAuthenticationApplication.class, args);
	}
}