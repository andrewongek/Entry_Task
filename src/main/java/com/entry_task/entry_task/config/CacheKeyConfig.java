package com.entry_task.entry_task.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheKeyConfig {

  private final ObjectMapper objectMapper;

  public CacheKeyConfig(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Bean("sha256KeyGenerator")
  public KeyGenerator sha256KeyGenerator() {
    return (target, method, params) -> {
      try {
        // Serialize parameters to JSON
        String paramJson = objectMapper.writeValueAsString(params);

        // Generate SHA-256 hash
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(paramJson.getBytes(StandardCharsets.UTF_8));

        // Encode as URL-safe Base64
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);

      } catch (Exception e) {
        throw new RuntimeException("Failed to generate cache key", e);
      }
    };
  }
}
