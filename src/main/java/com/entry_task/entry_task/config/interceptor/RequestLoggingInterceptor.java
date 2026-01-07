package com.entry_task.entry_task.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
  private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    long startTime = System.currentTimeMillis();

    logger.info("Request URI: {} | Method: {}", request.getRequestURI(), request.getMethod());

    MDC.put("requestStartTime", String.valueOf(startTime));
    MDC.put("requestUri", request.getRequestURI());

    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    long endTime = System.currentTimeMillis();
    long duration = endTime - Long.parseLong(MDC.get("requestStartTime"));

    logger.info("Response Status: {} | Duration: {} ms", response.getStatus(), duration);

    MDC.clear();
  }
}
