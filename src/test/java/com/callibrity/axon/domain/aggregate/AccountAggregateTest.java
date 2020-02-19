package com.callibrity.axon.domain.aggregate;

import com.callibrity.axon.domain.command.CloseAccountCommand;
import com.callibrity.axon.domain.command.DepositMoneyCommand;
import com.callibrity.axon.domain.command.OpenAccountCommand;
import com.callibrity.axon.domain.command.WithdrawMoneyCommand;
import com.callibrity.axon.domain.event.AccountClosedEvent;
import com.callibrity.axon.domain.event.AccountOpenedEvent;
import com.callibrity.axon.domain.event.MoneyDepositedEvent;
import com.callibrity.axon.domain.exception.AccountClosedException;
import com.callibrity.axon.domain.exception.InsufficientFundsException;
import com.callibrity.axon.domain.exception.OutstandingBalanceException;
import com.callibrity.axon.domain.service.CustomerNotificationService;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AccountAggregateTest {

    @Mock
    private CustomerNotificationService notificationService;

    private FixtureConfiguration<AccountAggregate> fixture;

    @BeforeEach
    public void setUpFixture() {
        fixture = new AggregateTestFixture<>(AccountAggregate.class);
        fixture.registerInjectableResource(notificationService);
    }

    @Test
    public void openAccount() {
        fixture.givenNoPriorActivity()
                .when(OpenAccountCommand.builder()
                        .openingBalance(10)
                        .build())
                .expectSuccessfulHandlerExecution()
                .expectState(account -> {
                    assertThat(account.getAccountId()).isNotNull();
                    assertThat(account.getBalance()).isEqualTo(10);
                    assertThat(account.isOpen()).isTrue();
                });
    }

    @Test
    public void depositMoney() {
        fixture.given(AccountOpenedEvent.builder()
                .accountId("12345")
                .build())
                .when(DepositMoneyCommand.builder()
                        .accountId("12345")
                        .amount(11)
                        .build())
                .expectSuccessfulHandlerExecution()
                .expectState(account -> {
                    assertThat(account.isOpen()).isTrue();
                    assertThat(account.getBalance()).isEqualTo(11);
                    assertThat(account.getAccountId()).isEqualTo("12345");
                });
    }

    @Test
    public void depositMoneyWithClosedAccount() {
        fixture.given(
                AccountOpenedEvent.builder()
                        .accountId("12345")
                        .build(),
                AccountClosedEvent.builder()
                        .accountId("12345")
                        .build())
                .when(DepositMoneyCommand.builder()
                        .accountId("12345")
                        .amount(11)
                        .build())
                .expectException(AccountClosedException.class);
    }

    @Test
    public void withdrawMoney() {
        fixture.given(AccountOpenedEvent.builder()
                        .accountId("12345")
                        .build(),
                MoneyDepositedEvent.builder()
                        .accountId("12345")
                        .amount(1000)
                        .balance(1000)
                        .build())
                .when(WithdrawMoneyCommand.builder()
                        .accountId("12345")
                        .amount(50)
                        .build())
                .expectSuccessfulHandlerExecution()
                .expectState(account -> {
                    assertThat(account.isOpen()).isTrue();
                    assertThat(account.getBalance()).isEqualTo(950);
                    assertThat(account.getAccountId()).isEqualTo("12345");
                });
        verifyNoMoreInteractions(notificationService);
    }


    @Test
    public void withdrawMoneyWithLowBalance() {
        fixture.given(
                AccountOpenedEvent.builder()
                        .accountId("12345")
                        .build(),
                MoneyDepositedEvent.builder()
                        .accountId("12345")
                        .amount(100)
                        .balance(100)
                        .build())
                .when(WithdrawMoneyCommand.builder()
                        .accountId("12345")
                        .amount(50)
                        .build())
                .expectSuccessfulHandlerExecution()
                .expectState(account -> {
                    assertThat(account.isOpen()).isTrue();
                    assertThat(account.getBalance()).isEqualTo(50);
                    assertThat(account.getAccountId()).isEqualTo("12345");
                });

        verify(notificationService).sendLowBalanceNotification("12345", 50);
    }

    @Test
    public void withdrawMoneyWithInsufficientFunds() {
        fixture.given(
                AccountOpenedEvent.builder()
                        .accountId("12345")
                        .build(),
                MoneyDepositedEvent.builder()
                        .accountId("12345")
                        .amount(10)
                        .balance(10)
                        .build())
                .when(WithdrawMoneyCommand.builder()
                        .accountId("12345")
                        .amount(11)
                        .build())
                .expectException(InsufficientFundsException.class);
    }

    @Test
    public void withdrawMoneyWithClosedAccount() {
        fixture.given(
                AccountOpenedEvent.builder()
                        .accountId("12345")
                        .build(),
                AccountClosedEvent.builder()
                        .accountId("12345")
                        .build())
                .when(WithdrawMoneyCommand.builder()
                        .accountId("12345")
                        .amount(11)
                        .build())
                .expectException(AccountClosedException.class);
    }

    @Test
    public void closeAccount() {
        fixture.given(
                AccountOpenedEvent.builder()
                        .accountId("12345")
                        .build())
                .when(CloseAccountCommand.builder()
                        .accountId("12345")
                        .build())
                .expectSuccessfulHandlerExecution()
                .expectState(account -> {
                    assertThat(account.isOpen()).isFalse();
                    assertThat(account.getBalance()).isEqualTo(0);
                    assertThat(account.getAccountId()).isEqualTo("12345");
                });
    }

    @Test
    public void closeAccountWithPositiveBalance() {
        fixture.given(
                AccountOpenedEvent.builder()
                        .accountId("12345")
                        .build(),
                MoneyDepositedEvent.builder()
                        .accountId("12345")
                        .amount(5)
                        .balance(5)
                        .build())
                .when(CloseAccountCommand.builder()
                        .accountId("12345")
                        .build())
                .expectException(OutstandingBalanceException.class);
    }

    @Test
    public void closeAccountWithClosedAccount() {
        fixture.given(
                AccountOpenedEvent.builder()
                        .accountId("12345")
                        .build(),
                AccountClosedEvent.builder()
                        .accountId("12345")
                        .build())
                .when(CloseAccountCommand.builder()
                        .accountId("12345")
                        .build())
                .expectException(AccountClosedException.class);
    }

}