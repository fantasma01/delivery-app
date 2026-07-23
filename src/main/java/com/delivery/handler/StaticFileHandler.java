package com.delivery.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.file.*;

public class StaticFileHandler implements HttpHandler {
    private final Path root;

    public StaticFileHandler(Path root) {
        this.root = root;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";

        // Try file system first
        Path file = root.resolve(path.substring(1)).normalize();
        if (file.startsWith(root) && Files.exists(file)) {
            serve(ex, file);
            return;
        }

        // Fallback to classpath (for fat jar)
        String resourcePath = "/web" + path;
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is != null) {
            String mime = mimeType(path);
            byte[] bytes = is.readAllBytes();
            is.close();
            ex.getResponseHeaders().set("Content-Type", mime);
            ex.sendResponseHeaders(200, bytes.length);
            ex.getResponseBody().write(bytes);
            ex.getResponseBody().close();
            return;
        }

        String notFound = "404 Not Found";
        ex.sendResponseHeaders(404, notFound.length());
        ex.getResponseBody().write(notFound.getBytes());
        ex.getResponseBody().close();
    }

    private void serve(HttpExchange ex, Path file) throws IOException {
        String mime = mimeType(file.toString());
        byte[] bytes = Files.readAllBytes(file);
        ex.getResponseHeaders().set("Content-Type", mime);
        ex.sendResponseHeaders(200, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    private String mimeType(String name) {
        if (name.endsWith(".html")) return "text/html; charset=utf-8";
        if (name.endsWith(".css")) return "text/css; charset=utf-8";
        if (name.endsWith(".js")) return "application/javascript; charset=utf-8";
        return "application/octet-stream";
    }
}
