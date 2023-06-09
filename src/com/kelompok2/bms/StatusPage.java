package com.kelompok2.bms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StatusPage extends JDialog{
    private JTable statusTable;
    private JPanel statusPanel;

    public StatusPage(LoginPage loginPage, String id) {
        super(loginPage);
        setTitle("Order Table");
        setModal(true);
        setContentPane(statusPanel);
        setMinimumSize(new Dimension(400, 400));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(loginPage);

        // Create the table model with column names
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"Order ID", "Order Date", "Amount", "Status"});

        // Get the database connection from another class
        Connection conn = Conn.getCon();

        try {
            // Create a SQL statement
            String query = "SELECT * FROM orders WHERE (customerId = ? AND ((orderDate BETWEEN DATE_SUB(NOW(), INTERVAL 7 DAY) AND NOW()) AND status='done')) OR (customerId = ? AND status<>'done') ORDER BY orderDate desc";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            statement.setString(2, id);

            // Execute the query and get the result set
            ResultSet resultSet = statement.executeQuery();

            // Populate the table model with the result set data
            while (resultSet.next()) {
                String orderId = resultSet.getString("orderId");
                String orderDate = resultSet.getString("orderDate");
                int amount = resultSet.getInt("amount");
                String status = resultSet.getString("status");

                tableModel.addRow(new Object[]{orderId, orderDate, amount, status});
            }

            // Close the result set and statement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create the order table with the populated table model
        statusTable.setModel(tableModel);

        // Create

        pack();
    }
}
