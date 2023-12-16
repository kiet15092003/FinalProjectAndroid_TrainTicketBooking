package com.example.finaltermandroid.model;

public class TrainStation {
    String city, name, id;
    public TrainStation(){

    }

    public TrainStation(String city, String name, String id) {
        this.city = city;
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
