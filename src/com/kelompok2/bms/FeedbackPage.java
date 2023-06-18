package com.kelompok2.bms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class FeedbackPage extends BasePage {
    private JPanel FeedbackPage;
    private JButton submitButton;
    private JTextArea inputSuggest;
    private JComboBox subject;

    public FeedbackPage(LoginPage loginPage, String id) {
        super(loginPage);
        setTitle("Feedback Page");
        setContentPane(FeedbackPage);
        setMinimumSize(new Dimension(450, 500));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loginPage.setResizable(false);
        setResizable(false);
        inputSuggest.setLineWrap(true);
        inputSuggest.setWrapStyleWord(true);
        submitButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               String message = inputSuggest.getText();
               submitFeedback(id, message);
               JOptionPane.showMessageDialog(null, "Thanks for the feedback ;)");
               dispose();
           }
       }
        );
    }

    ;

    private void submitFeedback(String sender, String message) {
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Some Fields Are Empty", "Error", 1);
        } else {
            try {
                // Prepare the insert statement
                Connection conn = Conn.getCon();
                String insertMessageSQL = "INSERT INTO feedback (sender, timestamp, subject, message) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(insertMessageSQL);
                preparedStatement.setString(1, sender);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                preparedStatement.setTimestamp(2, timestamp);
                String type = subject.getSelectedItem().toString();
                preparedStatement.setString(3, type);
                preparedStatement.setString(4, message);


                // Execute the insert statement
                preparedStatement.executeUpdate();
                preparedStatement.close();

                JOptionPane.showMessageDialog(rootPane,"Feedback has been sent. Thank you!","successfully ",1);
                dispose();

                System.out.println("Feedback has been saved in database.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}