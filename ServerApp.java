import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class ServerApp {

    private static void serveFile(HttpExchange exchange, String fileName) throws IOException {

        File file = new File(System.getProperty("user.dir") + "/" + fileName);

        if (!file.exists()) {
           exchange.sendResponseHeaders(404, -1);
           System.out.println("File not found: " + file);
           return;
        } else {
              System.out.println("Serving file: " + file);
        }

        byte[] bytes = Files.readAllBytes(file.toPath());
        exchange.sendResponseHeaders(200, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    public static void main(String[] args) throws IOException {

        sportsbook sb = new sportsbook();




        System.out.println("Working directory: " + System.getProperty("user.dir"));

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/run", exchange -> {
            String response = "Java is running";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.createContext("/", exchange -> {
            serveFile(exchange, "index.html");
        });

        server.createContext("/admin", exchange -> {
            serveFile(exchange, "admin.html");
        });

        server.createContext("/results", exchange -> {
            sb.update();
            serveFile(exchange, "results.html");
        });

        server.createContext("/trigger", exchange -> {

        });


        server.start();
        System.out.println("Java server running on http://localhost:8080");



        
        
    }
}
