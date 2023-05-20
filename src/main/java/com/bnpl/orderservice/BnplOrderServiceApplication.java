package com.bnpl.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BnplOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BnplOrderServiceApplication.class, args);
    }

}
