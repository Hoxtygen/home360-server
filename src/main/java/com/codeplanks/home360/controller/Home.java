package com.codeplanks.home360.controller;

import com.codeplanks.home360.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Home", description = "Home route management APIs")
public class Home {
  @Operation(
          summary = "Home360 home",
          description = "Home360 base home",
          tags = {"GET"}
  )
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Successful",
                  content = {@Content(mediaType = "application/json")}
          ),
  })
  @GetMapping("/api")
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
          tags = {"GET"}
  )
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Successful",
                  content = {@Content(mediaType = "application/json")}
          ),
  })
  @GetMapping("/api/v1")
  public ResponseEntity<Object> apiV1Root() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("message", "Welcome to Home360 version 1 API.");
    body.put("status", String.valueOf(HttpStatus.OK));
    body.put("timestamp", LocalDateTime.now());
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
