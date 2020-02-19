package com.callibrity.axon.web.msg;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TransactionDto {
    private int amount;
    private int balance;
}
