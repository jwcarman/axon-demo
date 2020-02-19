package com.callibrity.axon.readmodel.register.projection;

import com.callibrity.axon.domain.event.MoneyDepositedEvent;
import com.callibrity.axon.domain.event.MoneyWithdrawnEvent;
import com.callibrity.axon.readmodel.register.entity.Transaction;
import com.callibrity.axon.readmodel.register.query.FindAllTransactionsQuery;
import com.callibrity.axon.readmodel.register.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ProcessingGroup("account-register")
public class AccountRegisterProjection {

    private final TransactionRepository repository;

    @EventHandler
    public void on(MoneyDepositedEvent event) {
        repository.save(Transaction.builder()
                .accountId(event.getAccountId())
                .balance(event.getBalance())
                .amount(event.getAmount())
                .build());
    }

    @EventHandler
    public void on(MoneyWithdrawnEvent event) {
        repository.save(Transaction.builder()
                .accountId(event.getAccountId())
                .balance(event.getBalance())
                .amount(-event.getAmount())
                .build());
    }

    @QueryHandler
    public List<Transaction> handle(FindAllTransactionsQuery query) {
        return repository.findByAccountId(query.getAccountId());
    }
}
