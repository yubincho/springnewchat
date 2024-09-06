package com.example.banktest.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionEnum {

    WITHDRAW("출금"), DEPOSIT("입금"), TRANSACTION("이체"), ALL("입출금내역");

    private String value;

}
