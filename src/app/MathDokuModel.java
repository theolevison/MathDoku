package app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;

/**
 * Handles the game rules, generating problems and genrating solutions.
 * 
 * @author Theo Levison
 */
public class MathDokuModel {
    private MathDokuCell prevCell;
    private MathDokuCell currentCell;
    private int gridDimensions;
    private MathDokuCell[][] grid;
    private ArrayList<MathDokuCage> cages = new ArrayList<MathDokuCage>();
    public Stack<MathDokuCell> undoStack = new Stack<MathDokuCell>();
    public Stack<MathDokuCell> redoStack = new Stack<MathDokuCell>();
    private MathDoku mathDoku;
    private Boolean highlight = false;

    /**
     * Constructor, reference to mathDoku required for undo/redo.
     * <p>
     * Breaks the ideal rules of OOP, but it's the easiest way to get the required
     * undo/redo functionality.
     * 
     * @param mathDoku The parent class
     */
    public MathDokuModel(MathDoku mathDoku) {
        this.mathDoku = mathDoku;
    }

    public void setHighlight(Boolean highlight) {
        this.highlight = highlight;
    }

    public Boolean getHighlight() {
        return highlight;
    }

    /**
     * Disables undo and redo buttons if their respective stacks are empty,
     * otherwise enables them.
     */
    public void toggleUndoRedo() {
        mathDoku.enableDisableUndo(undoStack.empty());
        mathDoku.enableDisableRedo(redoStack.empty());
    }

    /**
     * Pushes a cell to the redo stack.
     * 
     * @param cell Cell to add to the redo stack.
     */
    public void pushToRedoStack(MathDokuCell cell) {
        redoStack.push(cell);
        System.out.println("added to redo stack");
    }

    /**
     * Pushes a cell to the undo stack.
     * 
     * @param cell Cell to add to the undo stack.
     */
    public void pushToUndoStack(MathDokuCell cell) {
        undoStack.push(cell);
        System.out.println("added to undo stack");
    }

    /**
     * @return The grid dimensions
     */
    public int getGridDimensions() {
        return this.gridDimensions;
    }

    /**
     * Sets up the matrix to store references to all the cells in, sets
     * gridDimensions variable.
     * 
     * @param gridDimensions The grid dimensions
     */
    public void setGridDimensions(int gridDimensions) {
        this.gridDimensions = gridDimensions;
        grid = new MathDokuCell[gridDimensions][gridDimensions];
    }

    /**
     * Returns the currently selected cell.
     * <p>
     * Adds the cell to the undo stack, as the only time this method is called is
     * when operations are being done on the cell.
     * 
     * @return The currently selected cell.
     */
    public MathDokuCell getCurrentCell() {
        //pushToUndoStack(currentCell);
        return this.currentCell;
    }

    /**
     * Checks if the user has begun to play the game.
     *
     * @return If the user has selected a cell yet.
     */
    public boolean hasCurrentCell() {
        return currentCell != null;
    }

    /**
     * @param currentCell The cell the user has just selected.
     */
    public void setCurrentStack(MathDokuCell currentCell) {
        this.currentCell = currentCell;
    }

    private DoubleProperty cellDimensions = new SimpleDoubleProperty(0.07);
    public final double getCellDimensions(){return cellDimensions.get();}
    public final void setCellDimensions(double value){cellDimensions.set(value);}
    public DoubleProperty cellDimensionsProperty() {return cellDimensions;}

    /**
     * @return The previously selected cell.
     */
    public MathDokuCell getPrevCell() {
        return prevCell;
    }

    /**
     * @param prevCell The previously selected cell.
     */
    public void setPrevCell(MathDokuCell prevCell) {
        this.prevCell = prevCell;
    }

