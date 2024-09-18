/* (C)2024 */
package com.codeplanks.home360.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Home", description = "Home route management APIs")
public class Home {
  @Operation(
      summary = "Home360 home",
      description = "Home360 base home",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful",
        content = {@Content(mediaType = "application/json")}),
  })
  @GetMapping
  public ResponseEntity<Object> apiRoot() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("message", "Welcome to Home360 API.");
    body.put("status", String.valueOf(HttpStatus.OK));
    body.put("timestamp", LocalDateTime.now());
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @Operation(
      summary = "Home360 version 1 home",
      description = "Home360 version 1 base home",
      tags = {"GET"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful",
        content = {@Content(mediaType = "application/json")}),
  })
  @GetMapping("/v1")
  public ResponseEntity<Object> apiV1Root() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("message", "Welcome to Home360 version 1 API.");
    body.put("status", String.valueOf(HttpStatus.OK));
    body.put("timestamp", LocalDateTime.now());
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  @Hidden
  @RequestMapping(value = "/**", produces = "application/json")
  public ResponseEntity<Object> handleAll(HttpServletRequest request) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("message", "This route does not exist.");
    body.put("status", String.valueOf(HttpStatus.NOT_FOUND));
    body.put("timestamp", LocalDateTime.now());
    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }
}
