package com.kelompok2.bms;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Customer extends Account{
    private String customerId;
    public Customer(String firstName, String lastName, String username, String password, String customerId){
        super(firstName, lastName, username, password);
        this.customerId=customerId;
    }
    @Override
    public void saveToDatabase(String type) {
        try {
            Connection connection = getConnection();

            String query = "INSERT INTO account (id, firstName, lastName, username, password, type) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, customerId);
            statement.setString(2, getFirstName());
            statement.setString(3, getLastName());
            statement.setString(4, getUsername());
            statement.setString(5, getPassword());
            statement.setString(6, type);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "User registered successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to register user.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            statement.close();
            closeConnection(connection);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
