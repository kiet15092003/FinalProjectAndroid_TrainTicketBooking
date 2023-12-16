package com.example.finaltermandroid.model;

public class TrainSchedule {
    private String departureTime;
    private String destinationTime;
    private long price;
    private String stationSchedule;
    private String trainNumber;

    public TrainSchedule(){

    }

    public TrainSchedule(String departureTime, String destinationTime, long price, String stationSchedule, String trainNumber) {
        this.departureTime = departureTime;
        this.destinationTime = destinationTime;
        this.price = price;
        this.stationSchedule = stationSchedule;
        this.trainNumber = trainNumber;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDestinationTime() {
        return destinationTime;
    }

    public void setDestinationTime(String destinationTime) {
        this.destinationTime = destinationTime;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getStationSchedule() {
        return stationSchedule;
    }

    public void setStationSchedule(String stationSchedule) {
        this.stationSchedule = stationSchedule;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }
}
