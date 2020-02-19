package com.callibrity.axon.domain.exception;

public class AccountClosedException extends RuntimeException {
    public AccountClosedException(String accountId) {
        super(String.format("Account %s is closed.", accountId));
    }
}
