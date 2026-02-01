
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.*;


import java.awt.*;
import java.util.ArrayList;

class sportsbook {
    public static ArrayList<bettor> bets = new ArrayList<>();
    public static bettor key;

    public static void createBettors() {
        String filePath = "Bets2.csv"; // Replace with your CSV file path

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int line_num = 1;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // Line 2 is the answer key
                if (line_num == 2) {
                    key = new bettor(values);
                } else if (line_num > 2) {
                    bets.add(new bettor(values, key));
                }

                line_num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void bubbleSort(ArrayList<bettor> players) {
        int n = players.size();
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                bettor current = players.get(j);
                bettor next = players.get(j + 1);

                // Sort primarily by score (higher score first)
                if (current.score < next.score) {
                    swap(players, j, j + 1);
                    swapped = true;
                }
                // If scores are equal, use tiebreaker_error (lower value first)
                else if (current.score == next.score) {
                    if (current.tiebreaker_error > next.tiebreaker_error) {
                        swap(players, j, j + 1);
                        swapped = true;
                    }

                    // Activate tiebreaker flag when there is a tie in score
                    current.tiebreaker_active = true;
                    next.tiebreaker_active = true;
                }
            }
            if (!swapped) break;
        }
    }

    private static void swap(ArrayList<bettor> players, int i, int j) {
        bettor temp = players.get(i);
        players.set(i, players.get(j));
        players.set(j, temp);
    }

    public static void printLeaderboard() {
        bubbleSort(bets);
        System.out.println("PRINTING LEADERBOARD\nLENGTH OF BETS ARRAYLIST: " + bets.size() + "\n");

        int i = 1;
        for (bettor b : bets) {
            System.out.print(i + "- " + b.name + " " + b.score + "/15");
            if (b.tiebreaker_active) {
                System.out.println(" TIEBREAKER: off from actual score by: " + b.tiebreaker_error);
            }
            System.out.println();
            i++;
        }
    }

    public static void displayLeaderboardGUI() {
        bubbleSort(bets); // Ensure sorting before displaying

        JFrame frame = new JFrame("Results");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);

        JTextPane textPane = new JTextPane();
        textPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textPane.setEditable(false);
    
        StyledDocument doc = textPane.getStyledDocument();
    
        // Create styles for bold and regular text
        Style boldStyle = textPane.addStyle("Bold", null);
        StyleConstants.setBold(boldStyle, true);
    
        Style regularStyle = textPane.addStyle("Regular", null);
        StyleConstants.setBold(regularStyle, false);

        try {
            doc.insertString(doc.getLength(), "Superbowl LIX Prop Bet Results\n", boldStyle);
            doc.insertString(doc.getLength(), "------------------------------\n", regularStyle);

            int rank = 1;
            for (bettor b : bets) {
                String line = String.format("%-3d %-15s %2d/15", rank, b.name, b.score);
                if (b.tiebreaker_active) {
                    line += String.format("  (Tiebreaker: %d)", b.tiebreaker_error);
                }
                line += "\n";
            // First two bettors in bold
                if (rank <= 2) {
                    doc.insertString(doc.getLength(), line, boldStyle);
               } else {
                    doc.insertString(doc.getLength(), line, regularStyle);
              }
               rank++;
            }
    } catch (BadLocationException e) {
        e.printStackTrace();
    }

    JScrollPane scrollPane = new JScrollPane(textPane);
    frame.add(scrollPane);

    frame.setVisible(true);
}

    
    public static String generateHTMLStandings() {
        StringBuilder html = new StringBuilder();
        bubbleSort(bets);
        for (bettor b : bets) {
            html.append(b.name).append(": ").append(b.score).append("/15<br>\n");
            if (b.tiebreaker_active) {
                html.append("&nbsp;&nbsp;(Tiebreaker: off by ").append(b.tiebreaker_error).append(")<br>\n");
            }
        }
        return html.toString();
    }

    public static String generateHTMLanswers() {
        StringBuilder html = new StringBuilder();
        html.append("<h2>Answer Key</h2>\n<table>\n<tr><th>Prop</th><th>Answer</th></tr>\n");
        for (int i = 0; i < key.props.length; i++) {
            html.append("<tr><td>").append(i + 1).append("</td><td>").append(key.props[i]).append("</td></tr>\n");
        }
        html.append("</table>\n");
        return html.toString();
    }

    public static void assembleHTML() {
        String htmlTemplate = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Superbowl LIX Prop Bet Results</title>
            <link rel="stylesheet" href="styles.css">
        </head>
        <body>
            <div class="container">
                <h1>Superbowl LIX Prop Bet Results</h1>
                <div class="leaderboard">
                    <h2>Leaderboard</h2>
                    %s
                </div>
                <div class="answers">
                    %s
                </div>
            </div>
        </body>
        </html>
        """;

        String standingsHTML = generateHTMLStandings();
        //String answersHTML = generateHTMLanswers();
        String finalHTML = String.format(htmlTemplate, standingsHTML, "");

        // Write finalHTML to a file named "results.html"
        try (java.io.FileWriter writer = new java.io.FileWriter("results.html")) {
            writer.write(finalHTML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public sportsbook() {
        createBettors();
        printLeaderboard(); // Print to console
        displayLeaderboardGUI(); // Show in GUI window
        assembleHTML();
    }
}

