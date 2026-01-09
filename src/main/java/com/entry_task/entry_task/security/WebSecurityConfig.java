package com.entry_task.entry_task.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {
  @Autowired private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Updated configuration for Spring Security 6.x
    http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF
        .cors(AbstractHttpConfigurer::disable) // Disable CORS (or configure if needed)
        .exceptionHandling(
            exceptionHandling -> exceptionHandling.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorizeRequests ->
                authorizeRequests
                    .requestMatchers(
                        "/api/test/**",
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs*/**",
                        "/h2-console")
                    .permitAll()
                    .requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/logout")
                    .permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/category/**")
                    .hasAnyRole()
                    .requestMatchers(
                        "/api/products/**", "/api/cart/**", "/api/favourites/**", "/api/orders/**")
                    .hasRole("CUSTOMER")
                    .requestMatchers("/api/test/seller", "/api/seller/**")
                    .hasRole("SELLER")
                    .requestMatchers(
                        "/api/test/admin",
                        "/api/admin/**",
                        "/api/auth/admin/**",
                        "/api/category/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .denyAll());
    // Add the JWT Token filter before the UsernamePasswordAuthenticationFilter
    http.addFilterBefore(
        authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
