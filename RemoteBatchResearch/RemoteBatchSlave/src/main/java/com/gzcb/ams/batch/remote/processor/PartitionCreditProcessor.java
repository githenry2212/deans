package com.gzcb.ams.batch.remote.processor;

import com.gzcb.ams.batch.remote.beans.CreditBill;
import org.springframework.batch.item.ItemProcessor;

public class PartitionCreditProcessor implements ItemProcessor<CreditBill,CreditBill> {
    @Override
    public CreditBill process(CreditBill item) throws Exception {
        item.setAmount(item.getAmount() + 100);
        return item;
    }
}
