package com.kelompok2.bms;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Account {
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    public Account(String firstName, String lastName, String username, String password){
        this.firstName=firstName;
        this.lastName=lastName;
        this.username=username;
        this.password=password;
    }
    public abstract void saveToDatabase(String type);
    protected Connection getConnection() throws SQLException {
        return Conn.getCon();
    }

    protected void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
