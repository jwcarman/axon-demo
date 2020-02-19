package com.callibrity.axon.domain.command;

import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import javax.validation.constraints.NotEmpty;

@Value
@Builder
public class CloseAccountCommand {
    @TargetAggregateIdentifier
    @NotEmpty(message = "Account id is required.")
    private String accountId;
}
