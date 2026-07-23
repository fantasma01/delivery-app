package com.delivery;

import com.delivery.dao.Database;
import com.delivery.handler.ApiHandler;
import com.delivery.handler.StaticFileHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws Exception {
        Database.init();
        System.out.println("Database ready");

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api", new ApiHandler());

        String webDir = System.getProperty("web.dir", "src/main/resources/web");
        server.createContext("/", new StaticFileHandler(Paths.get(webDir).toAbsolutePath()));

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("Server running at http://localhost:8080");
    }
}
