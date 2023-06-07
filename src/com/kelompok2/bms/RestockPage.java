package com.kelompok2.bms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class RestockPage extends JDialog{
    private JTextField textField1;
    private JTextField textField3;
    private JTextField textField5;
    private JTextField textField7;
    private JTextField textField2;
    private JTextField textField4;
    private JTextField textField6;
    private JTextField textField8;
    private JLabel Persediaan1;
    private JLabel Persediaan3;
    private JLabel Persediaan5;
    private JLabel Persediaan7;
    private JLabel Persediaan2;
    private JLabel Persediaan4;
    private JLabel Persediaan6;
    private JLabel Persediaan8;
    private JButton updateButton;
    private JPanel restockPanel;
    private JButton backButton;
    private Menu[] daftar = new Menu[8];
    public RestockPage(LoginPage loginPage) {
        super(loginPage);
        setTitle("Restock");
        setContentPane(restockPanel);
        setMinimumSize(new Dimension(500, 600));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loginPage.setResizable(false);
        stockZero();
        try {
            // Get the database connection from another class
            Connection conn = Conn.getCon();

            // Create a SQL statement
            String query = "SELECT productName, price, stock FROM products";  // Replace yourTableName with the actual table name
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Execute the query and get the result set
            ResultSet resultSet = stmt.executeQuery(query);

            // Determine the number of rows in the resultSet
            resultSet.last();
            int numRows = resultSet.getRow();

            // Initialize the daftar array with the appropriate size
            daftar = new Menu[numRows];

            // Reset the resultSet cursor back to the beginning
            resultSet.beforeFirst();

            // Iterate over the resultSet and populate the daftar array
            int index = 0;
            while (resultSet.next()) {
                String name = resultSet.getString("productName");
                int price = resultSet.getInt("price");
                int stock = resultSet.getInt("stock");

                daftar[index] = new Menu(name, price, stock);
                index++;
            }

            // Print the inserted data for verification
            for (Menu menu : daftar) {
                System.out.println(menu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Persediaan1.setText("Persediaan: "+daftar[0].getStock());
        Persediaan3.setText("Persediaan: "+daftar[1].getStock());
        Persediaan5.setText("Persediaan: "+daftar[2].getStock());
        Persediaan7.setText("Persediaan: "+daftar[3].getStock());
        Persediaan2.setText("Persediaan: "+daftar[4].getStock());
        Persediaan4.setText("Persediaan: "+daftar[5].getStock());
        Persediaan6.setText("Persediaan: "+daftar[6].getStock());
        Persediaan8.setText("Persediaan: "+daftar[7].getStock());

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean allStocksZero = true;
                boolean atLeastOneStockNonZero = false;

                for (int i = 0; i < daftar.length; i++) {
                    String stockValue = getTextFieldValue(i + 1);

                    // Check if the stock value is "0"
                    if (!stockValue.equals("0")) {
                        allStocksZero = false;
                        atLeastOneStockNonZero = true;
                        break;
                    }
                }

                if (allStocksZero) {
                    JOptionPane.showMessageDialog(RestockPage.this, "Please fill in at least one non-zero stock.");
                } else if (atLeastOneStockNonZero) {
                    try {
                        // Get the database connection from another class
                        Connection conn = Conn.getCon();
                        Statement stmt = conn.createStatement();

                        for (int i = 0; i < daftar.length; i++) {
                            String productName = daftar[i].getName();
                            int currentStock = daftar[i].getStock();

                            // Get the value from the corresponding JTextField
                            int stockToAdd = Integer.parseInt(getTextFieldValue(i + 1));

                            // Calculate the new stock
                            int newStock = currentStock + stockToAdd;

                            // Update the stock in the database
                            String updateQuery = "UPDATE products SET stock = " + newStock + " WHERE productId ="+i;

                            stmt.executeUpdate(updateQuery);

                            // Update the stock in the daftar array
                            daftar[i].setStock(newStock);

                            // Update the JLabel to display the updated stock
                            updateStockLabel(i + 1, newStock);
                        }

                        // Print the updated data for verification
                        for (Menu menu : daftar) {
                            System.out.println(menu);
                        }

                        JOptionPane.showMessageDialog(RestockPage.this, "Stock added successfully.");
                        stockZero();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuAdmin menuPageAdmin = new MenuAdmin(loginPage);
                menuPageAdmin.setVisible(true);
            }
        });
    }
    // Helper method to get the value from the corresponding JTextField
    private String getTextFieldValue(int index) {
        switch (index) {
            case 1:
                return textField1.getText();
            case 2:
                return textField3.getText();
            case 3:
                return textField5.getText();
            case 4:
                return textField7.getText();
            case 5:
                return textField2.getText();
            case 6:
                return textField4.getText();
            case 7:
                return textField6.getText();
            case 8:
                return textField8.getText();
            default:
                return "";
        }
    }

    // Helper method to update the JLabel to display the updated stock
    private void updateStockLabel(int index, int stock) {
        switch (index) {
            case 1:
                Persediaan1.setText("Persediaan: " + stock);
                break;
            case 2:
                Persediaan3.setText("Persediaan: " + stock);
                break;
            case 3:
                Persediaan5.setText("Persediaan: " + stock);
                break;
            case 4:
                Persediaan7.setText("Persediaan: " + stock);
                break;
            case 5:
                Persediaan2.setText("Persediaan: " + stock);
                break;
            case 6:
                Persediaan4.setText("Persediaan: " + stock);
                break;
            case 7:
                Persediaan6.setText("Persediaan: " + stock);
                break;
            case 8:
                Persediaan8.setText("Persediaan: " + stock);
                break;
        }
    }

    public void stockZero(){
        textField1.setText("0");
        textField2.setText("0");
        textField3.setText("0");
        textField4.setText("0");
        textField5.setText("0");
        textField6.setText("0");
        textField7.setText("0");
        textField8.setText("0");
    }
}
