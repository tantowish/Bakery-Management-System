package com.kelompok2.bms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ChatUser extends JDialog {
    private JTextArea textArea1;
    private JPanel chatPanel;
    private JTextField textField1;
    private JButton sendButton;

    public ChatUser(LoginPage loginPage, String id) {
        super(loginPage);
        setTitle("Chat");
        setContentPane(chatPanel);
        setMinimumSize(new Dimension(450, 500));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        loadMsg(id);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textField1.getText();
                if (!message.isEmpty()) {
                    textArea1.append("me: " + message + "\n");
                    textField1.setText("");
                    saveMessage(id, message);
                }
            }
        });
    }

    private void saveMessage(String sender, String message) {
        try {
            // Prepare the insert statement
            Connection conn = Conn.getCon();
            String insertMessageSQL = "INSERT INTO chat (sender, message, timestamp, receiver, read_status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertMessageSQL);
            preparedStatement.setString(1, sender);
            preparedStatement.setString(2, message);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(3, timestamp);
            preparedStatement.setString(4, "admin");
            preparedStatement.setInt(5, 0); // Set read_status to 0 for unread

            // Execute the insert statement
            preparedStatement.executeUpdate();
            preparedStatement.close();

            System.out.println("Message saved to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMsg(String id) {
        try {
            // Establish a database connection
            Connection conn = Conn.getCon();

            // Prepare the select statement
            String selectMessagesSQL = "SELECT sender, message, read_status FROM chat WHERE (sender = ? AND (receiver = ? OR receiver LIKE 'A%')) OR ((sender = ? OR sender LIKE 'A%') AND receiver = ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(selectMessagesSQL);
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, "admin");
            preparedStatement.setString(3, "admin");
            preparedStatement.setString(4, id);

            // Execute the select statement
            ResultSet resultSet = preparedStatement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String message = resultSet.getString("message");
                int readStatus = resultSet.getInt("read_status");
                if (sender.equals(id)) {
                    textArea1.append("me: " + message + "\n");
                } else {
                    textArea1.append("admin: " + message + "\n");
                    if (readStatus == 0) {
                        // Update read_status to 1 for messages received from admin
                        updateReadStatus(id, sender);
                    }
                }
            }

            // Close resources
            resultSet.close();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateReadStatus(String receiver, String sender) {
        try {
            // Prepare the update statement
            Connection conn = Conn.getCon();
            String updateReadStatusSQL = "UPDATE chat SET read_status = 1 WHERE sender = ? AND receiver = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateReadStatusSQL);
            preparedStatement.setString(1, sender);
            preparedStatement.setString(2, receiver);

            // Execute the update statement
            preparedStatement.executeUpdate();
            preparedStatement.close();

            System.out.println("Read status updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


