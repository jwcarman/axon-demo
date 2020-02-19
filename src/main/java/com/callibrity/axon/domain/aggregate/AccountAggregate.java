package com.callibrity.axon.domain.aggregate;

import com.callibrity.axon.domain.command.CloseAccountCommand;
import com.callibrity.axon.domain.command.DepositMoneyCommand;
import com.callibrity.axon.domain.command.OpenAccountCommand;
import com.callibrity.axon.domain.command.WithdrawMoneyCommand;
import com.callibrity.axon.domain.event.AccountClosedEvent;
import com.callibrity.axon.domain.event.AccountOpenedEvent;
import com.callibrity.axon.domain.event.MoneyDepositedEvent;
import com.callibrity.axon.domain.event.MoneyWithdrawnEvent;
import com.callibrity.axon.domain.exception.AccountClosedException;
import com.callibrity.axon.domain.exception.InsufficientFundsException;
import com.callibrity.axon.domain.exception.OutstandingBalanceException;
import com.callibrity.axon.domain.service.CustomerNotificationService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate(snapshotTriggerDefinition = "accountSnapshotTrigger")
@Getter
@NoArgsConstructor
@Slf4j
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;

    private int balance;

    private boolean open;

//**********************************************************************************************************************
// Commands
//**********************************************************************************************************************

    @CommandHandler
    public AccountAggregate(OpenAccountCommand command) {
        final String accountId = UUID.randomUUID().toString();
        log.info("Opening account {}...", accountId);
        apply(AccountOpenedEvent.builder()
                .accountId(accountId)
                .build());

        apply(MoneyDepositedEvent.builder()
                .accountId(accountId)
                .amount(command.getOpeningBalance())
                .balance(command.getOpeningBalance())
                .build());
    }

    @CommandHandler
    public void handle(DepositMoneyCommand command) {
        verifyOpen();
        log.info("Depositing {} into account {}...", command.getAmount(), accountId);
        apply(MoneyDepositedEvent.builder()
                .accountId(accountId)
                .balance(balance + command.getAmount())
                .amount(command.getAmount())
                .build());
    }

    @CommandHandler
    public void handle(WithdrawMoneyCommand command,
                       CustomerNotificationService notificationService) {
        verifyOpen();
        verifySufficientFunds(command.getAmount());
        final MoneyWithdrawnEvent event = MoneyWithdrawnEvent.builder()
                .accountId(accountId)
                .balance(balance - command.getAmount())
                .amount(command.getAmount())
                .build();
        if (event.getBalance() < 100) {
            notificationService.sendLowBalanceNotification(accountId, event.getBalance());
        }
        log.info("Withdrawing {} from account {}...", command.getAmount(), accountId);
        apply(event);
    }

    private void verifySufficientFunds(int amount) {
        if (balance < amount) {
            throw new InsufficientFundsException(accountId, amount, balance);
        }
    }

    private void verifyOpen() {
        if (!open) {
            throw new AccountClosedException(accountId);
        }
    }

    @CommandHandler
    public void handle(CloseAccountCommand command) {
        verifyOpen();
        verifyZeroBalance();
        log.info("Closing account {}...", accountId);
        apply(AccountClosedEvent.builder()
                .accountId(accountId)
                .build());
    }

    private void verifyZeroBalance() {
        if (balance > 0) {
            throw new OutstandingBalanceException(accountId, balance);
        }
    }

//**********************************************************************************************************************
// Events
//**********************************************************************************************************************

    @EventSourcingHandler
    public void on(AccountOpenedEvent event) {
        this.open = true;
        this.accountId = event.getAccountId();
        this.balance = 0;
    }

    @EventSourcingHandler
    public void on(MoneyDepositedEvent event) {
        this.balance = event.getBalance();
    }

    @EventSourcingHandler
    public void on(MoneyWithdrawnEvent event) {
        this.balance = event.getBalance();
    }

    @EventSourcingHandler
    public void on(AccountClosedEvent event) {
        this.open = false;
    }
}
