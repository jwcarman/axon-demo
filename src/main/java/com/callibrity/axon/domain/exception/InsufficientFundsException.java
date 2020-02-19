package com.callibrity.axon.domain.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String accountId, int withdrawalAmount, int balance) {
        super(String.format("Unable to withdraw %d from account %s (balance is %d).", withdrawalAmount, accountId, balance));
    }
}
