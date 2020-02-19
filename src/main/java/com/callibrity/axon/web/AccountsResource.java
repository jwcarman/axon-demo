package com.callibrity.axon.web;

import com.callibrity.axon.domain.command.CloseAccountCommand;
import com.callibrity.axon.domain.command.DepositMoneyCommand;
import com.callibrity.axon.domain.command.OpenAccountCommand;
import com.callibrity.axon.domain.command.WithdrawMoneyCommand;
import com.callibrity.axon.readmodel.balance.entity.AccountBalance;
import com.callibrity.axon.readmodel.balance.query.FindAccountBalanceQuery;
import com.callibrity.axon.readmodel.register.entity.Transaction;
import com.callibrity.axon.readmodel.register.query.FindAllTransactionsQuery;
import com.callibrity.axon.web.msg.AccountBalanceResponse;
import com.callibrity.axon.web.msg.AccountRegisterResponse;
import com.callibrity.axon.web.msg.DepositMoneyRequest;
import com.callibrity.axon.web.msg.OpenAccountRequest;
import com.callibrity.axon.web.msg.OpenAccountResponse;
import com.callibrity.axon.web.msg.TransactionDto;
import com.callibrity.axon.web.msg.WithdrawMoneyRequest;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsResource {

    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    @PostMapping
    public OpenAccountResponse openAccount(@RequestBody OpenAccountRequest request) {
        final String accountId = commandGateway.sendAndWait(OpenAccountCommand.builder()
                .openingBalance(request.getOpeningBalance())
                .build());
        return OpenAccountResponse.builder()
                .accountId(accountId)
                .build();
    }

    @PostMapping("/{accountId}/deposits")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void depositMoney(@PathVariable("accountId") String accountId, @RequestBody DepositMoneyRequest request) {
        commandGateway.send(DepositMoneyCommand.builder()
                .accountId(accountId)
                .amount(request.getAmount())
                .build());
    }

    @PostMapping("/{accountId}/withdrawals")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void withdrawMoney(@PathVariable("accountId") String accountId, @RequestBody WithdrawMoneyRequest request) {
        commandGateway.send(WithdrawMoneyCommand.builder()
                .accountId(accountId)
                .amount(request.getAmount())
                .build());
    }

    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void closeAccount(@PathVariable("accountId") String accountId) {
        commandGateway.send(CloseAccountCommand.builder()
                .accountId(accountId)
                .build());
    }

    @GetMapping("/{accountId}/balance")
    public Future<AccountBalanceResponse> accountBalance(@PathVariable("accountId") String accountId) {
        return queryGateway.query(new FindAccountBalanceQuery(accountId),
                ResponseTypes.optionalInstanceOf(AccountBalance.class))
                .thenApplyAsync(accountBalance -> accountBalance.map(ab ->
                        AccountBalanceResponse.builder()
                                .balance(ab.getBalance())
                                .build())
                        .orElse(null));
    }

    @GetMapping("/{accountId}/register")
    public Future<AccountRegisterResponse> accountRegister(@PathVariable("accountId") String accountId) {
        final FindAllTransactionsQuery query = FindAllTransactionsQuery.builder()
                .accountId(accountId)
                .build();

        return queryGateway.query(query, ResponseTypes.multipleInstancesOf(Transaction.class))
                .thenApplyAsync(transactions -> transactions.stream()
                        .map(tx -> TransactionDto.builder()
                                .amount(tx.getAmount())
                                .balance(tx.getBalance())
                                .build()).collect(Collectors.toList()))
                .thenApplyAsync(AccountRegisterResponse::new);
    }

}
