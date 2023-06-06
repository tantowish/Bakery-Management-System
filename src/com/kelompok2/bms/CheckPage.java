package com.kelompok2.bms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CheckPage extends JDialog{
    private JTable table1;
    private JLabel amount;
    private JLabel customer;
    private JLabel date;
    private JLabel id;
    private JPanel checkPanel;
    private JLabel status;

    public CheckPage(LoginPage loginPage, String orderId,String date,int amount,String status,String customerId){
        super(loginPage);
        setTitle("Register");
        setContentPane(checkPanel);
        setMinimumSize(new Dimension(400, 400));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        id.setText("Order Id : "+orderId);
        this.date.setText("Date : "+date);
        this.amount.setText("Amount : "+amount);
        this.status.setText("Status : "+status);
        customer.setText("Customer : "+Conn.getNama(customerId));

        try {
            Connection conn = Conn.getCon();
            Statement stmt = conn.createStatement();

            String query = "SELECT * FROM orderdetails WHERE orderId = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, orderId);

            // Execute the query
            ResultSet rs = statement.executeQuery();
            // Create a DefaultTableModel with column names
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new Object[]{"productId","productName", "quantity", "totalPrice"});
            // Add more column names as needed

            while (rs.next()) {
                // Process the retrieved data
                String productId = rs.getString("productId");
                String productName = Conn.getProductName(productId);
                String qty = rs.getString("quantity");
                String totalPrice = rs.getString("totalPrice");
                // Retrieve other columns as needed

                // Add a new row to the table model
                tableModel.addRow(new Object[]{productId,productName, qty, totalPrice});
            }
            table1.setModel(tableModel);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
