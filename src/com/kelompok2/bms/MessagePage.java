package com.kelompok2.bms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessagePage extends JDialog{
    private JPanel messagePanel;
    private JTable table1;
    private JButton goToChatButton;
    private JButton backButton;

    public MessagePage(LoginPage loginPage, String id){
        super(loginPage);
        setTitle("Message Page");
        setContentPane(messagePanel);
        setMinimumSize(new Dimension(450, 500));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loginPage.setResizable(false);

        loadChatData(id);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuAdmin menuAdmin = new MenuAdmin(loginPage,id);
                menuAdmin.setVisible(true);
            }
        });
        goToChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    String username = (String) table1.getValueAt(selectedRow, 1);
                    System.out.println(Conn.getId(username));
                    String receiver = Conn.getId(username);
                    // Update the read_status to mark the messages as read

                    ChatAdmin chatAdmin = new ChatAdmin(loginPage, id, receiver);
                    chatAdmin.setVisible(true);
                    // Update the table after opening the chat
                    loadChatData(id);
                } else {
                    JOptionPane.showMessageDialog(MessagePage.this, "Please select a row in the table.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void loadChatData(String id) {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Sender");
        tableModel.addColumn("Username");
        tableModel.addColumn("Unread"); // Add the Unread Count column

        try {
            Connection conn = Conn.getCon();
            String selectChatSQL = "SELECT max(timestamp) as timestamp, sender, SUM(CASE WHEN read_status = 0 THEN 1 ELSE 0 END) AS unread_count FROM chat WHERE receiver = ? OR receiver LIKE 'A%' GROUP BY sender ORDER BY timestamp desc";
            PreparedStatement preparedStatement = conn.prepareStatement(selectChatSQL);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                int unreadCount = resultSet.getInt("unread_count");
                tableModel.addRow(new Object[]{Conn.getNama(sender), Conn.getUsername(sender), unreadCount});
            }

            resultSet.close();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table1.setModel(tableModel);
    }

}
