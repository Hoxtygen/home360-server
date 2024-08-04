package com.codeplanks.home360.domain.listing;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonInclude(value =JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class Address {
    @NotNull(message = "Street name is required")
    @Field(name = "street_name")
    private String streetName;

    @Field(name = "house_number")
    private String houseNumber;

    @NotNull(message = "City is required")
    @Field(name = "city")
    private String city;

    @NotNull(message = "State is required")
    @Field(name = "state")
    private  String state;

    @NotNull(message = "Local government area is required")
    @Field(name = "lga")
    private String lga;

}
