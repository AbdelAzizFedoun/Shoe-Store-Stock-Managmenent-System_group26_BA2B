import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Database connection URL
            String url = "jdbc:mysql://localhost:3306/shoeStock";
            String user = "root";    // Username
            String password = "";    // Empty password

            // Establishing the connection
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection to the database established successfully.");
        } catch (SQLException e) {
            // Handle errors for JDBC
            e.printStackTrace();
            System.out.println("Failed to establish connection to the database.");
        }
        return conn;
    }

    public static void main(String[] args) {
        // Test the connection
        Connection conn = getConnection();
        if (conn != null) {
            try {
                // You can add any database queries here or close the connection
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

