package com.codeplanks.home360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Home360Application {

	public static void main(String[] args) {
		SpringApplication.run(Home360Application.class, args);
	}
	@GetMapping("/api/v1")
	public String apiRoot() {
		return "Welcome to Home360. Everything we do is all about making your life better and easier.";
	}

	@GetMapping("/error")
	public String error() {
		return "There was an error. Please fine tune your search";
	}

}
