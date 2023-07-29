package com.codeplanks.home360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class Home360Application {
    public static void main(String[] args) {
        SpringApplication.run(Home360Application.class, args);
    }
    @GetMapping("/api/v1")
    public ResponseEntity<Object> apiRoot() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Welcome to Home360 version 1 API.");
        body.put("status", String.valueOf(HttpStatus.OK));
        body.put("timestamp",
                new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }


}
