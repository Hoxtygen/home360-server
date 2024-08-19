package com.codeplanks.home360.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class Home {
  @GetMapping("/api")
  public ResponseEntity<Object> apiRoot() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("message", "Welcome to Home360 API.");
    body.put("status", String.valueOf(HttpStatus.OK));
    body.put("timestamp", LocalDateTime.now());
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @GetMapping("/api/v1")
  public ResponseEntity<Object> apiV1Root() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("message", "Welcome to Home360 version 1 API.");
    body.put("status", String.valueOf(HttpStatus.OK));
    body.put("timestamp", LocalDateTime.now());
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
