package com.callibrity.axon.domain.exception;

public class OutstandingBalanceException extends RuntimeException {
    public OutstandingBalanceException(String accountId, int balance) {
        super(String.format("Unable to close account %s, because it has a positive balance %d.", accountId, balance));
    }
}
