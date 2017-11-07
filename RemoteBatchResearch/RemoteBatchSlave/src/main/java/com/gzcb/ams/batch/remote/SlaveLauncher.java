package com.gzcb.ams.batch.remote;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SlaveLauncher {

    public static void main(String[] args) {
        AbstractXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:partition-slave.xml");
        try {
            System.out.println("press enter to close");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.destroy();
            context.close();
        }
    }
}
