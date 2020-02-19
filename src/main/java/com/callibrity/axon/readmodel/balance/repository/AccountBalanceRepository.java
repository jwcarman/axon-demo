package com.callibrity.axon.readmodel.balance.repository;

import com.callibrity.axon.readmodel.balance.entity.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBalanceRepository extends JpaRepository<AccountBalance, String> {
}
