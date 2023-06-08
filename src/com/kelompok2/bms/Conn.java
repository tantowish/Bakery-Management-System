package com.kelompok2.bms;

import java.sql.*;

public class Conn {
    static Connection con;
    public static  Connection getCon(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bms_db","root","");
        }
        catch (Exception ex){
            System.out.println(""+ex);
        }
        return con;
    }

    public static String getId(String username){
        String id = null;
        try {
            // Get the database connection from another class
            Connection conn = Conn.getCon();

            // Create a SQL statement with a parameterized query
            String query = "SELECT id FROM account WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);

            // Execute the query
            ResultSet resultSet = pstmt.executeQuery();

            // Check if a result was found
            if (resultSet.next()) {
                id = resultSet.getString("id");
            }

            // Close the ResultSet and PreparedStatement
            resultSet.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    public static void selectUsers() {
        try {
            Connection connection = getCon();
            if (connection != null) {
                String query = "SELECT * FROM account";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                // Iterate through the result set and print user details
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String type = resultSet.getString("type");
                    // Add any other relevant columns as needed

                    System.out.println("Username: " + username);
                    System.out.println("type: " + type);
                    // Print any other relevant details
                }

                // Close resources
                resultSet.close();
                statement.close();
                connection.close();
            } else {
                System.out.println("Failed to establish a database connection.");
            }
        } catch (Exception ex) {
            System.out.println("" + ex);
        }
    }
    public static boolean checkUser(String username, String password) {
        try {
            Connection connection = getCon();
            if (connection != null) {
                String query = "SELECT * FROM account WHERE username = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();

                // Check if a user with the given username and password exists
                boolean userExists = resultSet.next();

                // Close resources
                resultSet.close();
                statement.close();
                connection.close();

                return userExists;
            } else {
                System.out.println("Failed to establish a database connection.");
            }
        } catch (Exception ex) {
            System.out.println("" + ex);
        }

        return false;
    }
    public static String getNama(String id){
        String firstName="";
        String lastName="";

        try {
            // Establish the database connection
            Connection connection = getCon();
            // Create the SQL query
            String query = "SELECT firstName, lastName FROM account WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, id);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Process the query results
            if (resultSet.next()) {
                firstName = resultSet.getString("firstName");
                lastName = resultSet.getString("lastName");
            }

            // Close the connections and resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        // Combine firstName and lastName into a single variable
        String fullName = firstName + " " + lastName;

        return fullName;
    }

    public static String getUsername(String id){
        String username="";

        try {
            // Establish the database connection
            Connection connection = getCon();
            // Create the SQL query
            String query = "SELECT username FROM account WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, id);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Process the query results
            if (resultSet.next()) {
                username = resultSet.getString("username");
            }

            // Close the connections and resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        // Combine firstName and lastName into a single variable

        return username;
    }

    public static String getProductName(String id){
        String nama="";
        try {
            // Get the database connection from another class
            Connection conn = Conn.getCon();

            // Create a SQL statement with a parameterized query
            String query = "SELECT productName FROM products WHERE productId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);

            // Execute the query
            ResultSet resultSet = pstmt.executeQuery();

            // Check if a result was found
            if (resultSet.next()) {
                nama = resultSet.getString(1); // Use getInt() to retrieve the integer value
            }

            // Close the ResultSet and PreparedStatement
            resultSet.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nama;
    }
    public static int getCountOrder(String id) {
        int count = 0;
        try {
            // Get the database connection from another class
            Connection conn = Conn.getCon();

            // Create a SQL statement with a parameterized query
            String query = "SELECT count(orderId) FROM orders WHERE customerId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);

            // Execute the query
            ResultSet resultSet = pstmt.executeQuery();

            // Check if a result was found
            if (resultSet.next()) {
                count = resultSet.getInt(1); // Use getInt() to retrieve the integer value
            }

            // Close the ResultSet and PreparedStatement
            resultSet.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public static String checkType(String id) {
        String type="";
        try {
            // Establish the database connection
            Connection connection = Conn.getCon();
            // Create the SQL query
            String query = "SELECT type FROM account WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, id);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Process the query results
            if (resultSet.next()) {
                type = resultSet.getString("type");
            }

            // Close the connections and resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        // Combine firstName and lastName into a single variable

        return type;
    }
    public static String getFirstName(String id){
        String firstName="";

        try {
            // Establish the database connection
            Connection connection = getCon();
            // Create the SQL query
            String query = "SELECT firstName, lastName FROM account WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, id);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Process the query results
            if (resultSet.next()) {
                firstName = resultSet.getString("firstName");
            }

            // Close the connections and resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        // Combine firstName and lastName into a single variable
        String fullName = firstName;

        return fullName;
    }

    public static boolean isUsernameExists(String username) {
        try {
            Connection connection = getCon();

            String query = "SELECT COUNT(*) FROM account WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            statement.close();
            connection.close();

            return count > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
