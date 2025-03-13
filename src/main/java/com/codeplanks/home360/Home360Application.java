/* (C)2024-2025 */
package com.codeplanks.home360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "com.codeplanks.home360")
@RestController
@EnableAsync(proxyTargetClass = true)
@EnableTransactionManagement
@EnableCaching
@EnableJpaRepositories(basePackages = "com.codeplanks.home360.repository")
public class Home360Application {
  public static void main(String[] args) {
    SpringApplication.run(Home360Application.class, args);
  }
}
