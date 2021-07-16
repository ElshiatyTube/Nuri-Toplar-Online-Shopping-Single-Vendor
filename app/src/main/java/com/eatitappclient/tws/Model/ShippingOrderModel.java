package com.eatitappclient.tws.Model;

public class ShippingOrderModel {
    private String key, shipperPhone,shippingName;
    private double currentLat,currentLng;
    private Order orderModel;
    private boolean isStartTrip;
    private String estimateTime;

    public ShippingOrderModel() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getShipperPhone() {
        return shipperPhone;
    }

    public void setShipperPhone(String shipperPhone) {
        this.shipperPhone = shipperPhone;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public Order getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(Order orderModel) {
        this.orderModel = orderModel;
    }

    public boolean isStartTrip() {
        return isStartTrip;
    }

    public void setStartTrip(boolean startTrip) {
        isStartTrip = startTrip;
    }

    public String getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }
}
