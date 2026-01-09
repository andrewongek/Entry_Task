package com.entry_task.entry_task.config.observibility;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Add timestamp as soon as the request hits the filter
    response.addHeader("X-Request-Timestamp", Instant.now().toString());

    SpanContext ctx = Span.current().getSpanContext();
    if (ctx.isValid()) {
      response.setHeader("X-Trace-Id", ctx.getTraceId());
    }

    filterChain.doFilter(request, response);
  }
}
