package com.delivery.model;

public class Package {
    private int id;
    private String description;
    private double weight;
    private String address;
    private String status;
    private int driverId;

    public Package() {}

    public Package(String description, double weight, String address) {
        this.description = description;
        this.weight = weight;
        this.address = address;
        this.status = "PENDING";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String toString() {
        return id + " | " + description + " | " + weight + "kg | " + address + " | " + status;
    }
}
