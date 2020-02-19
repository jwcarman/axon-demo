package com.callibrity.axon.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoggingCustomerNotificationService implements CustomerNotificationService {
    @Override
    public void sendLowBalanceNotification(String accountId, int balance) {
        log.info("Account {} has a low balance of {}.", accountId, balance);
    }
}
