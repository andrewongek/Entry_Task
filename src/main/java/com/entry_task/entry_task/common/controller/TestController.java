package com.entry_task.entry_task.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    public TestController() {
    }

    @GetMapping("/all")
    public String allAccess() {
        logger.info("allAccess has been called");
        logger.debug("debug");
        return "Public Content.";
    }
    @GetMapping("/user")
    public String userAccess() {
        logger.info("userAccess has been called");
        logger.debug("debug2");
        return "User Content.";
    }
    @GetMapping("/seller")
    public String sellerAccess() {
        return "Seller Content.";
    }
    @GetMapping("/admin")
    public String adminAccess() {
        return "Admin Content.";
    }
}