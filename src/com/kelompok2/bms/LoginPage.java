package com.kelompok2.bms;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JDialog{
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JComboBox comboBox1;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel loginPanel;

    public LoginPage(LoginPage parent){
        super(parent);
        setTitle("login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450,500));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField1.getText();
                String password = passwordField1.getText();
                String type = comboBox1.getSelectedItem().toString();
                if(username.isEmpty() || password.isEmpty()||type.equals("Select")){
                    JOptionPane.showMessageDialog(rootPane,"Some Fields Are Empty","Erorr",1);
                }
                else{
                    if(Conn.checkUser(username,password,type)){
                        if(type.equals("admin")){
                            JOptionPane.showMessageDialog(rootPane,"Berhasil login sebagai admin","Logged",1);
                            dispose();
                            MenuAdmin menuPageAdmin = new MenuAdmin(LoginPage.this);
                            menuPageAdmin.setVisible(true);
                        }
                        else{
                            JOptionPane.showMessageDialog(rootPane,"Berhasil login sebagai customer","Logged",1);
                            dispose();
                            String id = Conn.getId(username);
                            MenuPage menuPageUser = new MenuPage(LoginPage.this,id);
                            menuPageUser.setVisible(true);
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(rootPane,"Pastikan Username dan Password benar","Gagal",1);
                    }
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                RegisterPage registerPage = new RegisterPage(LoginPage.this);
                registerPage.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        LoginPage loginPage = new LoginPage(null);
        loginPage.setVisible(true);
    }
}