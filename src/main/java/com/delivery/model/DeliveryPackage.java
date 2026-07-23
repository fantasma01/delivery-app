package com.delivery.model;

public class DeliveryPackage {
    private int id;
    private String description;
    private double weight;
    private String address;
    private String status;
    private Integer driverId;
    private Integer customerId;
    private String createdAt;
    private String deliveredAt;

    public DeliveryPackage() {}

    public DeliveryPackage(String description, double weight, String address, Integer customerId) {
        this.description = description;
        this.weight = weight;
        this.address = address;
        this.customerId = customerId;
        this.status = "PENDING";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public double getWeight() { return weight; }
    public void setWeight(double w) { this.weight = w; }
    public String getAddress() { return address; }
    public void setAddress(String a) { this.address = a; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer id) { this.driverId = id; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer id) { this.customerId = id; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String d) { this.createdAt = d; }
    public String getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(String d) { this.deliveredAt = d; }
}
