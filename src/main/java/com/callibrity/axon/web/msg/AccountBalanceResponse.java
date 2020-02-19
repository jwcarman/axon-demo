package com.callibrity.axon.web.msg;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountBalanceResponse {
    private int balance;
}
