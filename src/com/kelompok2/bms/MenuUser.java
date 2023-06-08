package com.kelompok2.bms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuUser extends JDialog{

    private JButton orderButton;
    private JButton chatAdminButton;
    private JButton backButton;
    private JPanel MenuUser;

    public MenuUser(LoginPage loginPage, String id){
        super(loginPage);
        setTitle("Menu Admin");
        setContentPane(MenuUser);
        setMinimumSize(new Dimension(450, 500));
        setModal(true);
        setLocationRelativeTo(loginPage);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loginPage.setResizable(false);
        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuPage menuPage = new MenuPage(loginPage,id);
                menuPage.setVisible(true);
            }
        });
        chatAdminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChatUser chatUser = new ChatUser(loginPage,id);
                chatUser.setVisible(true);
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginPage loginPage1 = new LoginPage(loginPage);
                loginPage1.setVisible(true);
            }
        });
    }

}
