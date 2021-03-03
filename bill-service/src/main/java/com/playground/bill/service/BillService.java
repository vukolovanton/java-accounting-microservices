package com.playground.bill.service;

import com.playground.bill.entity.Bill;
import com.playground.bill.exceptions.BillNotFoundException;
import com.playground.bill.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class BillService {
    private BillRepository billRepository;

    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Bill getBillById(Long billId) {
        return billRepository
                .findById(billId)
                .orElseThrow(() -> new BillNotFoundException("Unable to found bill " + billId));
    }

    public Long createBill(
                    Long accountId,
                    BigDecimal amount,
                    Boolean isDefault,
                    Boolean overdraftEnabled) {
        Bill bill = new Bill(accountId, amount, isDefault, OffsetDateTime.now(), overdraftEnabled);
        return billRepository.save(bill).getBillId();
    }

    public Bill updateBill(
                    Long billId,
                    Long accountId,
                    BigDecimal amount,
                    Boolean isDefault,
                    Boolean overdraftEnabled) {
            Bill bill = new Bill(accountId, amount, isDefault, overdraftEnabled);
            bill.setBillId(billId);
            return billRepository.save(bill);
    }

    public Bill deleteBill(Long billId) {
        Bill bill = getBillById(billId);
        billRepository.deleteById(billId);
        return bill;
    }

    public List<Bill> getBillsByAccountId(Long accountId) {
        return billRepository.getBillsByAccountId(accountId);
    }

}
