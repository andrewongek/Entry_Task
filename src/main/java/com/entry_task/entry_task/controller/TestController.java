package com.entry_task.entry_task.controller;

import com.entry_task.entry_task.api.ApiResponse;
import com.entry_task.entry_task.dto.CreateCategoryRequest;
import com.entry_task.entry_task.services.CategoryService;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    private final CategoryService categoryService;

    public TestController(CategoryService categoryService) {
        this.categoryService = categoryService;
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

    @PostMapping("/all/cat")
    public ResponseEntity<ApiResponse<?>> createCategory(@RequestBody CreateCategoryRequest request) {
        categoryService.createCategory(request);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }
}