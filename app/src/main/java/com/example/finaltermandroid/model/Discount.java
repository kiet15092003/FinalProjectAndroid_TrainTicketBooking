package com.example.finaltermandroid.model;

public class Discount {
    private String accountEmail;
    private String discountKey;
    private double discountValue;

    public Discount(){

    }
    public Discount(String accountEmail, String discountKey, double discountValue) {
        this.accountEmail = accountEmail;
        this.discountKey = discountKey;
        this.discountValue = discountValue;
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
