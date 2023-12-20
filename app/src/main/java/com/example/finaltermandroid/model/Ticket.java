package com.example.finaltermandroid.model;

public class Ticket {
    private String customerId;
    private String discountId;
    private String seatBookedId;
    private String serviceId;
    private long totalMoney;

    public Ticket(String customerId, String discountId, String seatBookedId, String serviceId, long totalMoney) {
        this.customerId = customerId;
        this.discountId = discountId;
        this.seatBookedId = seatBookedId;
        this.serviceId = serviceId;
        this.totalMoney = totalMoney;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public String getSeatBookedId() {
        return seatBookedId;
    }

    public void setSeatBookedId(String seatBookedId) {
        this.seatBookedId = seatBookedId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }
}
