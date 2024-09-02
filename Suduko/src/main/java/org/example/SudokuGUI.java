package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuGUI {

    private static final Color[] BLOCK_COLORS = {
            new Color(255, 220, 220), // Light red
            new Color(220, 255, 220), // Light green
            new Color(220, 220, 255), // Light blue
            new Color(255, 255, 220), // Light yellow
            new Color(220, 255, 255), // Light cyan
            new Color(255, 220, 255), // Light magenta
            new Color(255, 233, 210), // Light orange
            new Color(210, 233, 255), // Light sky
            new Color(233, 210, 255)  // Light purple
    };

    public static JPanel createSudokuPanel(int[][] grid , int a) {
        JPanel panel = new JPanel(new GridLayout(9, 9));
        JTextField[][] fields = new JTextField[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                fields[i][j] = new JTextField(2);
                fields[i][j].setHorizontalAlignment(JTextField.CENTER);
                int blockIndex = (i / 3) * 3 + (j / 3);
                fields[i][j].setBackground(BLOCK_COLORS[blockIndex]);
                panel.add(fields[i][j]);
                if (grid[i][j] == 0) {
                    fields[i][j].setText("");
                    fields[i][j].setEditable(true);
                } else {
                    fields[i][j].setText(String.valueOf(grid[i][j]));
                    fields[i][j].setEditable(false);
                }
            }
        }

        JButton checkButton = new JButton("Vérifier la solution");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[][] r = new int[9][9];
                try {
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            r[i][j] = Integer.parseInt(fields[i][j].getText().isEmpty() ? "0" : fields[i][j].getText());
                        }
                    }
                    boolean valid=false;
                    if(a==2) {
                        valid = SudokuBasique.compareArray(r);
                    } else if (a==3){
                       valid = SudokuIntermediaire.compareArray(r);

                    } else if(a==1){
                        valid=SudokuAvance.compareArray(r);
                    }
                    String message = valid ? "Solution valide!" : "Solution incorrecte!";
                    JOptionPane.showMessageDialog(panel, message, "Résultat de la vérification", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Veuillez entrer des nombres valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.add(panel, BorderLayout.CENTER);
        outerPanel.add(checkButton, BorderLayout.SOUTH);

        return outerPanel;
    }

}
