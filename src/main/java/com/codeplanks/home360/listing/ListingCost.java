package com.codeplanks.home360.listing;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.mongodb.lang.NonNull;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonInclude(value =JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class ListingCost {
    @NotNull(message = "Annual rent is required")
    @Field(name = "annual_rent")
    private BigDecimal annualRent;

    @Field(name = "agent_fee")
    private BigDecimal agentFee;

    @Field(name = "caution_fee")
    private BigDecimal cautionFee;

    @Field(name = "agreement_fee")
    private BigDecimal agreementFee;

}

