package com.codeplanks.home360.listing;


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

