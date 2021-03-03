package com.playground.deposit.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    public Long accountId;
    private String name;
    private String email;
    private String phone;
    private OffsetDateTime creationDate;
}