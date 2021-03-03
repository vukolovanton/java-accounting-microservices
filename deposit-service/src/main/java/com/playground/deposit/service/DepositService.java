package com.playground.deposit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.deposit.dto.DepositResponse;
import com.playground.deposit.entity.Deposit;
import com.playground.deposit.exceptions.DepositServiceException;
import com.playground.deposit.repository.DepositRepository;
import com.playground.deposit.rest.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class DepositService {

    private static final String TOPIC_EXCHANGE_DEPOSIT = "js.deposit.notify.exchange";
    private static final String ROUTING_KEY_DEPOSIT = "js.key.deposit";
    
    private final DepositRepository depositRepository;
    private final AccountServiceClient accountServiceClient;
    private final BillServiceClient billServiceClient;
    private final RabbitTemplate rabbitTemplate;

    public DepositService(DepositRepository depositRepository, AccountServiceClient accountServiceClient, BillServiceClient billServiceClient, RabbitTemplate rabbitTemplate) {
        this.depositRepository = depositRepository;
        this.accountServiceClient = accountServiceClient;
        this.billServiceClient = billServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public DepositResponse deposit(Long accountId, Long billId, BigDecimal amount) {
        if (accountId == null && billId == null) {
            throw new DepositServiceException("Account and bill are null");
        }
        
        if (billId != null) {
            BillResponse billResponse = billServiceClient.getBillById(billId);
            BillRequest billRequest = new BillRequest();
            
            billRequest.setAccountId(billResponse.getAccountId());
            billRequest.setCreationDate(billResponse.getCreationDate());
            billRequest.setIsDefault(billResponse.getIsDefault());
            billRequest.setOverdraftEnabled(billResponse.getOverdraftEnabled());
            billRequest.setAmount(billResponse.getAmount().add(amount));
            
            billServiceClient.update(billId, billRequest);
            
            AccountResponse accountResponse = accountServiceClient.getAccountById(billResponse.getAccountId());
            depositRepository.save(new Deposit(amount, billId, OffsetDateTime.now(), accountResponse.getEmail()));

            return createResponse(amount, accountResponse);
        }
        
        BillResponse defaultBill = getDefaultBill(accountId);
        BillRequest billRequest = createBillRequest(amount, defaultBill);
        billServiceClient.update(defaultBill.getBillId(), billRequest);
        AccountResponse account = accountServiceClient.getAccountById(accountId);
        depositRepository.save(new Deposit(amount, defaultBill.getBillId(), OffsetDateTime.now(), account.getEmail() ));

        return createResponse(amount, account);
    }

    private DepositResponse createResponse(BigDecimal amount, AccountResponse accountResponse) {
        DepositResponse depositResponse = new DepositResponse(amount, accountResponse.getEmail());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_DEPOSIT, ROUTING_KEY_DEPOSIT, objectMapper.writeValueAsString(depositResponse));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DepositServiceException("Can't send a message to RabbitMQ");
        }

        return depositResponse;
    }

    private BillResponse getDefaultBill(Long accountId) {
        return billServiceClient
                .getBillsByAccountId(accountId)
                .stream()
                .filter(BillResponse::getIsDefault)
                .findAny()
                .orElseThrow(() -> new DepositServiceException("Unable to find default bill for an account"));
    }

    private BillRequest createBillRequest(BigDecimal amount, BillResponse billResponse) {
        BillRequest billRequest = new BillRequest();

        billRequest.setAccountId(billResponse.getAccountId());
        billRequest.setCreationDate(billResponse.getCreationDate());
        billRequest.setIsDefault(billResponse.getIsDefault());
        billRequest.setOverdraftEnabled(billResponse.getOverdraftEnabled());
        billRequest.setAmount(billResponse.getAmount().add(amount));

        return billRequest;
    }
}
