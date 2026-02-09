import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class ServerApp {

    private static final String BASE_DIR = System.getProperty("user.dir");

    private static void sendError(HttpExchange exchange, int statusCode) {
        try {
            exchange.sendResponseHeaders(statusCode, -1);
        } catch (IOException ignored) {
        } finally {
            exchange.close();
        }
    }

    private static void serveFile(HttpExchange exchange, String fileName) throws IOException {
        File file = new File(BASE_DIR, fileName);

        if (!file.exists() || file.isDirectory()) {
            System.out.println("❌ File not found: " + file.getAbsolutePath());
            sendError(exchange, 404);
            return;
        }

        System.out.println("✅ Serving file: " + file.getAbsolutePath());

        byte[] bytes = Files.readAllBytes(file.toPath());
        exchange.sendResponseHeaders(200, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void main(String[] args) throws IOException {

        sportsbook sb = new sportsbook();

        System.out.println("📁 Working directory: " + BASE_DIR);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Health check / sanity route
        server.createContext("/run", exchange -> {
            try {
                String response = "Java server is running";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendError(exchange, 500);
            }
        });

        // Results page
        server.createContext("/results", exchange -> {
            try {
                sb.update();
                serveFile(exchange, "results.html");
            } catch (Exception e) {
                System.out.println("🔥 Error handling /results");
                e.printStackTrace();
                sendError(exchange, 500);
            }
        });

        // Boxes page
        server.createContext("/boxes", exchange -> {
            try {
                sb.update();
                serveFile(exchange, "boxes.html");
            } catch (Exception e) {
                System.out.println("🔥 Error handling /boxes");
                e.printStackTrace();
                sendError(exchange, 500);
            }
        });

        // Root + fallback handler
        server.createContext("/", exchange -> {
            try {
                String path = exchange.getRequestURI().getPath();

                if (path.equals("/") || path.equals("/index.html")) {
                    serveFile(exchange, "index.html");
                } else {
                    System.out.println("⚠️ Unknown route: " + path);
                    sendError(exchange, 404);
                }
            } catch (Exception e) {
                System.out.println("🔥 Error handling /");
                e.printStackTrace();
                sendError(exchange, 500);
            }
        });

        server.start();
        System.out.println("🚀 Java server running at http://localhost:8080");
    }
}
