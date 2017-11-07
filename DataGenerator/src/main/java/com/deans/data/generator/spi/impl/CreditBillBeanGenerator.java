package com.deans.data.generator.spi.impl;

import com.deans.data.generator.beans.CreditBill;
import com.deans.data.generator.spi.BeanGenerator;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class CreditBillBeanGenerator implements BeanGenerator<CreditBill> {

    private static final char[] SURNAME = {'邓', '陈', '黄', '杨', '袁', '刘', '全', '郝', '赵', '钱',
            '孙', '李', '周', '吴', '郑', '王', '冯', '朱', '喻', '许'};
    private static final String[] NAME = {"近南", "青", "芹", "正", "家洛", "小宝", "靖", "康", "峰",
            "竹", "誉", "林", "信", "宇通", "雨桐", "荣", "燕", "炎", "爱家", "爱国", "爱民", "爱党",
            "建军", "源", "默", "环", "建", "力", "亮", "懿", "攸", "彧", "群", "羽", "备", "飞",
            "权", "坚", "策", "超", "月华", "殿英", "世凯", "中山", "中正", "介石"};
    private static final String[] ADDRESSES = {"天河南路", "天河北路", "天河路", "海文路", "体育西路", "滨江东路", "茶子山路"};


    @Override
    public CreditBill generate() {
        CreditBill creditBill = new CreditBill();
        SecureRandom random = new SecureRandom(String.valueOf(System.nanoTime()).getBytes(Charset.forName("UTF-8")));
        creditBill.setAccountID(String.valueOf(System.currentTimeMillis()) + getRandomCardNumber(random));
        creditBill.setAmount(random.nextInt(10000) + random.nextDouble());
        creditBill.setName(SURNAME[random.nextInt(SURNAME.length)] + NAME[random.nextInt(NAME.length)]);
        creditBill.setAddress(ADDRESSES[random.nextInt(ADDRESSES.length)]);
        creditBill.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        return creditBill;
    }

    private String getRandomCardNumber(SecureRandom random) {
        return new DecimalFormat("000").format(random.nextInt(1000));
    }
}