    // TODO: generate solution from this so that in the future auto complete works
    /**
     * Creates the grid from an List generated from loading a save.
     * <p>
     * Takes the save data and generates a grid from it, allows users to play the
     * same game again and share games with others.
     * 
     * @param list A list of save data
     */
    public void generateFromList(List<String> list) {
        for (String line : list) {
            String[] array1 = line.split(" ");
            String[] targetArray = array1[0].split("");
            String[] numberArray = array1[1].split(",");

            String targetNumber = "";
            String sign = "";
            if (targetArray.length == 1){
                targetNumber = targetArray[0];
            } else {    
                for (int i = 0; i < targetArray.length - 1; i++) {
                    targetNumber += targetArray[i];
                }
                sign = targetArray[targetArray.length - 1];
            }

            

            MathDokuCage cage = new MathDokuCage();

            // add cells to cage
            for (int i = 0; i < numberArray.length; i++) {
                Integer num = Integer.parseInt(numberArray[i]);
                Integer x = num % gridDimensions == 0 ? gridDimensions - 1 : num % gridDimensions - 1;
                Integer y = (num - 1) / gridDimensions;
                cage.addCell(grid[x][y]);
            }

            //set FinalNumber if cage has only one possible solution
            if (cage.size() == 1){
                cage.get(0).setFinalSolutionNumber(Integer.parseInt(targetNumber));
            }

            cage.setTargetNumber(Integer.parseInt(targetNumber), sign);
            cages.add(cage);
        }

        drawCages();
        solve(false);
    }

    /**
     * Generates a default grid if no other options are selected.
     */
    public void generateDefault2Grid() {
        generateFromList(new ArrayList<String>(List.of(
        "3+ 1,3",
        "3+ 2,4"
        )));
    }

    /**
     * Generates a default grid if no other options are selected.
     */
    public void generateDefault3Grid() {
        generateFromList(new ArrayList<String>(List.of(
        "2 1",
        "4+ 3,2",
        "6+ 4,5,6",
        "1 7",
        "5+ 8,9"
        )));
    }

    // TOOD: replace with loading from list
    /**
     * Generates a default grid if no other options are selected.
     */
    public void generateDefault6Grid() {
        generateFromList(new ArrayList<String>(List.of(
        "11+ 1,7",
        "2÷ 2,3",
        "20x 4,10",
        "6x 5,6,12,18",
        "3- 8,9",
        "3÷ 11,17",
        "240x 13,14,19,20",
        "6x 15,16",
        "6x 21,27",
        "7+ 22,28,29",
        "30x 23,24",
        "6x 25,26",
        "9+ 30,36",
        "8+ 31,32,33",
        "2÷ 34,35"
        )));
    }

    /**
     * Undoes the last operation.
     */
    public void undo() {
        undoStack.pop().undo();
    }

    /**
     * Redoes the last undone operation.
     */
    public void redo() {
        redoStack.pop().redo();
    }

