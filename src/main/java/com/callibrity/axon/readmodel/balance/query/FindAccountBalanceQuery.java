package com.callibrity.axon.readmodel.balance.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindAccountBalanceQuery {
    @NotEmpty(message = "Account id is required.")
    private String accountId;
}
