package com.kelompok2.bms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterPage extends JDialog{
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JPasswordField passwordField1;
    private JPanel registerPanel;
    private JButton registerButton;
    private JButton cancelButton;
    private JTextField textField4;

    public RegisterPage(LoginPage loginPage) {
        super(loginPage);
        setTitle("Register");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 500));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = textField4.getText();
                String firstName = textField1.getText();
                String lastName = textField2.getText();
                String username = textField3.getText();
                String password = passwordField1.getText();

                if (id.isEmpty() || firstName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(rootPane, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Account account;
                if (username.equalsIgnoreCase("admin")) {
                    account = new Admin(firstName,lastName,username,password,'A'+id);
                    account.saveToDatabase("admin");
                }
                else{
                    account = new Customer(firstName,lastName,username,password,'C'+id);
                    account.saveToDatabase("user");
                }

                textField1.setText("");
                textField2.setText("");
                textField3.setText("");
                textField4.setText("");
                passwordField1.setText("");
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                LoginPage loginPage = new LoginPage(null);
                loginPage.setVisible(true);
            }
        });
    }

}
