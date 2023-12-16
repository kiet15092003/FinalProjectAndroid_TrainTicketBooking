package com.example.finaltermandroid.model;

public class Train {
    private String number;
    private int capacity;

    public Train(String number, int capacity) {
        this.number = number;
        this.capacity = capacity;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
