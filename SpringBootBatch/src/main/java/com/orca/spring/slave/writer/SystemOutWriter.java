package com.orca.spring.slave.writer;

import com.orca.spring.beans.CreditBill;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class SystemOutWriter implements ItemWriter<CreditBill> {
    @Override
    public void write(List<? extends CreditBill> items) throws Exception {
        System.out.println(items);
//        if(new SecureRandom().nextInt(100)%2 == 0){
//            throw new IllegalStateException("ex");
//        }
    }
}
