package com.delivery.service;

import com.delivery.dao.DriverDAO;
import com.delivery.dao.PackageDAO;
import com.delivery.model.Driver;
import com.delivery.model.Package;
import java.util.List;
import java.util.Scanner;

public class DeliveryService {
    private final DriverDAO driverDAO = new DriverDAO();
    private final PackageDAO packageDAO = new PackageDAO();
    private final Scanner sc = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n=== DELIVERY MANAGEMENT ===");
            System.out.println("1. Add driver");
            System.out.println("2. List drivers");
            System.out.println("3. Add package");
            System.out.println("4. List pending packages");
            System.out.println("5. Assign driver to package");
            System.out.println("6. List in-transit packages");
            System.out.println("7. Mark package delivered");
            System.out.println("8. List all packages");
            System.out.println("0. Exit");
            System.out.print("Choice: ");

            String input = sc.nextLine();
            switch (input) {
                case "1" -> addDriver();
                case "2" -> listDrivers();
                case "3" -> addPackage();
                case "4" -> listByStatus("PENDING");
                case "5" -> assign();
                case "6" -> listByStatus("IN_TRANSIT");
                case "7" -> deliver();
                case "8" -> allPackages();
                case "0" -> { System.out.println("Bye"); return; }
                default -> System.out.println("Invalid");
            }
        }
    }

    private void addDriver() {
        System.out.print("Name: "); String name = sc.nextLine();
        System.out.print("Phone: "); String phone = sc.nextLine();
        System.out.print("License plate: "); String plate = sc.nextLine();
        driverDAO.add(new Driver(name, phone, plate));
        System.out.println("Driver added");
    }

    private void listDrivers() {
        List<Driver> list = driverDAO.all();
        if (list.isEmpty()) { System.out.println("No drivers"); return; }
        list.forEach(System.out::println);
    }

    private void addPackage() {
        System.out.print("Description: "); String desc = sc.nextLine();
        System.out.print("Weight (kg): "); double w = Double.parseDouble(sc.nextLine());
        System.out.print("Delivery address: "); String addr = sc.nextLine();
        packageDAO.add(new Package(desc, w, addr));
        System.out.println("Package added");
    }

    private void listByStatus(String status) {
        List<Package> list = packageDAO.byStatus(status);
        if (list.isEmpty()) { System.out.println("No " + status + " packages"); return; }
        list.forEach(System.out::println);
    }

    private void assign() {
        List<Package> pending = packageDAO.byStatus("PENDING");
        if (pending.isEmpty()) { System.out.println("No pending packages"); return; }
        System.out.println("Pending packages:");
        pending.forEach(p -> System.out.println(p.getId() + " - " + p.getDescription()));

        List<Driver> drivers = driverDAO.all();
        if (drivers.isEmpty()) { System.out.println("No drivers"); return; }
        System.out.println("Drivers:");
        drivers.forEach(d -> System.out.println(d.getId() + " - " + d.getName()));

        System.out.print("Package ID: "); int pid = Integer.parseInt(sc.nextLine());
        System.out.print("Driver ID: "); int did = Integer.parseInt(sc.nextLine());
        packageDAO.assignDriver(pid, did);
        System.out.println("Assigned");
    }

    private void deliver() {
        List<Package> transit = packageDAO.byStatus("IN_TRANSIT");
        if (transit.isEmpty()) { System.out.println("No in-transit packages"); return; }
        transit.forEach(p -> System.out.println(p.getId() + " - " + p.getDescription()));
        System.out.print("Package ID to mark delivered: ");
        int id = Integer.parseInt(sc.nextLine());
        packageDAO.markDelivered(id);
        System.out.println("Marked as delivered");
    }

    private void allPackages() {
        List<Package> list = packageDAO.all();
        if (list.isEmpty()) { System.out.println("No packages"); return; }
        list.forEach(System.out::println);
    }
}
