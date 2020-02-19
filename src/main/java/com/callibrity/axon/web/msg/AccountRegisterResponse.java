package com.callibrity.axon.web.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class AccountRegisterResponse {
    private List<TransactionDto> transactions;
}
