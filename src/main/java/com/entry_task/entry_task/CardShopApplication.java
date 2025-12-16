package com.entry_task.entry_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class CardShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardShopApplication.class, args);
    }

}
