package com.kelompok2.bms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class OrderPage extends JDialog {
    private JTable table1;
    private JButton updateButton;
    private JComboBox comboBox1;
    private JButton backButton;
    private JPanel orderPanel;
    private JTextField textField1;
    private JButton searchButton;
    private JButton checkDetailsButton;
    DefaultTableModel tableModel;

    public OrderPage(LoginPage loginPage, String id) {
        super(loginPage);
        setTitle("Register");
        setContentPane(orderPanel);
        setMinimumSize(new Dimension(600, 600));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
//        try {
//            Connection conn = Conn.getCon();
//            PreparedStatement pst;
//            pst = conn.prepareStatement("select * database_student");
//            ResultSet rs;
//            rs = pst.executeQuery();
//            table1.setModel(DbUtils.resultSetToTableModel(rs));
//            rs.close();
//            pst.close();
//            conn.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"orderId", "orderDate", "amount", "status", "customerId"});
        loadTable(tableModel);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuAdmin menuAdmin = new MenuAdmin(loginPage,id);
                menuAdmin.setVisible(true);
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = textField1.getText().trim();
                if(searchTerm.isBlank() || searchTerm.isEmpty()){
                    tableModel.setRowCount(0);
                    loadTable(tableModel);
                }
                else{
                    sortTable(searchTerm);
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected value from the JComboBox
                String selectedStatus = (String) comboBox1.getSelectedItem();

                if (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equalsIgnoreCase("Select")) {
                    // Update the selected rows in the table
                    int[] selectedRows = table1.getSelectedRows();
                    if (selectedRows.length > 0) {
                        for (int row : selectedRows) {
                            // Get the orderId from the selected row
                            String orderId = table1.getValueAt(row, 0).toString();

                            // Update the status in the database
                            updateOrderStatus(orderId, selectedStatus);
                        }
                        tableModel.setRowCount(0);
                        loadTable(tableModel);
                    } else {
                        JOptionPane.showMessageDialog(OrderPage.this, "Please select rows to update.", "No Rows Selected", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(OrderPage.this, "Please select a status.", "No Status Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        checkDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();

                if (selectedRow != -1) { // Check if a row is selected
                    String customerId = table1.getValueAt(selectedRow, 4).toString();
                    String orderId = table1.getValueAt(selectedRow, 0).toString();
                    String date = table1.getValueAt(selectedRow, 1).toString();
                    String amount_value = (String) table1.getValueAt(selectedRow, 2);
                    int amount = Integer.parseInt(amount_value);
                    String status = table1.getValueAt(selectedRow, 3).toString();
                    CheckPage checkPage = new CheckPage(loginPage,orderId,date,amount,status,customerId);
                    checkPage.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(OrderPage.this, "Please select rows to update.", "No Rows Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }


    private void sortTable(String searchTerm) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table1.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> combinedFilter = createCombinedFilter(searchTerm);
        sorter.setRowFilter(combinedFilter);
    }

    private RowFilter<DefaultTableModel, Object> createCombinedFilter(String searchTerm) {
        int columnCount = table1.getColumnCount();
        RowFilter<DefaultTableModel, Object>[] filters = new RowFilter[columnCount];

        for (int i = 0; i < columnCount; i++) {
            filters[i] = RowFilter.regexFilter("(?i)" + searchTerm, i);
        }

        return new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                for (RowFilter<DefaultTableModel, Object> filter : filters) {
                    if (filter.include(entry)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private void loadTable(DefaultTableModel tableModel){
        try {
            Connection conn = Conn.getCon();
            Statement stmt = conn.createStatement();

            String query = "SELECT * FROM orders order by status";
            ResultSet rs = stmt.executeQuery(query);

            tableModel.setRowCount(0);

            while (rs.next()) {
                // Process the retrieved data
                String id = rs.getString("orderId");
                String date = rs.getString("orderDate");
                String amount = rs.getString("amount");
                String status = rs.getString("status");
                String custId = rs.getString("customerId");
                // Retrieve other columns as needed

                // Add a new row to the table model
                tableModel.addRow(new Object[]{id, date, amount, status, custId});
            }
            table1.setModel(tableModel);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void updateOrderStatus(String orderId, String status) {
        try {
            Connection conn = Conn.getCon();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE orders SET status = ? WHERE orderId = ?");
            pstmt.setString(1, status);
            pstmt.setString(2, orderId);
            pstmt.executeUpdate();

            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


