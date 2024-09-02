package org.example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

public class SudokuIntermediaire {

    public static void addSudokuConstraints(Model model, IntVar[][] grid) {

        for (int i = 0; i < 9; i++) {
            IntVar[] row = new IntVar[9];
            IntVar[] col = new IntVar[9];
            for (int j = 0; j < 9; j++) {
                row[j] = grid[i][j];
                col[j] = grid[j][i];
            }
            model.allDifferent(row).post();
            model.allDifferent(col).post();
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
                model.allDifferent(block).post();
            }
        }

    }


    public static boolean hasUniqueSolution(int[][] grid) {
        Model model = new Model("Sudoku");
        IntVar[][] sudokuGrid = model.intVarMatrix("grid", 9, 9, 1, 9);

        // Ajout des contraintes Sudoku standard
        SudokuIntermediaire.addSudokuConstraints(model, sudokuGrid);

        // Ajout des valeurs connues dans la grille
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] != 0) {
                    model.arithm(sudokuGrid[i][j], "=", grid[i][j]).post();
                }
            }
        }

        Solver solver = model.getSolver();
        solver.setSearch(Search.inputOrderLBSearch(ArrayUtils.flatten(sudokuGrid))); // Stratégie de recherche
        int numSolutions = 0;
        while (solver.solve()) {
            numSolutions++;
            if (numSolutions > 1) {
                return false; // Plus d'une solution trouvée, pas de solution unique
            }
        }
        return numSolutions == 1; // Une seule solution trouvée
    }

    public static boolean compareArray(int[][] sudokuSolution) {
        Model model = new Model("Sudoku");
        IntVar[][] grid = model.intVarMatrix("grid", 9, 9, 1, 9);
        SudokuIntermediaire.addSudokuConstraints(model, grid);

        // Alimentation des données dans le modèle
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudokuSolution[i][j] != 0) {
                    model.arithm(grid[i][j], "=", sudokuSolution[i][j]).post();
                }
            }
        }

        // Résolution
        Solver solver = model.getSolver();
        return solver.solve();
    }


    public static void measurePerformance(Model model, IntVar[][] grid) {
        addSudokuConstraints(model, grid);

        Solver solver = model.getSolver();
        solver.setSearch(Search.inputOrderLBSearch(ArrayUtils.flatten(grid)));

        long startTime = System.currentTimeMillis();
        solver.solve();
        long endTime = System.currentTimeMillis();

        System.out.println("Intermédiaire --> Temps de résolution: " + (endTime - startTime) + " ms, Backtracks: " + solver.getBackTrackCount());
    }
}
