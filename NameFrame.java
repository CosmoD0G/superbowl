import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.swing.*;



import java.awt.*;
import java.awt.event.ActionListener;


public class NameFrame extends JDialog implements ActionListener {
    
    JButton submit_button;
    JButton cancel_button;
    BoxWriter controller;
    JTextField text_field;

    public NameFrame(BoxWriter controller) {
        this.controller = controller;
        this.setTitle("Enter Player Name");
        this.setSize(700, 100);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.setAlwaysOnTop(true);

        // add button to confirm name entry
        submit_button = new JButton("Confirm");
        submit_button.addActionListener(this);

        // add button to cancel name entry
        cancel_button = new JButton("Cancel");
        cancel_button.addActionListener(this);

        text_field = new JTextField(30);
        text_field.setPreferredSize(new Dimension(250,40));
        text_field.setBackground(new Color(0xbfbfbf));
        this.add(text_field); 
        this.add(submit_button);
        this.add(cancel_button);
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        // Handle button click event
        if (e.getSource()==submit_button) {
            for (BoxButton b : controller.selected_buttons) {
                b.setPlayer(text_field.getText());
            }
            controller.deselectAllButtons();
            this.dispose(); // Close the frame
        } else if (e.getSource()==cancel_button) {
            controller.deselectAllButtons();
            this.dispose(); // Close the frame
        }
    }


    
}
