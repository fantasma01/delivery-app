package com.delivery.model;

public class Driver {
    private int id;
    private String name;
    private String phone;
    private String licensePlate;

    public Driver() {}

    public Driver(String name, String phone, String licensePlate) {
        this.name = name;
        this.phone = phone;
        this.licensePlate = licensePlate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public String getPhone() { return phone; }
    public void setPhone(String p) { this.phone = p; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String l) { this.licensePlate = l; }
}
