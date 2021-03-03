package com.playground.deposit.controller;

import com.playground.deposit.DepositApplication;
import com.playground.deposit.dto.DepositRequest;
import com.playground.deposit.dto.DepositResponse;
import com.playground.deposit.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DepositController {

    private DepositService depositService;

    @Autowired
    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/deposit")
    public DepositResponse deposit(@RequestBody DepositRequest depositRequest) {
        return depositService
                .deposit(depositRequest.getAccountId(), depositRequest.getBillId(), depositRequest.getAmount());
    }
}
