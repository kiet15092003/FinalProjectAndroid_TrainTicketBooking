package com.example.finaltermandroid.model;

public class TrainCarriage {
    private int capacity;
    private int carriageNumber;
    private String seatType;
    public TrainCarriage(){

    }
    public TrainCarriage(int capacity, int carriageNumber, String seatType) {
        this.capacity = capacity;
        this.carriageNumber = carriageNumber;
        this.seatType = seatType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(int carriageNumber) {
        this.carriageNumber = carriageNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }
}
