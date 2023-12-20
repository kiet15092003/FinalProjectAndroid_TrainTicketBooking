package com.example.finaltermandroid.model;

public class SeatBooked {
    private String trainSchedule;
    private String seatNumber;
    private long price;
    public SeatBooked(){

    }
    public SeatBooked(String trainSchedule, String seatNumber, long price) {
        this.trainSchedule = trainSchedule;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public String getTrainSchedule() {
        return trainSchedule;
    }

    public void setTrainSchedule(String trainSchedule) {
        this.trainSchedule = trainSchedule;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
