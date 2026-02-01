import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.swing.*;



import java.awt.*;
import java.awt.event.ActionListener;

public class BoxButton extends JButton implements ActionListener {

    public int home_value;
    public int away_value;
    public String player = "";
    public BoxWriter controller;

    public BoxButton(int x, int y, BoxWriter c) {
        this.controller = c;
        this.setText(null);
        this.setBounds(x, y, 150, 75);
        this.setPreferredSize(new Dimension(200, 60));
        this.setFont(new Font("Arial", Font.PLAIN, 24));
        this.addActionListener(this); // Register 'this' as the listener
    }

    public void setPlayer(String p) {
        this.player = p;
        this.setText(player);
    }



    public void setValues(int h, int a) {
        this.home_value = h;
        this.away_value = a;
    }


    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        
        if (e.getSource() == this) {
            if (controller.selection_active) {
                // Adding players
                if (!controller.selected_buttons.contains(this)) {
                    controller.selected_buttons.add(this);
                    this.setBackground(Color.GRAY);
                } else {
                    controller.selected_buttons.remove(this);
                    this.setBackground(null);
                }
            
            }
        }

    

    }
}
