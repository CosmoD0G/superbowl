import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class bettor {
    public int score = 0;
    public String name = "";
    public int[] props = new int[15];
    public int tiebreaker = 0;
    public int tiebreaker_error = 0;
    public boolean tiebreaker_active = false;
    
    public void score(bettor KEY) {
       for (int i = 0; i < 15; i++) {
            if (props[i]==KEY.props[i]) {
                score++;
            }
       }

       tiebreaker_error = Math.abs(KEY.tiebreaker-tiebreaker);
       
    }

    public bettor(String[] values, bettor KEY) {
        name = values[0];

        for (int i=1; i<17; i++) {
            try {
                int number = Integer.parseInt(values[i]);
                if (i == 16) {
                    tiebreaker = number;
                } else {
                    props[i-1] = number;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number: " + values[i]);
            }
        }
        score(KEY);

    }

    public bettor(String[] values) {
        name = values[0];

        for (int i=1; i<17; i++) {
            try {
                int number = Integer.parseInt(values[i]);
                if (i == 16) {
                    tiebreaker = number;
                } else {
                    props[i-1] = number;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number: " + values[i]);
            }
        }

    }
}