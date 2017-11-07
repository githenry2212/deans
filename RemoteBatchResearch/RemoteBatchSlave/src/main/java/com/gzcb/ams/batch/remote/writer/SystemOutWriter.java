package com.gzcb.ams.batch.remote.writer;

import com.gzcb.ams.batch.remote.beans.CreditBill;
import org.springframework.batch.item.ItemWriter;

import java.security.SecureRandom;
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
