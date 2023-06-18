package com.kelompok2.bms;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


public class MenuPage extends BasePage {
    private JTable table1;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JSpinner spinner3;
    private JSpinner spinner4;
    private JSpinner spinner5;
    private JSpinner spinner6;
    private JSpinner spinner7;
    private JSpinner spinner8;
    private JPanel menuPanel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton receiptButton;
    private JLabel totalLabel;
    private JButton updateButton;
    private JButton resetButton;
    private JButton statusButton;
    private JButton logoutButton;
    private JScrollPane spane;
    private JLabel timeTable;
    private static int tableCounter = 1;
    private static int total;
    private Menu daftar[] = new Menu[8];


    public MenuPage(LoginPage loginPage, String id) {
        super(loginPage);
        setTitle("Menu");
        setContentPane(menuPanel);
        setMinimumSize(new Dimension(1200, 700));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setColumnIdentifiers(new Object[]{"No","Name","Quantity","Price"});

        JSpinner[] spinners = {spinner1, spinner3, spinner5, spinner7, spinner2, spinner4, spinner6, spinner8};

        loadMenu();
        spane.setBackground(new Color(238, 225, 186));

        // memperbarui waktu
        super.updateTimeLabel(timeTable);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear existing rows from the table
//                model.setRowCount(0);

                for (int i = 0; i < spinners.length; i++) {
                    int quantity = (int) spinners[i].getValue();
                    if (quantity > 0) {
                        String name = daftar[i].getName();
                        int price = daftar[i].getPrice() * quantity;

                        // Check if the stock is sufficient for the quantity
                        int stock = daftar[i].getStock();
                        if (quantity > stock) {
                            JOptionPane.showMessageDialog(null, "Insufficient stock for " + name, "Error", JOptionPane.ERROR_MESSAGE);
                            continue; // Skip to the next iteration
                        }

                        // Update the stock
                        daftar[i].setStock(stock - quantity);
                    }
                }

                for (int i = 0; i < spinners.length; i++) {
                    int quantity = (int) spinners[i].getValue();
                    if (quantity > 0) {
                        String name = daftar[i].getName();
                        int price = daftar[i].getPrice() * quantity;
                        // Check if the menu item already exists in the table
                        boolean existingItem = false;
                        for (int row = 0; row < model.getRowCount(); row++) {
                            if (model.getValueAt(row, 1).equals(name)) { // Check the name column
                                // Update the existing row with new quantity and price
                                int oldQuantity = (int) model.getValueAt(row, 2);
                                int oldPrice = (int) model.getValueAt(row, 3);
                                int newQuantity = oldQuantity + quantity;
                                int newPrice = oldPrice + price;
                                model.setValueAt(newQuantity, row, 2);
                                model.setValueAt(newPrice, row, 3);
                                existingItem = true;
                                break;
                            }
                        }

                        if (!existingItem) {
                            // Add a new row if the menu item doesn't exist in the table
                            Object[] rowData = {tableCounter, name, quantity, price};
                            model.addRow(rowData);
                            tableCounter++;
                        }
                        total += price;
                        spinners[i].setValue(0);
                    }
                    spinners[i].setValue(0);
                }
                totalLabel.setText("Total : " + String.valueOf(total));
            }
        });


        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                int rowCount = model.getRowCount();
                for (int row = 0; row < rowCount; row++) {
                    String name = (String) model.getValueAt(row, 1);
                    int quantity = (int) model.getValueAt(row, 2);

                    // Update the stock
                    for (int i = 0; i < daftar.length; i++) {
                        if (daftar[i].getName().equals(name)) {
                            daftar[i].setStock(daftar[i].getStock() + quantity);
                            break;
                        }
                    }
                }

                model.setRowCount(0); // Remove all rows from the table
                total = 0; // Reset the total to zero
                tableCounter = 1;
                updateTotalLabel();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow >= 0) {
                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
                    String name = (String) model.getValueAt(selectedRow, 1);
                    int oldQuantity = (int) model.getValueAt(selectedRow, 2);

                    // Prompt the user for a new quantity
                    String newQuantityString = JOptionPane.showInputDialog(null, "Enter new quantity:", "Update Quantity", JOptionPane.QUESTION_MESSAGE);
                    if (newQuantityString != null) {
                        try {
                            int newQuantity = Integer.parseInt(newQuantityString);
                            if (newQuantity >= 0) {
                                int price = (int) model.getValueAt(selectedRow, 3);
                                int oldPrice = oldQuantity * price;
                                int newPrice = newQuantity * price;

                                // Check if the stock is sufficient for the quantity difference
                                int quantityDifference = newQuantity - oldQuantity;
                                int stock = 0;
                                for (int i = 0; i < daftar.length; i++) {
                                    if (daftar[i].getName().equals(name)) {
                                        stock = daftar[i].getStock();
                                        break;
                                    }
                                }
                                if (quantityDifference > stock) {
                                    JOptionPane.showMessageDialog(null, "Insufficient stock for " + name, "Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                // Update the stock
                                for (int i = 0; i < daftar.length; i++) {
                                    if (daftar[i].getName().equals(name)) {
                                        daftar[i].setStock(stock - quantityDifference);
                                        break;
                                    }
                                }

                                total = total - oldPrice + newPrice;
                                model.setValueAt(newQuantity, selectedRow, 2);
                                model.setValueAt(newPrice, selectedRow, 3);
                                updateTotalLabel();

                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a positive value or zero.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow >= 0) {
                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
                    int deletedQuantity = (int) model.getValueAt(selectedRow, 2);
                    int deletedPrice = (int) model.getValueAt(selectedRow, 3);
                    String name = (String) model.getValueAt(selectedRow, 1);

                    // Update the stock
                    for (int i = 0; i < daftar.length; i++) {
                        if (daftar[i].getName().equals(name)) {
                            daftar[i].setStock(daftar[i].getStock() + deletedQuantity);
                            break;
                        }
                    }

                    // Remove the selected row from the table
                    model.removeRow(selectedRow);

                    // Update the numbering of rows

                    int row;
                    for (row = selectedRow; row < model.getRowCount(); row++) {
                        model.setValueAt(row + 1, row, 0);
                        table1.setValueAt(row + 1, row, 0);
                    }
                    tableCounter = row + 1;

                    // Update the total
                    total -= deletedPrice;
                    updateTotalLabel();
                }
            }
        });
        receiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (model.getRowCount() > 0) {
                        Connection connection = Conn.getCon();

                        // Check if the ID has already placed an order
                        String checkQuery = "SELECT COUNT(*) FROM orders WHERE customerId = ?";
                        PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                        checkStatement.setString(1, id);

                        ResultSet checkResult = checkStatement.executeQuery();
                        checkResult.next();
                        int orderCount = checkResult.getInt(1);
                        checkResult.close();
                        checkStatement.close();

                        String orderId = id + (orderCount + 1);

                        exportPdf(id,orderId);

                        String query = "INSERT INTO orders (orderId, orderDate, amount, status, customerId) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, orderId);
                        statement.setString(2, String.valueOf(LocalDateTime.now()));
                        statement.setString(3, String.valueOf(total));
                        statement.setString(4, "paid");
                        statement.setString(5, id);

                        int rowsAffected = statement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(null, "Thanks for ordering :D.", "Success", JOptionPane.INFORMATION_MESSAGE);

                            // Decrease the stock in the database and update the Menu array
                            DefaultTableModel model = (DefaultTableModel) table1.getModel();
                            for (int row = 0; row < model.getRowCount(); row++) {
                                String name = (String) model.getValueAt(row, 1);
                                int quantity = (int) model.getValueAt(row, 2);

                                // Update the stock in the database
                                String updateQuery = "UPDATE products SET stock = stock - ? WHERE productName = ?";
                                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                                updateStatement.setInt(1, quantity);
                                updateStatement.setString(2, name);
                                updateStatement.executeUpdate();

                                // Update the stock in the Menu array
                                int i;
                                for (i = 0; i < daftar.length; i++) {
                                    if (daftar[i].getName().equals(name)) {
                                        daftar[i].setStock(daftar[i].getStock() - quantity);
                                        break;
                                    }
                                }
                                // Insert order details into the database
                                String productQuery = "INSERT INTO orderdetails (orderId, productId, quantity, totalPrice) VALUES (?, ?, ?, ?)";
                                PreparedStatement productStatement = connection.prepareStatement(productQuery);
                                productStatement.setString(1, orderId);
                                productStatement.setInt(2, i);
                                productStatement.setInt(3, quantity);
                                int price = (int) model.getValueAt(row, 3);
                                productStatement.setInt(4, price);
                                productStatement.executeUpdate();

                                tableCounter = 1;
                                total = 0;
                                updateTotalLabel();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to place order.", "Error", JOptionPane.ERROR_MESSAGE);
                            tableCounter = 1;
                            total = 0;
                            updateTotalLabel();
                        }
                        statement.close();
                        connection.close();
                    } else {
                        JOptionPane.showMessageDialog(null, "Please add the products", "Error", JOptionPane.ERROR_MESSAGE);
                        tableCounter = 1;
                        total = 0;
                        updateTotalLabel();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                model.setRowCount(0);
                loadMenu();
            }
        });
        statusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StatusPage statusPage = new StatusPage(loginPage,id);
                statusPage.setVisible(true);
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuUser menuUser = new MenuUser(loginPage,id);
                menuUser.setVisible(true);
            }
        });
    }

    private void updateTotalLabel() {
        totalLabel.setText("Total : " + String.valueOf(total));
    }

    private void exportPdf(String id, String orderId) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Receipt");
        fileChooser.setSelectedFile(new File(orderId+"-receipt.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try {
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                if (model.getRowCount() > 0) {
                    // Create a PDF document
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(outputFile));
                    document.open();

                    // Add a title to the PDF
                    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
                    Paragraph title = new Paragraph("Receipt", titleFont);
                    title.setAlignment(Element.ALIGN_CENTER);
                    document.add(title);

                    Font info = FontFactory.getFont(FontFactory.HELVETICA, 16);
                    LocalDateTime dateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = dateTime.format(formatter);

                    Paragraph datettime = new Paragraph(formattedDateTime, info);
                    Paragraph customer_name = new Paragraph("Customer Name : " + Conn.getNama(id), info);
                    Paragraph order_id = new Paragraph("Order ID : " + orderId, info);
                    Paragraph status = new Paragraph("Status : paid", info);
                    datettime.setAlignment(Element.ALIGN_LEFT);
                    customer_name.setAlignment(Element.ALIGN_LEFT);
                    order_id.setAlignment(Element.ALIGN_LEFT);
                    status.setAlignment(Element.ALIGN_LEFT);
                    document.add(datettime);
                    document.add(customer_name);
                    document.add(order_id);
                    document.add(status);


                    // Add a table to display the ordered products
                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(10f);

                    // Add table headers
                    String[] headers = {"No", "Name", "Quantity", "Price"};
                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(cell);
                    }

                    // Add table rows with ordered product details
                    for (int row = 0; row < model.getRowCount(); row++) {
                        int no = (int) model.getValueAt(row, 0);
                        String name = (String) model.getValueAt(row, 1);
                        int quantity = (int) model.getValueAt(row, 2);
                        int price = (int) model.getValueAt(row, 3);

                        table.addCell(String.valueOf(no));
                        table.addCell(name);
                        table.addCell(String.valueOf(quantity));
                        table.addCell(String.valueOf(price));
                    }

                    // Add the table to the document
                    document.add(table);

                    Paragraph paid = new Paragraph("Total : " + total, info);
                    paid.setAlignment(Element.ALIGN_LEFT);
                    document.add(paid);

                    Paragraph footer = new Paragraph("~Thank You~ ", titleFont);
                    footer.setAlignment(Element.ALIGN_CENTER);
                    document.add(footer);


                    // Close the document
                    document.close();

                    JOptionPane.showMessageDialog(null, "Receipt exported successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Please add the products", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadMenu(){
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

        JSpinner[] spinners = {spinner1, spinner3, spinner5, spinner7, spinner2, spinner4, spinner6, spinner8};
        for (int i = 0; i < spinners.length; i++) {
            JSpinner spinner = spinners[i];
            int finalI = i;
            spinner.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int quantity = (int) spinner.getValue();
                    int stock = daftar[finalI].getStock();

                    // Limit the quantity to the stock
                    if (quantity > stock) {
                        spinner.setValue(stock);
                    }

                    // Limit the quantity to 0
                    if (quantity < 0) {
                        spinner.setValue(0);
                    }
                }
            });
        }

    }
}

