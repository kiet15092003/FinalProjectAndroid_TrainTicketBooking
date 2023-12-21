package com.example.finaltermandroid.model;

public class Discount {
    private String accountEmail;
    private String discountKey;
    private double discountValue;
    private boolean status;
    public Discount(){

    }
    public Discount(String accountEmail, String discountKey, double discountValue, boolean status) {
        this.accountEmail = accountEmail;
        this.discountKey = discountKey;
        this.discountValue = discountValue;
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getDiscountKey() {
        return discountKey;
    }

    public void setDiscountKey(String discountKey) {
        this.discountKey = discountKey;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }
}
