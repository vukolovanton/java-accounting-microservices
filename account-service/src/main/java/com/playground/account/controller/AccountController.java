package com.playground.account.controller;

import com.playground.account.controller.dto.AccountRequestDTO;
import com.playground.account.controller.dto.AccountResponseDTO;
import com.playground.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}")
    public AccountResponseDTO getAccount(@PathVariable Long accountId) {
        return new AccountResponseDTO(accountService.getAccountById(accountId));
    }

    @PostMapping("/")
    public Long createAccount(@RequestBody AccountRequestDTO accountRequestDTO) {
        return accountService.createAccount(
                accountRequestDTO.getName(),
                accountRequestDTO.getEmail(),
                accountRequestDTO.getPhone(),
                accountRequestDTO.getBills()
        );
    }

    @PutMapping("/{accountId}")
    public AccountResponseDTO updateAccount(
            @PathVariable Long accountId,
            @RequestBody AccountRequestDTO accountRequestDTO) {

        return new AccountResponseDTO(accountService.updateAccount(
                accountId,
                accountRequestDTO.getName(),
                accountRequestDTO.getEmail(),
                accountRequestDTO.getPhone(),
                accountRequestDTO.getBills()
        ));
    }

    @DeleteMapping("/{accountId}")
    public AccountResponseDTO deleteAccount(@PathVariable Long accountId) {
        return new AccountResponseDTO(accountService.deleteAccount(accountId));
    }

}
