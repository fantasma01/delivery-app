package com.delivery.dao;

import com.delivery.model.Driver;
import com.delivery.model.DeliveryPackage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    @BeforeEach
    void setUp() {
        Database.init();
    }

    @Test void connectionWorks() {
        assertDoesNotThrow(() -> Database.connect());
    }
}
