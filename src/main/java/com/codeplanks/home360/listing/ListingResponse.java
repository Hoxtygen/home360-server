package com.codeplanks.home360.listing;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingResponse {
private HttpStatus status;
private String message;
private Listing listing;

//  private String address;
//  private String agentId;
//  private boolean available;
//  private String description;
//  private String id;
//  private String name;
//  private String city;
//  private String state;
//  private  String images;


}


