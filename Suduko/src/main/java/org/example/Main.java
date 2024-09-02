package org.example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import javax.swing.*;
import java.awt.*;
// Classe principale qui hérite de JFrame pour créer l'interface utilisateur
public class Main extends JFrame {

    // Constructeur de la classe Main
    public Main() {
        setTitle("Sudoku avec ChocoSolver");  // Titre de la fenêtre
        setSize(500, 500);                    // Définit la taille de la fenêtre
        setLocationRelativeTo(null);          // Positionne la fenêtre au centre de l'écran
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Opération à l'exécution de la fermeture de la fenêtre

        JTabbedPane tabbedPane = new JTabbedPane();  // Crée un panneau d'onglets pour différents niveaux de Sudoku

        // Ajoute des onglets pour chaque niveau de jeu
        tabbedPane.addTab("Basique", createSudokuPane1());
        tabbedPane.addTab("Intermédiaire", createSudokuPane2());
        tabbedPane.addTab("Avancé", createSudokuPane3());

        add(tabbedPane);  // Ajoute le panneau d'onglets à la fenêtre
    }

    // Méthode pour créer le panneau de Sudoku de niveau basique
    private JPanel createSudokuPane1() {
        final int SIZE = 9;
        Model model = new Model("Advanced Sudoku");
        IntVar[][] grid = model.intVarMatrix("grid", SIZE, SIZE, 1, SIZE);
        SudokuBasique.addArcConsistencyConstraints(model, grid);
        model.getSolver().solve(); // Résout le modèle pour trouver une solution initiale

        int[][] gridCopy = new int[SIZE][SIZE];
        // Copie la grille résolue dans gridCopy
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                gridCopy[i][j] = grid[i][j].getValue();
            }
        }

        int numEmptyCells = 60;  // Nombre de cellules à vider
        for (int k = 0; k < numEmptyCells; k++) {
            int x = (int) (Math.random() * SIZE);
            int y = (int) (Math.random() * SIZE);
            int oldValue = gridCopy[x][y];
            gridCopy[x][y] = 0;
            // Restaure la valeur précédente si la solution n'est pas unique
            if (!SudokuAvance.hasUniqueSolution(gridCopy)) {
                gridCopy[x][y] = oldValue;
            }
        }
        SudokuBasique.measurePerformance(model,grid);  // Mesure et affiche la performance de la résolution
        return SudokuGUI.createSudokuPanel(gridCopy,1);  // Crée et retourne le panneau GUI pour ce Sudoku
    }

//------------------------------------------------------------------------------------------------------------------

    private JPanel createSudokuPane2() {


        final int SIZE = 9; // taille du Sudoku 9x9

        // Création du modèle
        Model model = new Model("Sudoku");

        // Création des variables
        IntVar[][] grid = model.intVarMatrix("grid", SIZE, SIZE, 1, SIZE);

        // Ajout des contraintes Sudoku standard
        SudokuIntermediaire.addSudokuConstraints(model, grid);



        model.getSolver().solve(); // Trouve une solution complète initiale



        // Copier le contenu de la grille dans un nouveau tableau
        int[][] gridCopy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                gridCopy[i][j] = grid[i][j].getValue();
            }
        }
        // Nombre de cases à vider
        int numEmptyCells = 60;

        // Remplacer quelques valeurs par des cases vides
        for (int k = 0; k < numEmptyCells; k++) {
            int x = (int) (Math.random() * SIZE);
            int y = (int) (Math.random() * SIZE);
            int oldValue = gridCopy[x][y];
            gridCopy[x][y] = 0;

            // Vérifier s'il y a toujours une solution unique
            if (!SudokuBasique.hasUniqueSolution(gridCopy)) {
                gridCopy[x][y] = oldValue; // Rétablir la valeur précédente
            }
        }

        SudokuIntermediaire.measurePerformance(model,grid);
        JPanel p= SudokuGUI.createSudokuPanel(gridCopy,2);

        return p;
    }


//--------------------------------------------------------------------------------------------------------------------------

    private JPanel createSudokuPane3() {

        // Identique à createSudokuPane1 mais appelle SudokuIntermediaire au lieu de SudokuBasique
        final int SIZE = 9; // taille du Sudoku 9x9

        Model model = new Model("Sudoku");
        IntVar[][] grid = model.intVarMatrix("grid", SIZE, SIZE, 1, SIZE);

        SudokuAvance.addBinaryConstraints(model, grid);  // Utilisation des contraintes binaires

        model.getSolver().solve(); // Trouve une solution complète initiale

        int[][] gridCopy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                gridCopy[i][j] = grid[i][j].getValue();
            }
        }
        int numEmptyCells = 60;
        for (int k = 0; k < numEmptyCells; k++) {
            int x = (int) (Math.random() * SIZE);
            int y = (int) (Math.random() * SIZE);
            int oldValue = gridCopy[x][y];
            gridCopy[x][y] = 0;
            if (!SudokuIntermediaire.hasUniqueSolution(gridCopy)) {
                gridCopy[x][y] = oldValue; // Restauration si pas unique
            }
        }
        SudokuAvance.measurePerformance(model,grid);
        return SudokuGUI.createSudokuPanel(gridCopy,3);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {


                new Main().setVisible(true);
            }
        });
    }
    }
