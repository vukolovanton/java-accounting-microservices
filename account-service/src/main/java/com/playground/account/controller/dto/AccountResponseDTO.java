package com.playground.account.controller.dto;

import com.playground.account.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class AccountResponseDTO {

    public Long accountId;
    private String name;
    private String email;
    private String phone;
    private OffsetDateTime creationDate;

    private List<Long> bills;

    public AccountResponseDTO(Account account) {
        accountId = account.getAccountId();
        name = account.getName();
        email = account.getEmail();
        phone = account.getPhone();
        creationDate = account.getCreationDate();

        bills = account.getBills();
    }
}
