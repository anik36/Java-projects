package com.loan_system.lls.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
  private static final String[] PUBLIC_API ={"/api/**", "/swagger-ui/**", "/v3/api-docs/**", "/**" };
  // private static final String[] ADMIN_API ={"/sent_otp","/verify_otp","/import_loan_types", "/list_loans","/update_loans_status"}; "/request_loan","/sent_otp","/verify_otp",
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);

  @Autowired
  private JwtAuthenticationFilter jwtAuthFilter;
  @Autowired
  private  AuthenticationProvider authenticationProvider;
  @Autowired
  private LogoutHandler logoutHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    LOGGER.debug("In securityFilterChain() -> SecurityConfiguration.java ");
    http
        .csrf()
        .disable()
        .authorizeHttpRequests()
        .requestMatchers(PUBLIC_API)
          .permitAll()
          // .requestMatchers(ADMIN_API).hasAuthority("ADMIN")
        .anyRequest()
          .authenticated()
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .logout()
        .logoutUrl("/api/logout")
        .addLogoutHandler(logoutHandler)
        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
    ;

    return http.build();
  }
}
