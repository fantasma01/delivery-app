package com.delivery.dao;

import com.delivery.model.Package;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackageDAO {
    public void add(Package p) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO packages (description, weight, address, status) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, p.getDescription());
            ps.setDouble(2, p.getWeight());
            ps.setString(3, p.getAddress());
            ps.setString(4, p.getStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to add package", e);
        }
    }

    public List<Package> all() {
        List<Package> list = new ArrayList<>();
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM packages")) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to list packages", e);
        }
        return list;
    }

    public List<Package> byStatus(String status) {
        List<Package> list = new ArrayList<>();
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM packages WHERE status = ?")) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to filter packages", e);
        }
        return list;
    }

    public void assignDriver(int packageId, int driverId) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE packages SET driver_id = ?, status = 'IN_TRANSIT' WHERE id = ?")) {
            ps.setInt(1, driverId);
            ps.setInt(2, packageId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign driver", e);
        }
    }

    public void markDelivered(int packageId) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE packages SET status = 'DELIVERED' WHERE id = ?")) {
            ps.setInt(1, packageId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark delivered", e);
        }
    }

    private Package map(ResultSet rs) throws SQLException {
        Package p = new Package();
        p.setId(rs.getInt("id"));
        p.setDescription(rs.getString("description"));
        p.setWeight(rs.getDouble("weight"));
        p.setAddress(rs.getString("address"));
        p.setStatus(rs.getString("status"));
        p.setDriverId(rs.getInt("driver_id"));
        return p;
    }
}
