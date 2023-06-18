package com.kelompok2.bms;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends BasePage{
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JComboBox comboBox1;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel loginPanel;
    private JLabel timeLabel;

    public LoginPage(LoginPage parent){
        super(parent);
        setTitle("login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450,500));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // memperbarui waktu
        super.updateTimeLabel(timeLabel);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField1.getText();
                String password = passwordField1.getText();
                if(username.isEmpty() || password.isEmpty()){
                    JOptionPane.showMessageDialog(rootPane,"Some Fields Are Empty","Erorr",1);
                }
                else{
                    if(Conn.checkUser(username,password)){
                        if(Conn.checkType(Conn.getId(username)).equals("admin")){
                            JOptionPane.showMessageDialog(rootPane,"Successfully logged in as admin","Logged",1);
                            dispose();
                            String id = Conn.getId(username);
                            MenuAdmin menuPageAdmin = new MenuAdmin(LoginPage.this,id);
                            menuPageAdmin.setVisible(true);
                        }
                        else{
                            JOptionPane.showMessageDialog(rootPane,"Successfully logged in as cusotmer","Logged",1);
                            dispose();
                            String id = Conn.getId(username);
                            MenuUser menuUser = new MenuUser(LoginPage.this,id);
                            menuUser.setVisible(true);
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
