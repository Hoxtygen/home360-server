package com.codeplanks.home360.domain.listing;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Setter
@Getter
@AllArgsConstructor
@Builder
@JsonInclude(value =JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ApartmentInfo {
    @Field(name = "room_nums")
    @NotNull(message = "Number of rooms in the apartment is required")
    private Integer roomNums;

    @Field(name = "bathroom_nums")
    @NotNull(message = "Number of bathroom is required")
    private Integer bathroomNums;

    @Field(name = "bedroom_nums")
    @NotNull(message = "Number of bedroom is required")
    private Integer bedroomNums;

    @Field(name = "apartment_type")
    @NotNull(message = "Apartment type is required")
    @Valid
    private ApartmentType apartmentType;
}