    /**
     * Resets the grid and game back to when the grid was loaded.
     */
    public void clearAllCells() {
        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                grid[i][j].clearCell();
            }
        }
        redoStack.clear();
        undoStack.clear();
        toggleUndoRedo();
    }

    /**
     * Pseudorandomly creates cages, then generates a sodoku solution, then
     * generates target numbers in the cages, turning it into mathdoku.
     */
    public void generateNewGrid() {

        // In the unlikely event that there are two single cells with the same number,
        // start the grid generation process over again
        System.out.println("\nnew attempt\n");
        cages.clear();

        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                MathDokuCell cell = grid[i][j];
                cell.setPossibleSolutionNumber(0);
                cell.setFinalSolutionNumber(0);
                cell.getPossibleSolutionList().reset();
            }
        }

        generateCages();
        // decide solution first before drawing and setting target numbers
        // fill single cages to make layout more random and not symmetrical

        for (MathDokuCage mathDokuCage : cages) {
            mathDokuCage.fillSingleCages(gridDimensions);
        }
        
        //!checkSolutions()
        // have to do this to reset highlighting etc after using checkSolutions()
        /*
        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                MathDokuCell cell = grid[i][j];
                cell.setRowConflict(false);
                cell.setColumnConflict(false);
            }
        }
        */
        //check();

        //generateGeneralSodoku();
        if (!solve(false)){
            generateNewGrid();
            return;
        }
        

        //now calculate maths targets
        for (MathDokuCage mathDokuCage : cages) {
            mathDokuCage.fillBigCages(gridDimensions);
        }

        drawCages();
    }

    /**
     * Randomly generates a set of cages.
     */
    private void generateCages() {
        Set<MathDokuCell> usedCellsSet = new HashSet<MathDokuCell>();

        while (usedCellsSet.size() < gridDimensions * gridDimensions) {
            // generate new cage from next available cell that isnt already in a cage.
            MathDokuCage cage = createCage(usedCellsSet);
            cages.add(cage);
        }
    }

    /**
     * Generate a single cage from unused cells, restricting it's minimum and
     * maximum size.
     * 
     * @param usedCellSet The set of cells that have already been used.
     * @return A MathDokuCage with cells in a valid configuration.
     */
    private MathDokuCage createCage(Set<MathDokuCell> usedCellsSet) {

        Random rand = new Random();
        int maximumCells = gridDimensions * gridDimensions;
        int numberToAdd;

        do {
            // restrict maximum size of cage to 6 and prevent it being 0.
            numberToAdd = rand.nextInt(6);
            // regenerate to reduce the probability of single cages to 1/36, in reality it's
            // higher.
            if (numberToAdd == 1) {
                numberToAdd = rand.nextInt(6);
            }
        } while (numberToAdd == 0);

        // if there are only a few cells left, use those.
        if ((usedCellsSet.size() + numberToAdd) > maximumCells) {
            numberToAdd = maximumCells - usedCellsSet.size();
        }

        // System.out.println("\nnumber to add: " + numberToAdd);

        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {

                // if the cell is not in a cage, begin a cage from this cell.
                if (!usedCellsSet.contains(grid[i][j])) {

                    Set<MathDokuCell> cellsToAddToCage = generateCageSet(numberToAdd, usedCellsSet,
                            new HashSet<MathDokuCell>(), i, j);
                    System.out.println(cellsToAddToCage.size());

                    // once the set has been generated, create a cage with the cells in the set.
                    MathDokuCage cage = new MathDokuCage();
                    for (MathDokuCell mathDokuCell : cellsToAddToCage) {
                        cage.addCell(mathDokuCell);
                    }
                    return cage;
                }
            }
        }

        // should never be reached
        return new MathDokuCage();
    }

    /**
     * Recursively generates a set of adjacent cells without an existing cage.
     * <p>
     * The first cell is always unused, so that is successfully added to the cage,
     * guaranteeing at least one cell in the cage. There is a 60% chance to branch
     * in each direction to create a random pattern of cells.
     * 
     * @param numberToAdd      The amount of cells that this cage should contain.
     * @param usedCellsSet     The set of cells that have already been used.
     * @param cellsToAddToCage The set of cells that will eventually become the
     *                         cage.
     * @param i                The x coordinate that is used to find a new cell.
     * @param j                The y coordinate that is used to find a new cell.
     * @return A set of cells in the cage.
     */
    private Set<MathDokuCell> generateCageSet(Integer numberToAdd, Set<MathDokuCell> usedCellsSet,
            Set<MathDokuCell> cellsToAddToCage, int i, int j) {

        Random rand = new Random();
        MathDokuCell cell;

        try {
            cell = grid[i][j];
        } catch (IndexOutOfBoundsException e) {
            // can't add to cage because the grid border has been hit, to stop this
            // happening forever, take 1 from numberToAdd.
            numberToAdd--;
            return cellsToAddToCage;
        }

        if (numberToAdd <= cellsToAddToCage.size() || usedCellsSet.contains(cell)) {
            return cellsToAddToCage;
        } else {
            // add this cell to the cage.
            usedCellsSet.add(cell);
            cellsToAddToCage.add(cell);

            // find neighbours, call addToCage on those.
            // to create cages with interesting shapes, give a 60% chance to progress in
            // each direction.

            if (rand.nextInt(10) < 6) {
                // try above
                cellsToAddToCage.addAll(generateCageSet(numberToAdd, usedCellsSet, cellsToAddToCage, i, j - 1));
            }

            if (rand.nextInt(10) < 6) {
                // try below
                cellsToAddToCage.addAll(generateCageSet(numberToAdd, usedCellsSet, cellsToAddToCage, i, j + 1));
            }

            if (rand.nextInt(10) < 6) {
                // try left
                cellsToAddToCage.addAll(generateCageSet(numberToAdd, usedCellsSet, cellsToAddToCage, i - 1, j));
            }

            if (rand.nextInt(10) < 6) {
                // try right
                cellsToAddToCage.addAll(generateCageSet(numberToAdd, usedCellsSet, cellsToAddToCage, i + 1, j));
            }
        }
        return cellsToAddToCage;
    }

    /**
     * Automatically draw the walls for the cages.
     */
    private void drawCages() {
        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                MathDokuCell cell = grid[i][j];
                cell.setTargetNumber(cell.getCage().display());

                try {
                    if (cell.getCage().equals(grid[i][j + 1].getCage())) {
                        // do nothing because the cage has a cell below
                    } else {
                        cell.setCageWall(0, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    cell.setCageWall(0, true);
                }

                try {
                    if (cell.getCage().equals(grid[i - 1][j].getCage())) {
                        // do nothing because the cage has a cell left
                    } else {
                        cell.setCageWall(1, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    cell.setCageWall(1, true);
                }

                try {
                    if (cell.getCage().equals(grid[i][j - 1].getCage())) {
                        // do nothing because the cage has a cell top
                    } else {
                        cell.setCageWall(2, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    grid[i][j].setCageWall(2, true);
                }

                try {
                    if (cell.getCage().equals(grid[i + 1][j].getCage())) {
                        // do nothing because the cage has a cell right
                    } else {
                        cell.setCageWall(3, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    cell.setCageWall(3, true);
                }
            }
        }
    }

    /**
     * Add cell to matrix.
     * 
     * @param cell Cell to add.
     * @param x    X index.
     * @param y    Y index.
     */
    public void addCell(MathDokuCell cell, int x, int y) {
        grid[x][y] = cell;
    }

    /**
     * Checks if the cage's math's rules are being followed.
     * 
     * @param highlight Whether or not to highlight the cells.
     * @return If the maths is correct.
     */
    public boolean checkMaths() {
        boolean check = true;

        for (MathDokuCage mathDokuCage : cages) {
            if (!mathDokuCage.checkMaths(highlight)) {
                check = false;
            }
        }

        return check;
    }

    /**
     * @return If the cells are filled
     */
    public boolean checkAllCellsFilled() {
        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                if (grid[i][j].getNumber() == "") {
                    return false;
                }
            }
        }
        return true;
    }

    // TODO: see if this can be made more elegant/efficient/use of the same code in
    // both parts/do all highlighting at the end? (idk if that would actually
    // improve performance or make it worse)
    /**
     * Checks the user's answers for row and column conflicts according to sudoku
     * rules.
     * 
     * @param highlight Whether or not to highlight the cells according to the
     *                  user's decision.
     * @return True if there are no conflicts.
     */
    public boolean check() {
        // have to initalise these to make the compiler stop complaining
        boolean noColumnConflict = false;
        boolean noRowConflict = false;
        boolean globalFlag = true;

        for (int i = 0; i < gridDimensions; i++) {
            Set<Integer> columnSet = new HashSet<Integer>();
            Set<Integer> rowSet = new HashSet<Integer>();
            noColumnConflict = true;
            noRowConflict = true;
            for (int j = 0; j < gridDimensions; j++) {

                // attempt to add number to columnSet, if there is a conflict then highlight the column.
                try {
                    if (!columnSet.add(Integer.parseInt(grid[i][j].getNumber()))) {
                        for (int k = 0; k < gridDimensions; k++) {
                            if (highlight) {
                                grid[i][k].highlight(Color.RED);
                            }
                            grid[i][k].setColumnConflict(true);
                            noColumnConflict = false;
                            globalFlag = false;
                        }
                        // only need to find the first conflict to highlight it, so move to the next column.
                    } else if (noColumnConflict) {
                        // if everything is correct unhighlight.
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[i][k].setColumnConflict(false);
                            if (!grid[i][k].hasConflict()) {
                                grid[i][k].highlight(Color.GREY);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Not all cells filled in so error thrown and caught.
                }

                // attempt to add number to rowSet, if there is a conflict then highlight the row.
                try {
                    if (!rowSet.add(Integer.parseInt(grid[j][i].getNumber()))) {
                        for (int k = 0; k < gridDimensions; k++) {
                            if (highlight) {
                                grid[k][i].highlight(Color.RED);
                            }
                            grid[k][i].setRowConflict(true);
                            noRowConflict = false;
                            globalFlag = false;
                        }
                        // only need to find the first conflict to highlight it, so move to the next row.
                    } else if (noRowConflict) {
                        // if everything is correct unhighlight.
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[k][i].setRowConflict(false);
                            if (!grid[k][i].hasConflict()) {
                                grid[k][i].highlight(Color.GREY);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Not all cells filled in so error thrown and caught.
                }
            }
        }
        return globalFlag;
    }


    /**
     * Checks if the cage's math's rules are being followed when auto solving.
     * 
     * @return If the maths is correct.
     */
    public boolean checkMathsSolutions() {
        boolean check = true;

        for (MathDokuCage mathDokuCage : cages) {
            if (!mathDokuCage.checkMathsSolutions()) {
                check = false;
            }
        }

        return check;
    }

    // TODO: idk try to merge with above, not likely to happen tbh
    /**
     * Checks the solution numbers for row and column conflicts according to sudoku rules, does not highlight.
     * 
     * @return True there are no conflicts.
     */
    public boolean checkSolutions() {
        // have to initalise these to make the compiler stop complaining.
        boolean noColumnConflict = false;
        boolean noRowConflict = false;
        boolean globalFlag = true;

        for (int i = 0; i < gridDimensions; i++) {
            Set<Integer> columnSet = new HashSet<Integer>();
            Set<Integer> rowSet = new HashSet<Integer>();
            noColumnConflict = true;
            noRowConflict = true;
            for (int j = 0; j < gridDimensions; j++) {
                //ignore cells without a solution
                if (grid[i][j].getPossibleSolutionNumber() != 0){
                // attempt to add number to columnSet, check if there is a conflict.
                    if (!columnSet.add(grid[i][j].getPossibleSolutionNumber())) {
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[i][k].setColumnConflict(true);
                            noColumnConflict = false;
                            globalFlag = false;
                        }
                        // only need to find the first conflict, so move to the next column.
                    } else if (noColumnConflict) {
                        // everything is correct.
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[i][k].setColumnConflict(false);
                        }
                    }

                // attempt to add number to rowSet, check if there is a conflict.
                    if (!rowSet.add(grid[j][i].getPossibleSolutionNumber())) {
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[k][i].setRowConflict(true);
                            noRowConflict = false;
                            globalFlag = false;
                        }
                        // only need to find the first conflict, so move to the next row.
                    } else if (noRowConflict) {
                        // everything is correct.
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[k][i].setRowConflict(false);
                        }
                    }
                }
            }
        }
        return globalFlag;
    }

    public void hint() {
        int hintCount = 0;
        Random rand = new Random();
        
        MathDokuCell cell;
        //ensure cell has not been filled in yet.
        do {
            cell = grid[rand.nextInt(gridDimensions)][rand.nextInt(gridDimensions)];
            hintCount++;
        } while (cell.getNumber() != "" && hintCount <= gridDimensions*gridDimensions);

        //randomly reveal a cell's solution.
        pushToUndoStack(cell);
        cell.updateNumber(String.valueOf(cell.getPossibleSolutionNumber()), false);
    }

    public boolean solve(boolean fill) {
        // check for single cells.
        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                if (grid[i][j].getFinalSolutionNumber() != 0) {
                    System.out.println("found a single");
                    // eliminate that number from other cell's possibility sets in the same
                    // row/column.
                    for (int k = 0; k < gridDimensions; k++) {
                        if (k != j){
                            //column.
                            grid[i][k].getPossibleSolutionList().remove(grid[i][j].getFinalSolutionNumber(), 0);
                        }
                        if (k != i){
                            //row.
                            grid[k][j].getPossibleSolutionList().remove(grid[i][j].getFinalSolutionNumber(), 0);
                        }
                    }
                }
            }
        }

        // now start recursively finding a solution.
        // start in top left.
        if (solveRecursively(0, 0)) {
            // solution has been found

            for (int i = 0; i < gridDimensions; i++) {
                for (int j = 0; j < gridDimensions; j++) {
                    MathDokuCell cell = grid[i][j];
                    // TODO: decide if I should bother updating this
                    // cell.setAbsoluteSolutionList(cell.getPossibleSolutionList());
                    cell.setFinalSolutionNumber(cell.getPossibleSolutionNumber());
                    //TODO: enable below if not generating game with this
                    if (fill){
                        cell.updateNumber(Integer.toString(cell.getFinalSolutionNumber()), false);
                    }
                    //cell.updateNumber(Integer.toString(cell.getFinalSolutionNumber()));
                }
            }
            System.out.println("Puzzle has been solved");
            return true;
        } else {
            return false;
        }
    }

    private boolean solveRecursively(int i, int j) {
        MathDokuCell cell = grid[i][j];

        
        if (i == 0 && j == 0 && cell.getPossibleSolutionList().isEmpty()){
            System.out.println("Failed miserably");
        }

        // if solution is known, use that.
        if (cell.getFinalSolutionNumber() != 0) {
            cell.setPossibleSolutionNumber(cell.getFinalSolutionNumber());
        } else {
            // try the first option from the set.
            // set should never be empty at this point fingers crossed.

            
            if (cell.getPossibleSolutionList().isEmpty()) {
                // options exhausted before this cell could do anything, so the error is higher up.

                System.out.println("Child has no prospects, go back once.");
                return false;
            }
            

            cell.setPossibleSolutionNumber(cell.getPossibleSolutionList().get());
        }
        //cell.updateNumber(String.valueOf(cell.getPossibleSolutionNumber()));


        //eliminate that number from other cell's possibility sets in the same row/column.
        //No arguments with parents, only try to delete from cells in front and below.
        
        for (int k = 0; k < gridDimensions; k++) {
            if (k > j){
                //column.
                grid[i][k].getPossibleSolutionList().remove((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
            }
            if (k > i){
                //row.
                grid[k][j].getPossibleSolutionList().remove((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
            }
        }

        boolean success = false;
        
        /*
        if (checkMathsSolutions()){
            */
            if (i < gridDimensions - 1){
                success = solveRecursively(i+1, j);
            } else if (j < gridDimensions - 1){
                success = solveRecursively(0, j+1);
            } else {
                //i = 5 and j =5
                //to get to this point all sodoku rules should have been followed
                success = checkSolutions();// && checkMathsSolutions();
            }
            /*
        }
        */

        if (!success && cell.getFinalSolutionNumber() != 0){
            //has only one possible value so error is before this
            //this shouldnt have changed any values because that has already been done at the beginning

            return false;

        } else if (!success && !cell.getPossibleSolutionList().isEmpty()){
            //error but more options available so remove this attempt and try those.

            System.out.println("Disappointed parent, try to make more children.");
            //try to restore unused option to other cells in the same row/column.

            
            //No sex with parents, only restore cells in front and below.
            for (int k = 0; k < gridDimensions; k++) {
                if (k > j){
                    //column.
                    grid[i][k].getPossibleSolutionList().add((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
                    //grid[i][k].setPossibleSolutionNumber(0);
                }
                if (k > i){
                    //row.
                    grid[k][j].getPossibleSolutionList().add((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
                    //grid[k][j].setPossibleSolutionNumber(0);
                }
            }
            

            cell.getPossibleSolutionList().remove((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);

            return solveRecursively(i, j);

        }
        //TODO: remove this bit after I get maths checking working, I think it's redundant but I want to be sure.
        else if (!success && cell.getPossibleSolutionList().isEmpty()) {
            //options exhausted here so the error is higher up.
            System.out.println("\n\n\nDisappointed parent and exhausted, go back once.\n\n\n");

            //restore the last value used
            for (int k = 0; k < gridDimensions; k++) {
                if (k > j){
                    //column.
                    grid[i][k].getPossibleSolutionList().add((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
                    //grid[i][k].setPossibleSolutionNumber(0);
                }
                if (k > i){
                    //row.
                    grid[k][j].getPossibleSolutionList().add((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
                    //grid[k][j].setPossibleSolutionNumber(0);
                }
            }

            return false;
        }

        //this branch was successful, but the overall attempt may not be, so go up.
        System.out.println("Proud parent: go up");
        return true;
    }






    /**
     * 
     * A solver that doesn't work yay!!!!
     * 
     * 
     * 
     */

    public boolean shitSolve() {
        // check for single cells.
        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                if (grid[i][j].getFinalSolutionNumber() != 0) {
                    System.out.println("found a single");
                    // eliminate that number from other cell's possibility sets in the same
                    // row/column.
                    for (int k = 0; k < gridDimensions; k++) {
                        if (k != j){
                            //column.
                            grid[i][k].getPossibleSolutionList().remove(grid[i][j].getFinalSolutionNumber(), 0);
                        }
                        if (k != i){
                            //row.
                            grid[k][j].getPossibleSolutionList().remove(grid[i][j].getFinalSolutionNumber(), 0);
                        }
                    }
                }
            }
        }

        // now start recursively finding a solution.
        // start in top left.
        if (shitSolveRecursively(0, 0)) {
            // solution has been found

            for (int i = 0; i < gridDimensions; i++) {
                for (int j = 0; j < gridDimensions; j++) {
                    MathDokuCell cell = grid[i][j];
                    // TODO: decide if I should bother updating this
                    // cell.setAbsoluteSolutionList(cell.getPossibleSolutionList());
                    cell.setFinalSolutionNumber(cell.getPossibleSolutionNumber());
                    //TODO: enable below if not generating game with this
                    cell.updateNumber(Integer.toString(cell.getFinalSolutionNumber()), false);
                }
            }
            System.out.println("Puzzle has been solved");
            return true;
        } else {
            return false;
        }
    }

    private boolean shitSolveRecursively(int i, int j) {
        MathDokuCell cell = grid[i][j];

        
        if (i == 0 && j == 0 && cell.getPossibleSolutionList().isEmpty()){
            System.out.println("Failed miserably");
        }

        // if solution is known, use that.
        if (cell.getFinalSolutionNumber() != 0) {
            cell.setPossibleSolutionNumber(cell.getFinalSolutionNumber());
        } else {
            // try the first option from the set.
            // set should never be empty at this point fingers crossed.

            
            if (cell.getPossibleSolutionList().isEmpty()) {
                // options exhausted before this cell could do anything, so the error is higher up.

                System.out.println("Child has no prospects, go back once.");
                cell.setPossibleSolutionNumber(0);
                return false;
            }
            

            cell.setPossibleSolutionNumber(cell.getPossibleSolutionList().get());
        }
        //cell.updateNumber(String.valueOf(cell.getPossibleSolutionNumber()));


        //eliminate that number from other cell's possibility sets in the same row/column.
        //No arguments with parents, only try to delete from cells in front and below.
        
        for (int k = 0; k < gridDimensions; k++) {
            if (k > j){
                //column.
                grid[i][k].getPossibleSolutionList().remove((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
            }
            if (k > i){
                //row.
                grid[k][j].getPossibleSolutionList().remove((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
            }
        }

        boolean success = false;
        
        
        if (checkMathsSolutions()){
            if (i < gridDimensions - 1){
                success = solveRecursively(i+1, j);
            } else if (j < gridDimensions - 1){
                success = solveRecursively(0, j+1);
            } else {
                //i = 5 and j =5
                //to get to this point all sodoku rules should have been followed
                success = checkSolutions();// && checkMathsSolutions();
            }
            
        }
        

        if (!success && cell.getFinalSolutionNumber() != 0){
            //has only one possible value so error is before this
            //this shouldnt have changed any values because that has already been done at the beginning
            cell.setPossibleSolutionNumber(0);
            return false;

        } else if (!success && !cell.getPossibleSolutionList().isEmpty()){
            //error but more options available so remove this attempt and try those.

            System.out.println("Disappointed parent, try to make more children.");
            //try to restore unused option to other cells in the same row/column.

            //TODO: restore child if they are below and before you
            //No sex with parents, only restore cells in front and below.
            for (int k = 0; k < gridDimensions; k++) {
                if (k > j){
                    //column.
                    //grid[i][k].getPossibleSolutionList().add((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
                    //restore the childrens values as much as you can.
                    for (int k2 = 1; k2 < gridDimensions+1; k2++) {
                        grid[i][k].getPossibleSolutionList().add(k2, i+j*gridDimensions);
                    }
                }
                if (k > i){
                    //row.
                    //grid[k][j].getPossibleSolutionList().add((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
                    //restore the childrens values as much as you can.
                    for (int k2 = 1; k2 < gridDimensions+1; k2++) {
                        grid[k][j].getPossibleSolutionList().add(k2, i+j*gridDimensions);
                    }
                }
            }
            

            cell.getPossibleSolutionList().remove((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);

            return solveRecursively(i, j);

        }
        //TODO: remove this bit after I get maths checking working, I think it's redundant but I want to be sure.
        else if (!success && cell.getPossibleSolutionList().isEmpty()) {
            //options exhausted here so the error is higher up.
            System.out.println("\n\n\nDisappointed parent and exhausted, go back once.\n\n\n");

            //restore the last value used
            for (int k = 0; k < gridDimensions; k++) {
                if (k > j){
                    //column.
                    //grid[i][k].getPossibleSolutionList().add((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
                    //restore the childrens values as much as you can.
                    for (int k2 = 1; k2 < gridDimensions+1; k2++) {
                        grid[i][k].getPossibleSolutionList().add(k2, i+j*gridDimensions);
                    }
                }
                if (k > i){
                    //row.
                    //grid[k][j].getPossibleSolutionList().add((Integer)cell.getPossibleSolutionNumber(), i+j*gridDimensions);
                    //restore the childrens values as much as you can.
                    for (int k2 = 1; k2 < gridDimensions+1; k2++) {
                        grid[k][j].getPossibleSolutionList().add(k2, i+j*gridDimensions);
                    }
                }
                //restore your own values as much as you can.
                cell.getPossibleSolutionList().add(k, i+j*gridDimensions);
            }

            cell.setPossibleSolutionNumber(0);
            

            return false;
        }

        //this branch was successful, but the overall attempt may not be, so go up.
        System.out.println("Proud parent: go up");
        return true;
    }



}