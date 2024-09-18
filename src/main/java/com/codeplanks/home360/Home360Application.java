/* (C)2024 */
package com.codeplanks.home360;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableAsync(proxyTargetClass = true)
@OpenAPIDefinition(
    info = @Info(title = "Swagger API", version = "v1.0", description = "Home360 API"))
public class Home360Application {
  public static void main(String[] args) {
    SpringApplication.run(Home360Application.class, args);
  }
}
