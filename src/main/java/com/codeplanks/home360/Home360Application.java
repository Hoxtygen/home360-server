/* (C)2024 */
package com.codeplanks.home360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableAsync(proxyTargetClass = true)
@EnableTransactionManagement
public class Home360Application {
  public static void main(String[] args) {
    SpringApplication.run(Home360Application.class, args);
  }
}
