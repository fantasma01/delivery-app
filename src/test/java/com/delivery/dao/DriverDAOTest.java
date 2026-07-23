package com.delivery.dao;

import com.delivery.model.Driver;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DriverDAOTest {
    private final DriverDAO dao = new DriverDAO();

    @BeforeEach
    void setUp() {
        Database.init();
        try (var conn = Database.connect(); var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM drivers");
        } catch (Exception ignored) {}
    }

    @Test void addAndList() {
        dao.add(new Driver("Test Driver", "123", "ABC123"));
        var list = dao.all();
        assertFalse(list.isEmpty());
        assertEquals("Test Driver", list.getLast().getName());
    }

    @Test void findById() {
        dao.add(new Driver("Find Me", "456", "XYZ789"));
        var all = dao.all();
        Driver found = dao.byId(all.getLast().getId());
        assertNotNull(found);
        assertEquals("Find Me", found.getName());
    }
}
