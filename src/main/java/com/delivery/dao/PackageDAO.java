package com.delivery.dao;

import com.delivery.model.DeliveryPackage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackageDAO {
    public void add(DeliveryPackage p) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO packages (description, weight, address, status, customer_id) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, p.getDescription());
            ps.setDouble(2, p.getWeight());
            ps.setString(3, p.getAddress());
            ps.setString(4, p.getStatus());
            if (p.getCustomerId() != null) ps.setInt(5, p.getCustomerId());
            else ps.setNull(5, Types.INTEGER);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to add package", e);
        }
    }

    public List<DeliveryPackage> all() {
        return query("SELECT * FROM packages ORDER BY created_at DESC");
    }

    public List<DeliveryPackage> byStatus(String status) {
        return query("SELECT * FROM packages WHERE status = ? ORDER BY created_at DESC", status);
    }

    public List<DeliveryPackage> search(String term) {
        return query("SELECT * FROM packages WHERE description LIKE ? OR address LIKE ? ORDER BY created_at DESC",
            "%" + term + "%", "%" + term + "%");
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
                 "UPDATE packages SET status = 'DELIVERED', delivered_at = datetime('now') WHERE id = ?")) {
            ps.setInt(1, packageId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark delivered", e);
        }
    }

    public int countByStatus(String status) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT COUNT(*) FROM packages WHERE status = ?")) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to count", e);
        }
        return 0;
    }

    public int totalCount() {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM packages")) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("Failed to count", e);
        }
        return 0;
    }

    private List<DeliveryPackage> query(String sql, String... params) {
        List<DeliveryPackage> list = new ArrayList<>();
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setString(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Query failed", e);
        }
        return list;
    }

    private DeliveryPackage map(ResultSet rs) throws SQLException {
        DeliveryPackage p = new DeliveryPackage();
        p.setId(rs.getInt("id"));
        p.setDescription(rs.getString("description"));
        p.setWeight(rs.getDouble("weight"));
        p.setAddress(rs.getString("address"));
        p.setStatus(rs.getString("status"));

        int did = rs.getInt("driver_id");
        p.setDriverId(rs.wasNull() ? null : did);

        int cid = rs.getInt("customer_id");
        p.setCustomerId(rs.wasNull() ? null : cid);

        p.setCreatedAt(rs.getString("created_at"));
        p.setDeliveredAt(rs.getString("delivered_at"));
        return p;
    }
}
