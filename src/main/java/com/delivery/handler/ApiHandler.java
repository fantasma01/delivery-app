package com.delivery.handler;

import com.delivery.dao.CustomerDAO;
import com.delivery.dao.DriverDAO;
import com.delivery.dao.PackageDAO;
import com.delivery.model.Customer;
import com.delivery.model.DeliveryPackage;
import com.delivery.model.Driver;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class ApiHandler implements HttpHandler {
    private final DriverDAO drivers = new DriverDAO();
    private final PackageDAO packages = new PackageDAO();
    private final CustomerDAO customers = new CustomerDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();
            String query = ex.getRequestURI().getQuery();

            String json;
            if (path.equals("/api/drivers") && method.equals("GET")) {
                json = toJson(drivers.all());
            } else if (path.equals("/api/drivers") && method.equals("POST")) {
                Driver d = readBody(ex, Driver.class);
                drivers.add(d);
                json = "{\"ok\":true}";
            } else if (path.equals("/api/customers") && method.equals("GET")) {
                json = toJson(customers.all());
            } else if (path.equals("/api/customers") && method.equals("POST")) {
                Customer c = readBody(ex, Customer.class);
                customers.add(c);
                json = "{\"ok\":true}";
            } else if (path.equals("/api/packages") && method.equals("GET")) {
                if (query != null && query.contains("status=")) {
                    String status = query.split("status=")[1].split("&")[0];
                    json = toJson(packages.byStatus(status));
                } else if (query != null && query.contains("search=")) {
                    String term = query.split("search=")[1].split("&")[0];
                    json = toJson(packages.search(term));
                } else {
                    json = toJson(packages.all());
                }
            } else if (path.equals("/api/packages") && method.equals("POST")) {
                DeliveryPackage p = readBody(ex, DeliveryPackage.class);
                packages.add(p);
                json = "{\"ok\":true}";
            } else if (path.matches("/api/packages/\\d+/assign") && method.equals("PUT")) {
                int pid = Integer.parseInt(path.split("/")[3]);
                int did = Integer.parseInt(getParam(query, "driverId"));
                packages.assignDriver(pid, did);
                json = "{\"ok\":true}";
            } else if (path.matches("/api/packages/\\d+/deliver") && method.equals("PUT")) {
                int pid = Integer.parseInt(path.split("/")[3]);
                packages.markDelivered(pid);
                json = "{\"ok\":true}";
            } else if (path.equals("/api/stats") && method.equals("GET")) {
                json = "{" +
                    "\"total\":" + packages.totalCount() + "," +
                    "\"pending\":" + packages.countByStatus("PENDING") + "," +
                    "\"inTransit\":" + packages.countByStatus("IN_TRANSIT") + "," +
                    "\"delivered\":" + packages.countByStatus("DELIVERED") +
                "}";
            } else {
                json = "{\"error\":\"not found\"}";
                ex.sendResponseHeaders(404, json.length());
            }

            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            ex.sendResponseHeaders(200, bytes.length);
            ex.getResponseBody().write(bytes);
        } catch (Exception e) {
            String err = "{\"error\":\"" + e.getMessage() + "\"}";
            byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json");
            ex.sendResponseHeaders(500, bytes.length);
            ex.getResponseBody().write(bytes);
        } finally {
            ex.getResponseBody().close();
        }
    }

    private String getParam(String query, String key) {
        if (query == null) return null;
        for (String p : query.split("&")) {
            String[] kv = p.split("=");
            if (kv[0].equals(key)) return kv.length > 1 ? kv[1] : "";
        }
        return null;
    }

    private <T> T readBody(HttpExchange ex, Class<T> clz) {
        String body = new BufferedReader(new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8))
            .lines().collect(Collectors.joining());
        // simple JSON parse for flat objects
        try {
            T obj = clz.getDeclaredConstructor().newInstance();
            for (String pair : body.replaceAll("[{}\"]", "").split(",")) {
                String[] kv = pair.split(":", 2);
                if (kv.length < 2) continue;
                String key = kv[0].trim();
                String val = kv[1].trim();
                var f = clz.getDeclaredField(key);
                f.setAccessible(true);
                if (f.getType() == int.class || f.getType() == Integer.class) {
                    f.set(obj, Integer.parseInt(val));
                } else if (f.getType() == double.class || f.getType() == Double.class) {
                    f.set(obj, Double.parseDouble(val));
                } else if (val.equals("null")) {
                    f.set(obj, null);
                } else {
                    f.set(obj, val);
                }
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse body", e);
        }
    }

    private String toJson(Object obj) {
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJsonObj(list.get(i)));
            }
            return sb.append("]").toString();
        }
        return toJsonObj(obj);
    }

    private String toJsonObj(Object obj) {
        if (obj == null) return "null";
        try {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (var f : obj.getClass().getDeclaredFields()) {
                if (!first) sb.append(",");
                first = false;
                f.setAccessible(true);
                Object val = f.get(obj);
                sb.append("\"").append(f.getName()).append("\":");
                if (val == null) sb.append("null");
                else if (val instanceof Number) sb.append(val);
                else sb.append("\"").append(val.toString().replace("\"", "\\\"")).append("\"");
            }
            return sb.append("}").toString();
        } catch (Exception e) {
            return "{}";
        }
    }
}
