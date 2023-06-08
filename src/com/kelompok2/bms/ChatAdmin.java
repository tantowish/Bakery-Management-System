package com.kelompok2.bms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ChatAdmin extends JDialog{
    private JTextArea textArea1;
    private JPanel panel1;
    private JTextField textField1;
    private JButton sendButton;
    public ChatAdmin(LoginPage loginPage, String id, String receiver){
        super(loginPage);
//        String id="A502562";
//        String receiver="C492531";
        setTitle("login");
        setContentPane(panel1);
        setMinimumSize(new Dimension(450,500));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        loadMsg(id,receiver);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textField1.getText();
                if (!message.isEmpty()) {
                    textArea1.append("admin : "+message + "\n");
                    textField1.setText("");
                    saveMessage(id,message,receiver);
                }
            }
        });
    }
    private void saveMessage(String sender, String message, String receiver) {
        try {
            // Prepare the insert statement
            Connection conn = Conn.getCon();
            String insertMessageSQL = "INSERT INTO chat (sender ,message, timeStamp, receiver, read_status) VALUES (?, ?, ?, ?, 0)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertMessageSQL);
            preparedStatement.setString(1, sender);
            preparedStatement.setString(2, message);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(3, timestamp);
            preparedStatement.setString(4, receiver);

            // Execute the insert statement
            preparedStatement.executeUpdate();
            preparedStatement.close();

            System.out.println("Message saved to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadMsg(String id,String receiver) {
        try {
            // Establish a database connection
            Connection conn = Conn.getCon();

            // Prepare the select statement
            String selectMessagesSQL = "SELECT sender, message, receiver FROM chat WHERE (sender = ? OR sender LIKE 'A%') OR receiver = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(selectMessagesSQL);
            preparedStatement.setString(1, "admin");
            preparedStatement.setString(2, "admin");

            // Execute the select statement
            ResultSet resultSet = preparedStatement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String message = resultSet.getString("message");
                String receiver1 = resultSet.getString("receiver");
                if ((sender.startsWith("A") || sender.startsWith("a")) && receiver1.equals(receiver)) {
                    textArea1.append("admin : " + message + "\n");
                }
                else if(sender.equals(receiver)){
                    textArea1.append (Conn.getFirstName(receiver)+" : "+ message + "\n");
                    updateReadStatus(receiver);

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

    private void updateReadStatus(String sender) {
        try {
            Connection conn = Conn.getCon();
            String updateReadStatusSQL = "UPDATE chat SET read_status = 1 WHERE sender = ? AND receiver = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateReadStatusSQL);
            preparedStatement.setString(1, sender);
            preparedStatement.setString(2, "admin");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            conn.close();
            System.out.println("Read status updated for sender: " + sender);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
