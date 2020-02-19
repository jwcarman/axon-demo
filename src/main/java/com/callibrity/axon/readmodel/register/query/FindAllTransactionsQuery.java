package com.callibrity.axon.readmodel.register.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindAllTransactionsQuery {
    @NotEmpty(message = "Account id is required.")
    private String accountId;
}
