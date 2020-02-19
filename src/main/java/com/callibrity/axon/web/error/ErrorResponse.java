package com.callibrity.axon.web.error;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ErrorResponse {
    @Singular
    private List<String> errors;
}
