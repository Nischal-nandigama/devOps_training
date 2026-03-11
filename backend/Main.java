import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.sun.net.httpserver.HttpServer;

public class Main {

    public static void main(String[] args) throws Exception {

        String dbUrl = "jdbc:postgresql://database:5432/myapp";
        String dbUser = "appuser";
        String dbPassword = "secret123";

        HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);

        server.createContext("/save", exchange -> {

            String message = "Sample Multi Container App";

            try {
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                Statement stmt = conn.createStatement();

                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS messages(id SERIAL PRIMARY KEY, text VARCHAR(255));");

                stmt.executeUpdate(
                        "INSERT INTO messages(text) VALUES('" + message + "')");

                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            String response = "Message stored in PostgreSQL!";
            exchange.sendResponseHeaders(200, response.length());

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.start();
        System.out.println("Backend running on port 3000");
    }
}