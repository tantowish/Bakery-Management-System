package com.kelompok2.bms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuAdmin extends JDialog{
    private JButton restockButton;
    private JButton antrianPesananButton;
    private JButton laporanMingguanButton;
    private JPanel MenuAdmin;
    private JButton backButton;
    private JButton chatCustomerButton;

    public MenuAdmin(LoginPage loginPage, String id) {
        super(loginPage);
        setTitle("Menu Admin");
        setContentPane(MenuAdmin);
        setMinimumSize(new Dimension(450, 500));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loginPage.setResizable(false);
//        restockButton.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                setVisible(false);
//                RegisterPage registerPage = new RegisterPage(LoginPage.this);
//                registerPage.setVisible(true);
//            }
//        });
        restockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                RestockPage restockPage = new RestockPage(loginPage,id);
                restockPage.setVisible(true);
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginPage loginPageBack = new LoginPage(loginPage);
                loginPageBack.setVisible(true);
            }
        });
        antrianPesananButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                OrderPage orderPage = new OrderPage(loginPage,id);
                orderPage.setVisible(true);
            }
        });
        chatCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MessagePage messagePage = new MessagePage(loginPage,id);
                messagePage.setVisible(true);
            }
        });
    }


}


