package com.kelompok2.bms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class ReportPage extends JDialog{
    private JPanel reportPanel;
    private JTable table1;
    private JTable table2;
    private JButton PDFButton;
    private JButton backButton;
    private JLabel date;
    private JLabel pendapatan;
    private JLabel terjual;

    public ReportPage(LoginPage loginPage,String adminId) {
        super(loginPage);
        setTitle("Weekly Report");
        setContentPane(reportPanel);
        setMinimumSize(new Dimension(800, 600));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        LocalDate currentDate = LocalDate.now();
        LocalDate dateBefore7Days = currentDate.minusDays(7);
        date.setText(dateBefore7Days+" to "+currentDate);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"customerId", "Name", "Total Order", "Total Amount"});
        try {
            Connection conn = Conn.getCon();
            Statement stmt = conn.createStatement();

            String query = "SELECT account.id, count(orders.orderId) as count, SUM(orders.amount) AS total FROM account JOIN orders ON orders.customerId = account.id WHERE orders.orderDate BETWEEN DATE_SUB(NOW(), INTERVAL 7 DAY) AND NOW() GROUP BY account.id ORDER BY total DESC;";
            ResultSet rs = stmt.executeQuery(query);

            tableModel.setRowCount(0);

            while (rs.next()) {
                // Process the retrieved data
                String id = rs.getString("id");
                String total = rs.getString("total");
                String order = rs.getString("count");
                // Add a new row to the table model
                tableModel.addRow(new Object[]{id, Conn.getNama(id), order, total});
            }
            table1.setModel(tableModel);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DefaultTableModel tableModel2 = new DefaultTableModel();
        tableModel2.setColumnIdentifiers(new Object[]{"productName", "Quantity", "Total Amount"});
        try {
            Connection conn = Conn.getCon();
            Statement stmt = conn.createStatement();

            String query = "select products.productName, sum(orderdetails.quantity) as quantity, sum(orderdetails.totalPrice) as amount from products join orderdetails ON orderdetails.productId = products.productId join orders on orders.orderId = orderdetails.orderId WHERE orders.orderDate BETWEEN DATE_SUB(NOW(), INTERVAL 7 DAY) AND NOW() group by orderdetails.productId order by quantity desc;";
            ResultSet rs = stmt.executeQuery(query);

            tableModel2.setRowCount(0);

            while (rs.next()) {
                // Process the retrieved data
                String pname = rs.getString("productName");
                String qty = rs.getString("quantity");
                String total = rs.getString("amount");
                // Add a new row to the table model
                tableModel2.addRow(new Object[]{pname, qty, total});
            }
            table2.setModel(tableModel2);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        pendapatan.setText("Total pendapatan : Rp"+Conn.getSelling());
        terjual.setText("Produk terjual       : "+ Conn.getSold());
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuAdmin menuAdmin = new MenuAdmin(loginPage,adminId);

            }
        });
    }
}
