package com.delivery.dao;

import com.delivery.model.Driver;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    public void add(Driver d) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO drivers (name, phone, license_plate) VALUES (?, ?, ?)")) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getPhone());
            ps.setString(3, d.getLicensePlate());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to add driver", e);
        }
    }

    public List<Driver> all() {
        List<Driver> list = new ArrayList<>();
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM drivers")) {
            while (rs.next()) {
                Driver d = new Driver();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setPhone(rs.getString("phone"));
                d.setLicensePlate(rs.getString("license_plate"));
                list.add(d);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to list drivers", e);
        }
        return list;
    }

    public Driver byId(int id) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM drivers WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Driver d = new Driver();
                    d.setId(rs.getInt("id"));
                    d.setName(rs.getString("name"));
                    d.setPhone(rs.getString("phone"));
                    d.setLicensePlate(rs.getString("license_plate"));
                    return d;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to find driver", e);
        }
        return null;
    }
}
