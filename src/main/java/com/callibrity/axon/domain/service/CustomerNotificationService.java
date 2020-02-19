package com.callibrity.axon.domain.service;

public interface CustomerNotificationService {
    void sendLowBalanceNotification(String accountId, int balance);
}
