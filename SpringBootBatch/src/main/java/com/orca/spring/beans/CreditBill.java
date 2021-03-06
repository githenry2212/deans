package com.orca.spring.beans;

public class CreditBill {
    private String id;
    /**
     * 银行卡账户ID
     */
    private String accountID;
    /**
     * 持卡人姓名
     */
    private String name;
    /**
     * 消费金额
     */
    private double amount = 0;
    /**
     * 消费日期 ，格式YYYY-MM-DD HH:MM:SS
     */
    private String date;
    /**
     * 消费场所
     **/
    private String address;


    public CreditBill() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id=" + getId() + ";accountID=" + getAccountID() + ";name=" + getName() + ";amount="
                + getAmount() + ";date=" + getDate() + ";address=" + getAddress());
        return sb.toString();
    }
}
