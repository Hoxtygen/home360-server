package com.codeplanks.home360;

import com.codeplanks.home360.student.Address;
import com.codeplanks.home360.student.Gender;
import com.codeplanks.home360.student.Student;
import com.codeplanks.home360.student.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class Home360Application {
    public static void main(String[] args) {
        SpringApplication.run(Home360Application.class, args);
    }

//    @Bean
//    CommandLineRunner runner(StudentRepository repository, MongoTemplate mongoTemplate) {
//        System.out.println("This is running in commandLine runner");
//        return args -> {
//            Address address = new Address("Germany", "Frankfurt", "30171");
//            String email = "zea_mays@example.com";
//            Student student = new Student(
//                    "Zea", "Mays", email, Gender.MALE, address, List.of(
//                    "Computer Science", "Mathematics", "Biology"), BigDecimal.TEN,
//                    LocalDateTime.now()
//            );
////      usingMongoTemplateAndEmail(repository, mongoTemplate, email, student);
//            repository.findStudentByEmail(email).ifPresentOrElse(student1 -> {
//                System.out.println("student already exists.");
//            }, () -> {
//                System.out.println("Inserting student " + student);
//                repository.insert(student);
//            });
//
//        };
//    }



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
