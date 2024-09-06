package com.example.banktest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


@EnableJpaAuditing
@SpringBootApplication
@EntityScan(basePackages = {"com.example.banktest.domain.entity"})
@EnableRedisRepositories(basePackages = "com.example.banktest.domain.chat")
public class BanktestApplication {

	public static void main(String[] args) {
		SpringApplication.run(BanktestApplication.class, args);
	}

}
