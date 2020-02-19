package com.callibrity.axon.domain.command;

import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Value
@Builder
public class DepositMoneyCommand {
    @TargetAggregateIdentifier
    @NotEmpty(message = "Account id is required.")
    private String accountId;

    @Min(value = 1, message = "Deposit amount must be positive.")
    private int amount;
}
