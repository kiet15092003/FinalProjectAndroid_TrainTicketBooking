package com.example.finaltermandroid.model;

public class Notification {
    private String accountEmail;
    private String message;
    private String time;
    public Notification(){

    }
    public Notification(String accountEmail, String message, String time) {
        this.accountEmail = accountEmail;
        this.message = message;
        this.time = time;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
