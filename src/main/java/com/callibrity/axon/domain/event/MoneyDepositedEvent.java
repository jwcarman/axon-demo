package com.callibrity.axon.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyDepositedEvent {
    private String accountId;
    private int amount;
    private int balance;
}
