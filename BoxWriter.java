import javax.swing.*;
import java.awt.*;
import java.util.*;

public class BoxWriter {
    
    public ArrayList<BoxButton> buttons = new ArrayList<BoxButton>();
    public ArrayList<BoxButton> selected_buttons = new ArrayList<BoxButton>();
    public ArrayList<Integer> homeNumbers = new ArrayList<Integer>();
    public ArrayList<Integer> awayNumbers = new ArrayList<Integer>();
    public boolean selection_active = true;


    public static boolean listContainsInteger(ArrayList<Integer> list, int num) {
        for (Integer n : list) {
            if (n == num) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Integer> randomizeNumbers() {
        ArrayList<Integer> nums = new ArrayList<Integer>();
        int randomInt = (int) (Math.random() * 10);

        for (int i=1; i < 11; i++) {
            while (listContainsInteger(nums, randomInt)) {
                randomInt = (int) (Math.random() * 10);
            }
            nums.add(randomInt);
        }
        return nums;
    }

    // Deselect all selected buttons
    public void deselectAllButtons() {
        for (BoxButton b : selected_buttons) {
            b.setBackground(null);
        }
        selected_buttons.clear();
    }

    public BoxWriter() {
        SwingUtilities.invokeLater(() -> {
            // Create the main frame
            JFrame frame = new JFrame("Fullscreen Window");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true); // Removes title bar and borders

            /* Set fullscreen mode */
            GraphicsDevice device = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice();
            device.setFullScreenWindow(frame);

            
            frame.setLocationRelativeTo(null); // Center the window on screen
            frame.setLayout(null);

            Button addButton = new Button("Add Players");
            addButton.setBounds(160, 25, 150, 50);
            addButton.addActionListener(e -> {
                if (selected_buttons.size() > 0) {
                    NameFrame nameFrame = new NameFrame(this);
                    nameFrame.setVisible(true);
                }
            });
            frame.add(addButton);

            Button removeButton = new Button("Remove Players");
            removeButton.setBounds(320, 25, 150, 50);
            removeButton.setBackground(null);
            removeButton.addActionListener(e -> {
                if (selected_buttons.size() > 0) {
                    for (BoxButton b : selected_buttons) {
                        b.setPlayer(null);
                        b.setText(null);
                        b.setBackground(null);
                    }
                    selected_buttons.clear();
                }
            });
            frame.add(removeButton);

            Button randomizeButton = new Button("Randomize Numbers");
            randomizeButton.setBounds(480, 25, 150, 50);
            randomizeButton.addActionListener(e -> {
                if (selected_buttons.size() > 0) {
                    this.homeNumbers = randomizeNumbers();
                    this.awayNumbers = randomizeNumbers();

                    for (int i=0; i < 10; i++) {
                        for (int j=0; j < 10; j++) {
                            BoxButton btn = buttons.get(i*10 + j);
                            btn.setValues(homeNumbers.get(i), awayNumbers.get(j));
                            btn.setText(btn.home_value + " - " + btn.away_value);
                        }
                    }

                    
                }
            });
            frame.add(randomizeButton);


            // Create the score buttons
            for (int i=1; i < 11; i++) {
                for (int j=1; j < 11; j++) {
                    BoxButton tempButton = new BoxButton(j*160, i*80, this);
                    frame.add(tempButton);
                    buttons.add(tempButton);
                }
            }

            

            // Show window
            frame.setVisible(true);
        });

        


    }
    

    



    public static void main(String[] args) {
        BoxWriter boxWriter = new BoxWriter();       
    }
}
