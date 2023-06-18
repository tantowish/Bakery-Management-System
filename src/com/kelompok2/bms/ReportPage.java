package com.kelompok2.bms;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReportPage extends JDialog{
    private JPanel reportPanel;
    private JTable table1;
    private JTable table2;
    private JButton PDFButton;
    private JButton backButton;
    private JLabel date;
    private JLabel pendapatan;
    private JLabel terjual;
    private DefaultTableModel tableModel,tableModel2;

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

        tableModel = new DefaultTableModel();
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
                int total = rs.getInt("total");
                int order = rs.getInt("count");
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
        tableModel2 = new DefaultTableModel();
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
                int qty = rs.getInt("quantity");
                int total = rs.getInt("amount");
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
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        format.setMaximumFractionDigits(0);
        // Format the value as currency
        String formattedValue = format.format(Conn.getSelling());
        pendapatan.setText("Total pendapatan : "+formattedValue);
        terjual.setText("Produk terjual       : "+ Conn.getSold());
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuAdmin menuAdmin = new MenuAdmin(loginPage,adminId);
                menuAdmin.setVisible(true);
            }
        });
        PDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportPdf();
            }
        });
    }

    private void exportPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDateTime2 = dateTime.format(formatter2);
        fileChooser.setSelectedFile(new File(formattedDateTime2+" Report.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();


            try {
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                DefaultTableModel model2 = (DefaultTableModel) table2.getModel();


                // Insert data from table1
                if (table1.getRowCount() > 0 && table2.getRowCount()>0) {
                    Document document = new Document(PageSize.A4);
                    PdfWriter.getInstance(document, new FileOutputStream(outputFile));
                    document.open();
                    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
                    Font secondaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
                    Font thirdFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
                    Paragraph title = new Paragraph("Weekly Report", titleFont);

                    LocalDate currentDate = LocalDate.now();
                    LocalDate dateBefore7Days = currentDate.minusDays(7);
                    Paragraph date = new Paragraph(dateBefore7Days+" to "+currentDate,secondaryFont);

                    date.setAlignment(Element.ALIGN_CENTER);
                    title.setAlignment(Element.ALIGN_CENTER);

                    document.add(title);
                    document.add(date);

                    Paragraph newLine = new Paragraph("\n",secondaryFont);
                    document.add(newLine);

                    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

                    // Format the value as currency
                    String formattedValue = format.format(Conn.getSelling());
                    format.setMaximumFractionDigits(0);
                    Paragraph pendapatan = new Paragraph("Total Pendapatan : "+formattedValue,secondaryFont);
                    document.add(pendapatan);

                    Paragraph terjual = new Paragraph("Produk Terjual       : "+ Conn.getSold(),secondaryFont);
                    document.add(terjual);
                    document.add(newLine);


                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(10f);

                    Paragraph customer = new Paragraph("Customer",thirdFont);
                    customer.setAlignment(Element.ALIGN_CENTER);
                    document.add(customer);

                    // Add table headers
                    String[] headers = {"customerId", "Name", "Total Order", "Total Amount"};
                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(cell);
                    }

                    // Add table rows with ordered product details
                    for (int row = 0; row < model.getRowCount(); row++) {
                        String customerId = (String) model.getValueAt(row, 0);
                        String name = (String) model.getValueAt(row, 1);
                        int order = (int) model.getValueAt(row, 2);
                        int amount = (int) model.getValueAt(row, 3);

                        table.addCell(customerId);
                        table.addCell(name);
                        table.addCell(String.valueOf(order));
                        table.addCell(String.valueOf(amount));
                    }

                    // Add the table to the document
                    document.add(table);

                    document.add(newLine);

                    Paragraph product = new Paragraph("Product",thirdFont);
                    product.setAlignment(Element.ALIGN_CENTER);
                    document.add(product);


                    PdfPTable table2 = new PdfPTable(3);
                    table2.setWidthPercentage(100);
                    table2.setSpacingBefore(10f);
                    table2.setSpacingAfter(10f);

                    // Add table headers
                    String[] headers2 = {"productName", "Quantity", "Total Amount"};
                    for (String header : headers2) {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table2.addCell(cell);
                    }

                    // Add table rows with ordered product details
                    for (int row = 0; row < model2.getRowCount(); row++) {
                        String productName = (String) model2.getValueAt(row, 0);
                        int qty = (int) model2.getValueAt(row, 1);
                        int amount2 = (int) model2.getValueAt(row, 2);

                        table2.addCell(productName);
                        table2.addCell(String.valueOf(qty));
                        table2.addCell(String.valueOf(amount2));
                    }
                    document.add(table2);

                    document.add(newLine);
                    Paragraph footer = new Paragraph();
                    footer.setFont(secondaryFont);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = dateTime.format(formatter);
                    footer.add(formattedDateTime);
                    footer.add("\n");
                    footer.add("Kinan Bakery");
                    footer.setAlignment(Element.ALIGN_CENTER);
                    document.add(footer);

                    document.newPage();
                    try (Connection connection = Conn.getCon();
                         Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery("SELECT sender, message, subject FROM feedback WHERE timestamp BETWEEN DATE_SUB(NOW(), INTERVAL 7 DAY) AND NOW() GROUP BY timestamp")) {

                        // Create the feedback table
                        PdfPTable feedbackTable = new PdfPTable(3);
                        feedbackTable.setWidthPercentage(100);
                        feedbackTable.setSpacingBefore(10f);
                        feedbackTable.setSpacingAfter(10f);

                        Paragraph feedbackHeader = new Paragraph("Feedback", thirdFont);
                        feedbackHeader.setAlignment(Element.ALIGN_CENTER);
                        document.add(feedbackHeader);

                        // Add feedback table headers
                        String[] feedbackHeaders = {"Sender","Type", "Message"};
                        float[] columnWidths = {1f, 1f, 2f}; // Adjust the widths here (e.g., 1f for sender, 2f for message)
                        feedbackTable.setWidths(columnWidths);
                        for (String header : feedbackHeaders) {
                            PdfPCell cell = new PdfPCell(new Phrase(header));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            feedbackTable.addCell(cell);
                        }

                        // Add feedback table rows
                        while (resultSet.next()) {
                            String sender = resultSet.getString("sender");
                            String type = resultSet.getString("subject");
                            String nama_sender = Conn.getNama(sender);
                            String message = resultSet.getString("message");

                            feedbackTable.addCell(nama_sender);
                            feedbackTable.addCell(type);
                            feedbackTable.addCell(message);
                        }

                        // Add the feedback table to the document
                        document.add(feedbackTable);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    document.add(newLine);
                    document.add(footer);


                    document.close();
                }

                JOptionPane.showMessageDialog(null, "Report exported successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (DocumentException | FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
