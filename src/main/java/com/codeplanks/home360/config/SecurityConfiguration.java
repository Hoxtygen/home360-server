/* (C)2024 */
package com.codeplanks.home360.config;

import com.codeplanks.home360.exception.CustomAccessDeniedHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author Wasiu Idowu
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;

  @Value("${cors.allowed-origins}")
  private String allowedOrigins;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .addFilterBefore(new TrailingSlashRedirectFilter(), ChannelProcessingFilter.class)
        .addFilterBefore(jwtAuthFilter, BasicAuthenticationFilter.class)
        .cors(Customizer.withDefaults())
        .csrf()
        .disable()
        .authorizeHttpRequests(
            (requests) ->
                requests
                    .requestMatchers(
                        HttpMethod.POST, "/api/v1/listing-enquiries", "/api/v1/listing-views")
                    .permitAll()
                    .requestMatchers("/api/v1/auth/**", "/actuator/**")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html/**",
                        "/v3/api/docs/**",
                        "/api/v1",
                        "/api/v1/listings",
                        "/api/v1/listings/*",
                        "/api/v1/listings/search/*")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            (exceptions) ->
                exceptions
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                    .accessDeniedHandler(new CustomAccessDeniedHandler()))
        .authenticationProvider(authenticationProvider);
    return httpSecurity.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
