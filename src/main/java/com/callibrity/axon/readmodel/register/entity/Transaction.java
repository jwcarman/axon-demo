package com.callibrity.axon.readmodel.register.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(indexes = @Index(name = "account_id_ndx", columnList = "accountId"))
public class Transaction {
    @Id
    private final String id = UUID.randomUUID().toString();

    private String accountId;

    private int amount;

    private int balance;

}
