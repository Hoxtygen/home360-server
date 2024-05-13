package com.codeplanks.home360.listing;


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
public class ListingCost {
    @NotNull(message = "Annual rent is required")
    @Field(name = "annual_rent")
    private Integer annualRent;

    @Field(name = "agent_fee")
    private Integer agentFee;

    @Field(name = "caution_fee")
    private Integer cautionFee;

    @Field(name = "agreement_fee")
    private Integer agreementFee;

}

