package com.callibrity.axon.readmodel.balance.projection;

import com.callibrity.axon.domain.event.AccountClosedEvent;
import com.callibrity.axon.domain.event.AccountOpenedEvent;
import com.callibrity.axon.domain.event.MoneyDepositedEvent;
import com.callibrity.axon.domain.event.MoneyWithdrawnEvent;
import com.callibrity.axon.readmodel.balance.entity.AccountBalance;
import com.callibrity.axon.readmodel.balance.query.FindAccountBalanceQuery;
import com.callibrity.axon.readmodel.balance.repository.AccountBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@ProcessingGroup("account-balance")
public class AccountBalanceProjection {

    private final AccountBalanceRepository repository;

    @EventHandler
    public void on(AccountOpenedEvent event) {
        repository.save(AccountBalance.builder()
                .accountId(event.getAccountId())
                .balance(0)
                .build());
    }

    @EventHandler
    public void on(MoneyDepositedEvent event) {
        updateBalance(event.getAccountId(), event.getBalance());
    }

    @EventHandler
    public void on(MoneyWithdrawnEvent event) {
        final int balance = event.getBalance();
        final String accountId = event.getAccountId();
        updateBalance(accountId, balance);
    }

    @EventHandler
    public void on(AccountClosedEvent event) {
        repository.deleteById(event.getAccountId());
    }

    @QueryHandler
    public Optional<AccountBalance> handle(FindAccountBalanceQuery query) {
        return repository.findById(query.getAccountId());
    }

    private void updateBalance(String accountId, int balance) {
        repository.findById(accountId).ifPresent(account -> account.setBalance(balance));
    }
}
