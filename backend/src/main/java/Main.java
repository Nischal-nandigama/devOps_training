import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;
import com.sun.net.httpserver.HttpServer;

public class Main {

    static String dbUrl = "jdbc:postgresql://database:5432/myapp";
    static String dbUser = "appuser";
    static String dbPassword = "secret123";

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(3000),0);

        initDB();

        server.createContext("/add", exchange -> {

            try {

                String query = exchange.getRequestURI().getQuery();
                String[] parts = query.split("&");

                String name = parts[0].split("=")[1];
                String email = parts[1].split("=")[1];

                Connection conn = DriverManager.getConnection(dbUrl,dbUser,dbPassword);

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users(name,email) VALUES (?,?)");

                ps.setString(1,name);
                ps.setString(2,email);

                ps.executeUpdate();

                conn.close();

                String response="User added";

                exchange.sendResponseHeaders(200,response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } catch(Exception e){
                e.printStackTrace();
            }

        });


        server.createContext("/users", exchange -> {

            try {

                Connection conn = DriverManager.getConnection(dbUrl,dbUser,dbPassword);

                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT * FROM users");

                String json="[";

                while(rs.next()){

                    json += "{";
                    json += "\"id\":" + rs.getInt("id") + ",";
                    json += "\"name\":\"" + rs.getString("name") + "\",";
                    json += "\"email\":\"" + rs.getString("email") + "\"";
                    json += "},";
                }

                if(json.endsWith(",")) json=json.substring(0,json.length()-1);

                json += "]";

                byte[] resp=json.getBytes();

                exchange.sendResponseHeaders(200,resp.length);

                OutputStream os = exchange.getResponseBody();
                os.write(resp);
                os.close();

                conn.close();

            } catch(Exception e){
                e.printStackTrace();
            }

        });


        server.createContext("/delete", exchange -> {

            try {

                String query = exchange.getRequestURI().getQuery();
                int id = Integer.parseInt(query.split("=")[1]);

                Connection conn = DriverManager.getConnection(dbUrl,dbUser,dbPassword);

                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM users WHERE id=?");

                ps.setInt(1,id);

                ps.executeUpdate();

                conn.close();

                String response="User deleted";

                exchange.sendResponseHeaders(200,response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } catch(Exception e){
                e.printStackTrace();
            }

        });

        server.start();

        System.out.println("Backend running on port 3000");
    }


    static void initDB() {

        try {

            Connection conn = DriverManager.getConnection(dbUrl,dbUser,dbPassword);

            Statement stmt = conn.createStatement();

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users(" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100)," +
                    "email VARCHAR(100))");

            conn.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}