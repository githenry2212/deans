package com.orca.spring.slave.processor;

import com.orca.spring.beans.CreditBill;
import org.springframework.batch.item.ItemProcessor;

public class PartitionCreditProcessor implements ItemProcessor<CreditBill, CreditBill> {
    @Override
    public CreditBill process(CreditBill item) throws Exception {
        item.setAmount(item.getAmount() + 100);
        return item;
    }
}
