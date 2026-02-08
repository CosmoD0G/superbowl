
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;


import java.awt.*;
import java.util.ArrayList;

class sportsbook {
    // PROP BET Variables
    public static ArrayList<bettor> bets = new ArrayList<>();
    public static bettor key;
    public static bettor confirmed;

    // BOX Variables
    public static ArrayList<String[]> rows = new ArrayList<String[]>();

    public static int scoreNE = 0;
    public static int scoreSEA = 0;



    // Read bettors from CSV file and create bettor objects
    public static void createBettors() {
        bets.clear();
        String filePath = "props.csv"; // Replace with your CSV file path

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int line_num = 1;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // Line 2 is the answer key
                if (line_num == 2) {
                    key = new bettor(values);
                } else if (line_num == 3) {
                    confirmed = new bettor(values);
                } else if (line_num > 3) {
                    bets.add(new bettor(values, key));
                }

                line_num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Bubble sort implementation to sort bettors by score and tiebreaker_error
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

    // swap two elements in arraylist
    private static void swap(ArrayList<bettor> players, int i, int j) {
        bettor temp = players.get(i);
        players.set(i, players.get(j));
        players.set(j, temp);
    }

    public static String getQInfoFromNum(int ques, int ans, boolean isAnswer, String filepath) {
        String optionText = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            int line_num = 1;

            while ((line = br.readLine()) != null) {
                if (line_num == ques + 1) { // +1 to account for header
                    if (!isAnswer) {
                        optionText = line.split(",")[0];
                        break;
                    }
                    String[] options = line.split(",");
                    if (ans >= 1 && ans < options.length) {
                        optionText = options[ans];
                    } else {
                        optionText = "";
                    }
                    break;
                }
                line_num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
    }
    return optionText;
}
        
    public static boolean isConfirmed(int quesIndex) {
        if (quesIndex == 16) { // Tiebreaker question
            return confirmed.tiebreaker != 0;

        }
        return confirmed.props[quesIndex] != 0;
    }

    /*public static void editCell(int colIndex, String newValue) throws IOException {
        ArrayList<String[]> rows = new ArrayList<>();

        // 1. Read CSV
        try (BufferedReader br = new BufferedReader(new FileReader("props.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                rows.add(line.split(","));
            }
        }

        // 2. Modify cell
        if (rowIndex < rows.size() && colIndex < rows.get(rowIndex).length) {
            rows.get(rowIndex)[colIndex] = newValue;
        } else {
            throw new IndexOutOfBoundsException("Invalid row or column index");
        }

        // 3. Rewrite CSV
        try (PrintWriter pw = new PrintWriter(new FileWriter("props.csv"))) {
            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }
        }
    }*/



    // Print leaderboard to console
    public static void printLeaderboard() {
        bubbleSort(bets);
        System.out.println("PRINTING LEADERBOARD\nLENGTH OF BETS ARRAYLIST: " + bets.size() + "\n");

        int i = 1;
        for (bettor b : bets) {
            System.out.print(i + "- " + b.name + " " + b.score + "/15");
            if (b.tiebreaker_active) {
                System.out.print(" TIEBREAKER: off from actual score by: " + b.tiebreaker_error);
            }
            System.out.println();
            i++;
        }
    }

    // Display leaderboard in a JFrame GUI
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

    // Convert HTML file to String
    public static String FileToString(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    // Generate HTML standings for results page based on sorted bettors
    public static String generateHTMLStandings() {
        StringBuilder html = new StringBuilder();






        bubbleSort(bets);
        html.append("<table>\n<tr><th>Rank</th><th>Name</th><th>Score</th><th>Tiebreaker Error</th></tr>\n");
        int i = 0;
        int prevScore = bets.get(0).score;
        String alt_color = "#f9f9f9";
        for (bettor b : bets) {
            if (b.score != prevScore) {
                // Swap alt_color for next row
                if (alt_color.equals("#f9f9f9")) {
                    alt_color = "#e4e4e4";
                } else {
                    alt_color = "#f9f9f9";
                }
            }
            String color = "";
            if (i == 0) {
                color = "#ffeb3b"; // Yellow for first place
            } else if (i == 1) {
                color = "#c0c0c0"; // Silver for second place
            } else {
                color = alt_color; // Alternate color for third and beyond
            }
            prevScore = b.score;
            
            

            html.append("<tr style=\"background-color: "+color+"\"><td>").append(i + 1).append("</td><td class=\"name-cell\">").append(b.name).append("</td><td>").append(b.score).append("/15</td>\n");
            html.append("<td>");
            if (b.tiebreaker_active) {
                html.append(b.tiebreaker_error);
            }
            
            html.append("</td>\n");
            
            i++;
            html.append("</tr>\n");
        }
        html.append("</table>\n");
        return html.toString();
    }

    // Generate HTML for answer key
    public static String generateHTMLanswers() {
        StringBuilder html = new StringBuilder();
        html.append("<h2 class=\"leaderboard-header\">Answer Key</h2>\n<table>\n<tr><th>Prop</th><th>Answer</th></tr>\n");
        for (int i = 0; i < key.props.length; i++) {
            String color = isConfirmed(i) ? "#a8e6cf" : "#f9f9f9";
            html.append("<tr style=\"background-color: "+color+"\"><td>").append(getQInfoFromNum(i+1, key.props[i], false, "answerMap.csv")).append("</td><td>").append(getQInfoFromNum(i+1, key.props[i], true, "answerMap.csv")).append("</td></tr>\n");
        }
        String color = isConfirmed(16) ? "#a8e6cf" : "#f9f9f9";
        html.append("<tr style=\"background-color: "+color+"\"><td>").append("Tiebreaker (Total Points)").append("</td><td>").append(key.tiebreaker).append("</td></tr>\n");
        html.append("</table>\n");
        return html.toString();
    }

    // Assemble final HTML results page
    public static void assembleHTML() throws IOException {
        String htmlTemplate = """
        <!DOCTYPE html>
        <html lang="en">
           <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Superbowl LIX Prop Bet Results</title>
            <style>%s</style>
        </head>
        <body>
            <div class="container">
                
                
                <h1 class="leaderboard-header">Superbowl LX Prop Bet Results</h1>
                <button onclick="location.href='boxes.html'">Go to Boxes</button>
                <div class="leaderboard">
                    <h2 class="leaderboard-header">Leaderboard</h2>
                    %s
                </div>
                <div class="answers">
                    %s
                </div>
            </div>
        </body>
        </html>
        """;


        //String answersHTML = generateHTMLanswers();
        String finalHTML = String.format(htmlTemplate, FileToString("style.css"), generateHTMLStandings(), generateHTMLanswers());

        // Write finalHTML to a file named "results.html"
        try (java.io.FileWriter writer = new java.io.FileWriter("results.html")) {
            writer.write(finalHTML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




// ===========================================================================
// ========================== BOXES PAGE CODE BELOW ==========================
// ===========================================================================


    public static void createBoxes() {
        String filePath = "boxes.csv"; // Replace with your CSV file path

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int line_num = 1;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                rows.add(values);
                line_num++;
                System.out.println("Read line " + line_num + ": " + String.join(", ", values));
            }
        } catch (IOException e) {
            e.printStackTrace();
    }
}


    public static String BOXgenerateHTMLStandings() {
        StringBuilder html = new StringBuilder();


        createBoxes();
        html.append("<table>\n");
        int i = 0;
        int j = 0;
        for (String r[] : rows) {
            html.append("<tr>\n");
            if (i==0) {
                i++;
                continue; // Skip header row
            }
            for (String c : r) {
                System.out.println(c);
                int NEscore = Integer.parseInt(r[0]);
                if (j==0) {
                    j++;
                    continue; // Skip NE score column
                }
                if (NEscore == scoreNE && Integer.parseInt(rows.get(0)[j]) == scoreSEA) {
                    html.append("<td style=\"background-color: #3bff72\">").append(c).append("</td>");
                } else {
                    html.append("<td style=\"background-color: #f9f9f9\">").append(c).append("</td>");
                }

                }
                j++;
                
            }
            html.append("</tr>\n");
            i++;
        
        html.append("</table>\n");
        return html.toString();
    }




    // Assemble final HTML results page
    public static void BOXassembleHTML() throws IOException {
        String htmlTemplate = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Superbowl LIX Score Boxes</title>
            <style>%s</style>
        </head>
        <body>
            <div class="container">
                
                
                <h1 class="leaderboard-header">Superbowl LX Score Boxes Results</h1>
                <button onclick="location.href='results.html'">Go to Prop Bets</button>
                <div class="leaderboard">
                    <h2 class="leaderboard-header">Leaderboard</h2>
                    %s
                </div>
                <div class="answers">
                    %s
                </div>
            </div>
        </body>
        </html>
        """;


        //String answersHTML = generateHTMLanswers();
        String finalHTML = String.format(htmlTemplate, FileToString("style.css"), BOXgenerateHTMLStandings(), "");

        // Write finalHTML to a file named "boxes.html"
        try (java.io.FileWriter writer = new java.io.FileWriter("boxes.html")) {
            writer.write(finalHTML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        createBettors();
        //printLeaderboard(); // Print to console
        System.out.println("Leaderboard updated with " + bets.size() + " bettors.");
        //displayLeaderboardGUI(); // Show in GUI window
        try {
            assembleHTML();
            BOXassembleHTML();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public sportsbook() throws IOException {
        update();
    }
}