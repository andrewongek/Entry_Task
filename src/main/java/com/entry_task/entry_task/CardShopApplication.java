package com.entry_task.entry_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class CardShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardShopApplication.class, args);
    }

}
