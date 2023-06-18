package com.kelompok2.bms;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BasePage extends JDialog {
    private JLabel timeLabel;
    public BasePage(LoginPage parent) {
    }

    protected void updateTimeLabel(JLabel timeLabel) {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String formattedTime = sdf.format(new Date());
                    SwingUtilities.invokeLater(() -> timeLabel.setText(formattedTime));
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
