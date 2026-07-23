package com.delivery.dao;

import com.delivery.model.DeliveryPackage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class PackageDAOTest {
    private final PackageDAO dao = new PackageDAO();

    @BeforeEach
    void setUp() {
        Database.init();
        // Clean tables to isolate tests
        var conn = Database.connect();
        try (var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM packages");
        } catch (Exception ignored) {}
    }

    @Test void addAndList() {
        dao.add(new DeliveryPackage("Laptop", 2.5, "Calle 123", null));
        var list = dao.all();
        assertFalse(list.isEmpty());
        assertEquals("Laptop", list.getFirst().getDescription());
    }

    @Test void startsPending() {
        dao.add(new DeliveryPackage("Book", 0.5, "Av 456", null));
        var list = dao.byStatus("PENDING");
        assertFalse(list.isEmpty());
        assertEquals("PENDING", list.getFirst().getStatus());
    }

    @Test void assignAndDeliver() {
        dao.add(new DeliveryPackage("Tablet", 1.0, "Plaza 789", null));
        var list = dao.all();
        int id = list.getFirst().getId();

        dao.assignDriver(id, 1);
        assertEquals("IN_TRANSIT", dao.byStatus("IN_TRANSIT").getFirst().getStatus());

        dao.markDelivered(id);
        assertEquals("DELIVERED", dao.byStatus("DELIVERED").getFirst().getStatus());
    }

    @Test void search() {
        dao.add(new DeliveryPackage("Monitor 4K", 5.0, "Calle Nueva", null));
        var results = dao.search("Monitor");
        assertFalse(results.isEmpty());
    }
}
