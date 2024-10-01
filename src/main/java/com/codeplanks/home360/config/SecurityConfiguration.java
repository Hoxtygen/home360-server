/* (C)2024 */
package com.codeplanks.home360.config;

import com.codeplanks.home360.exception.CustomAccessDeniedHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .addFilterBefore(new TrailingSlashRedirectFilter(), ChannelProcessingFilter.class)
        .addFilterBefore(jwtAuthFilter, BasicAuthenticationFilter.class)
        .cors(Customizer.withDefaults())
        .csrf()
        .disable()
        .authorizeHttpRequests()
        .requestMatchers(
            HttpMethod.GET, "/**", "/api", "/api-docs/**", "/swagger-ui/**", "/api" + "/v1")
        .permitAll()
        .and()
        .authorizeHttpRequests()
        .requestMatchers("/api/v1/auth/**")
        .permitAll()
        .and()
        .authorizeHttpRequests()
        .requestMatchers(
            HttpMethod.GET, "/api/v1/listings", "/api/v1/listings/*", "/api/v1/listings/search/*")
        .permitAll()
        .and()
        .authorizeHttpRequests()
        .requestMatchers(HttpMethod.POST, "/api/v1/listing-enquiries")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
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
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
