package com.playground.deposit.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "bill-service")
public interface BillServiceClient {

    @RequestMapping(value = "bills/{billId}", method = RequestMethod.GET)
    BillResponse getBillById(@PathVariable("billId") Long billId);

    @RequestMapping(value = "bills/{billId}", method = RequestMethod.PUT)
    void update(@PathVariable("billId") Long billId, BillRequest billRequest);

    @RequestMapping(value = "bills/account/{accountId}", method = RequestMethod.GET)
    List<BillResponse> getBillsByAccountId(@PathVariable("accountId") Long accountId);

}
