package org.example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;
public class SudokuAvance {

    public static void addBinaryConstraints(Model model, IntVar[][] grid) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = j + 1; k < 9; k++) {
                    model.arithm(grid[i][j], "!=", grid[i][k]).post();  // row constraints
                    model.arithm(grid[j][i], "!=", grid[k][i]).post();  // column constraints
                }
            }
        }

        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                IntVar[] block = new IntVar[9];
                int idx = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        block[idx++] = grid[blockRow * 3 + i][blockCol * 3 + j];
                    }
                }
                for (int m = 0; m < block.length; m++) {
                    for (int n = m + 1; n < block.length; n++) {
                        model.arithm(block[m], "!=", block[n]).post();
                    }
                }
            }
        }
    }

    public static boolean hasUniqueSolution(int[][] grid) {
        Model model = new Model("Sudoku");
        IntVar[][] sudokuGrid = model.intVarMatrix("grid", 9, 9, 1, 9);

        addBinaryConstraints(model, sudokuGrid);  // Utilisation des contraintes binaires

        // Ajout des valeurs connues dans la grille
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] != 0) {
                    model.arithm(sudokuGrid[i][j], "=", grid[i][j]).post();
                }
            }
        }

        Solver solver = model.getSolver();
        solver.setSearch(Search.inputOrderLBSearch(ArrayUtils.flatten(sudokuGrid)));
        int numSolutions = 0;
        while (solver.solve()) {
            numSolutions++;
            if (numSolutions > 1) {
                return false; // Plus d'une solution trouvée, pas de solution unique
            }
        }
        return numSolutions == 1; // Une seule solution trouvée
    }
    // Méthode équivalente à compareArray pour le modèle intermédiaire
    public static boolean compareArray(int[][] sudokuSolution) {
        Model model = new Model("Sudoku");
        IntVar[][] grid = model.intVarMatrix("grid", 9, 9, 1, 9);
        addBinaryConstraints(model, grid);  // Utilisation des contraintes binaires pour ce modèle

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudokuSolution[i][j] != 0) {
                    model.arithm(grid[i][j], "=", sudokuSolution[i][j]).post();
                }
            }
        }

        Solver solver = model.getSolver();
        return solver.solve();  // Résolution et vérification de la validité de la solution donnée
    }


    public static void measurePerformance(Model model, IntVar[][] grid) {
        addBinaryConstraints(model, grid);

        Solver solver = model.getSolver();
        solver.setSearch(Search.inputOrderLBSearch(ArrayUtils.flatten(grid)));

        long startTime = System.currentTimeMillis();
        solver.solve();
        long endTime = System.currentTimeMillis();

        System.out.println("Avancé --> Temps de résolution: " + (endTime - startTime) + " ms, Backtracks: " + solver.getBackTrackCount());
    }
}
