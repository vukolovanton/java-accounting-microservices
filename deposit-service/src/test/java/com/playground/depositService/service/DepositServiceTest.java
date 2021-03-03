package com.playground.depositService.service;

import com.playground.deposit.dto.DepositResponse;
import com.playground.deposit.exceptions.DepositServiceException;
import com.playground.deposit.repository.DepositRepository;
import com.playground.deposit.rest.AccountResponse;
import com.playground.deposit.rest.AccountServiceClient;
import com.playground.deposit.rest.BillResponse;
import com.playground.deposit.rest.BillServiceClient;
import com.playground.deposit.service.DepositService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class DepositServiceTest {

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private BillServiceClient billServiceClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DepositService depositService;

    @Test
    public void depositServiceTest_withBillId() {
        BillResponse billResponse = createBillResponse();
        Mockito.when(billServiceClient.getBillById(ArgumentMatchers.anyLong())).thenReturn(billResponse);
        Mockito.when(accountServiceClient.getAccountById(ArgumentMatchers.anyLong())).thenReturn(createAccountResponse());
        DepositResponse depositResponse = depositService.deposit(null, 1L, BigDecimal.valueOf(1000));
        Assertions.assertThat(depositResponse.getMail()).isEqualTo("pickle@rick.com");
    }

    @Test(expected = DepositServiceException.class)
    public void depositServiceTest_exception() {
        depositService.deposit(null, null, BigDecimal.valueOf(1000));
    }

    private AccountResponse createAccountResponse() {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountId(1L);
        accountResponse.setBills(Arrays.asList(1L, 2L, 3L));
        accountResponse.setCreationDate(OffsetDateTime.now());
        accountResponse.setEmail("pickle@rick.com");
        accountResponse.setName("Rick");
        accountResponse.setPhone("+4815162342");

        return accountResponse;
    }

    private BillResponse createBillResponse() {
        BillResponse billResponse = new BillResponse();
        billResponse.setAccountId(1L);
        billResponse.setAmount(BigDecimal.valueOf(1000));
        billResponse.setBillId(1L);
        billResponse.setCreationDate(OffsetDateTime.now());
        billResponse.setIsDefault(true);
        billResponse.setOverdraftEnabled(true);

        return billResponse;
    }
}
