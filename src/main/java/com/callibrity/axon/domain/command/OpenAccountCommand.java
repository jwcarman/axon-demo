package com.callibrity.axon.domain.command;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Min;

@Value
@Builder
public class OpenAccountCommand {
    @Min(value = 1, message = "Opening balance must be positive.")
    private int openingBalance;
}
