package com.delivery.dao;

import com.delivery.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public void add(Customer c) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO customers (name, phone, address) VALUES (?, ?, ?)")) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getAddress());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to add customer", e);
        }
    }

    public List<Customer> all() {
        List<Customer> list = new ArrayList<>();
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {
            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setAddress(rs.getString("address"));
                list.add(c);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to list customers", e);
        }
        return list;
    }

    public Customer byId(int id) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setPhone(rs.getString("phone"));
                    c.setAddress(rs.getString("address"));
                    return c;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to find customer", e);
        }
        return null;
    }
}
