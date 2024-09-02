package org.example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;
public class SudokuBasique {

    public static void addArcConsistencyConstraints(Model model, IntVar[][] grid) {
        for (int i = 0; i < 9; i++) {
            IntVar[] row = new IntVar[9];
            IntVar[] col = new IntVar[9];
            for (int j = 0; j < 9; j++) {
                row[j] = grid[i][j];
                col[j] = grid[j][i];
            }
            model.allDifferent(row, "AC").post();
            model.allDifferent(col, "AC").post();
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
                model.allDifferent(block, "AC").post();
            }
        }
    }
    public static boolean hasUniqueSolution(int[][] grid) {
        Model model = new Model("Advanced Sudoku Solver");
        IntVar[][] sudokuGrid = model.intVarMatrix("grid", 9, 9, 1, 9);

        addArcConsistencyConstraints(model, sudokuGrid);

        // Populate known values
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] != 0) {
                    model.arithm(sudokuGrid[i][j], "=", grid[i][j]).post();
                }
            }
        }

        Solver solver = model.getSolver();
        solver.setSearch(Search.minDomLBSearch(ArrayUtils.flatten(sudokuGrid)));
        boolean hasSolution = solver.solve();
        return hasSolution && !solver.solve(); // Check for uniqueness
    }


    public static boolean compareArray(int[][] sudokuSolution) {
        Model model = new Model("Advanced Sudoku Validation");
        IntVar[][] grid = model.intVarMatrix("grid", 9, 9, 1, 9);

        addArcConsistencyConstraints(model, grid);  // Use advanced constraints for this model

        // Set known values
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudokuSolution[i][j] != 0) {
                    model.arithm(grid[i][j], "=", sudokuSolution[i][j]).post();
                }
            }
        }

        Solver solver = model.getSolver();
        solver.setSearch(Search.inputOrderLBSearch(ArrayUtils.flatten(grid)));
        boolean hasSolution = solver.solve();
        return hasSolution && !solver.solve(); // Check for uniqueness
    }

    public static void measurePerformance(Model model, IntVar[][] grid) {
        addArcConsistencyConstraints(model, grid);

        Solver solver = model.getSolver();
        solver.setSearch(Search.minDomLBSearch(ArrayUtils.flatten(grid)));

        long startTime = System.currentTimeMillis();
        boolean hasSolution = solver.solve();
        long endTime = System.currentTimeMillis();

        System.out.println("Basique --> Temps de résolution: " + (endTime - startTime) + " ms, Backtracks: " + solver.getBackTrackCount());
    }
}
