package com.delivery;

import com.delivery.dao.Database;
import com.delivery.service.DeliveryService;

public class App {
    public static void main(String[] args) {
        Database.init();
        System.out.println("Database ready");
        new DeliveryService().run();
    }
}
