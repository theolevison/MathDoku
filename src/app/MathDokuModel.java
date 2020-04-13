package app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import javafx.scene.paint.Color;

/**
 * Handles the game rules, generating problems and genrating solutions.
 * 
 * @author Theo Levison
 */
public class MathDokuModel {
    private MathDokuCell prevCell;
    private MathDokuCell currentCell;
    private int cellDimensions;
    private int gridDimensions;
    private MathDokuCell[][] grid;
    private ArrayList<MathDokuCage> cages = new ArrayList<MathDokuCage>();
    public Stack<MathDokuCell> undoStack = new Stack<MathDokuCell>();
    public Stack<MathDokuCell> redoStack = new Stack<MathDokuCell>();
    private MathDoku mathDoku;

    /**
     * Constructor, reference to mathDoku required for undo/redo.
     * <p>
     * Breaks the ideal rules of OOP, but it's the easiest way to get the required undo/redo functionality.
     * 
     * @param mathDoku The parent class
     */
    public MathDokuModel(MathDoku mathDoku){
        this.mathDoku = mathDoku;
    }

    /**
     * Disables undo and redo buttons if their respective stacks are empty, otherwise enables them.
     */
    public void toggleUndoRedo(){
        mathDoku.enableDisableUndo(undoStack.empty());
        mathDoku.enableDisableRedo(redoStack.empty());
    }

    /**
     * Pushes a cell to the redo stack.
     * 
     * @param cell Cell to add to the redo stack.
     */
    public void pushToRedoStack(MathDokuCell cell){
        redoStack.push(cell);
        System.out.println("added to redo stack");
    }

    /**
     * Pushes a cell to the undo stack.
     * 
     * @param cell Cell to add to the undo stack.
     */
    public void pushToUndoStack(MathDokuCell cell){
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
     * Sets up the matrix to store references to all the cells in, sets gridDimensions variable.
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
     * Adds the cell to the undo stack, as the only time this method is called is when operations are being done on the cell.
     * 
     * @return The currently selected cell.
     */
    public MathDokuCell getCurrentCell() {
        undoStack.push(currentCell);
        System.out.println("added to undo stack");
        return this.currentCell;
    }

    /**
     * Checks if the user has begun to play the game.
     *
     * @return If the user has selected a cell yet.
     */
    public boolean hasCurrentCell(){
        return currentCell != null;
    }

    /**
     * @param currentCell The cell the user has just selected.
     */
    public void setCurrentStack(MathDokuCell currentCell) {
        this.currentCell = currentCell;
    }

    /**
     * @return Cell dimensions.
     */
    public int getCellDimensions() {
        return this.cellDimensions;
    }

    /**
     * @param cellDimensions The dimensions that the cell should be.
     */
    public void setCellDimensions(int cellDimensions) {
        this.cellDimensions = cellDimensions;
    }

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

    //TODO: generate solution from this so that in the future auto complete works
    /**
     * Creates the grid from an List generated from loading a save.
     * <p>
     * Takes the save data and generates a grid from it, allows users to play the same game again and share games with others.
     * 
     * @param list A list of save data
     */
    public void generateFromList(List<String> list) {
        for (String line : list) {
            String[] array1 = line.split(" ");
            String[] targetArray = array1[0].split("");
            String[] numberArray = array1[1].split(",");

            String targetNumber = "";
            for (int i = 0; i < targetArray.length-1; i++) {
                targetNumber += targetArray[i];
            }

            MathDokuCage cage = new MathDokuCage();

            //add cells to cage
            for (int i = 0; i < numberArray.length; i++) {
                Integer num = Integer.parseInt(numberArray[i]);
                Integer thing1 = num%gridDimensions == 0 ? gridDimensions-1 : num%gridDimensions-1;
                Integer thing2 = (num-1)/gridDimensions;
                cage.addCell(grid[thing1][thing2]);
            }

            cage.setTargetNumber(Integer.parseInt(targetNumber),targetArray[targetArray.length-1]);
            cages.add(cage);
        }

        drawCages();
    }

    /**
     * Generates a default grid if no other options are selected.
     */
    public void generateDefaultGrid() {
        MathDokuCage cage1 = new MathDokuCage();
        cage1.addCell(grid[0][0]);
        cage1.addCell(grid[0][1]);
        cage1.setTargetNumber(11,"+");
        cages.add(cage1);

        MathDokuCage cage2 = new MathDokuCage();
        cage2.addCell(grid[1][0]);
        cage2.addCell(grid[2][0]);
        cage2.setTargetNumber(2,"÷");
        cages.add(cage2);

        MathDokuCage cage3 = new MathDokuCage();
        cage3.addCell(grid[1][1]);
        cage3.addCell(grid[2][1]);
        cage3.setTargetNumber(3,"-");
        cages.add(cage3);

        MathDokuCage cage4 = new MathDokuCage();
        cage4.addCell(grid[3][0]);
        cage4.addCell(grid[3][1]);
        cage4.setTargetNumber(20,"x");
        cages.add(cage4);

        MathDokuCage cage5 = new MathDokuCage();
        cage5.addCell(grid[4][0]);
        cage5.addCell(grid[5][0]);
        cage5.addCell(grid[5][1]);
        cage5.addCell(grid[5][2]);
        cage5.setTargetNumber(6,"x");
        cages.add(cage5);

        MathDokuCage cage6 = new MathDokuCage();
        cage6.addCell(grid[4][1]);
        cage6.addCell(grid[4][2]);
        cage6.setTargetNumber(3,"÷");
        cages.add(cage6);

        MathDokuCage cage7 = new MathDokuCage();
        cage7.addCell(grid[0][2]);
        cage7.addCell(grid[1][2]);
        cage7.addCell(grid[0][3]);
        cage7.addCell(grid[1][3]);
        cage7.setTargetNumber(240,"x");
        cages.add(cage7);

        MathDokuCage cage8 = new MathDokuCage();
        cage8.addCell(grid[2][2]);
        cage8.addCell(grid[3][2]);
        cage8.setTargetNumber(6,"x");
        cages.add(cage8);

        MathDokuCage cage9 = new MathDokuCage();
        cage9.addCell(grid[0][4]);
        cage9.addCell(grid[1][4]);
        cage9.setTargetNumber(6,"x");
        cages.add(cage9);

        MathDokuCage cage10 = new MathDokuCage();
        cage10.addCell(grid[2][3]);
        cage10.addCell(grid[2][4]);
        cage10.setTargetNumber(6,"x");
        cages.add(cage10);

        MathDokuCage cage11 = new MathDokuCage();
        cage11.addCell(grid[3][3]);
        cage11.addCell(grid[3][4]);
        cage11.addCell(grid[4][4]);
        cage11.setTargetNumber(7,"+");
        cages.add(cage11);

        MathDokuCage cage12 = new MathDokuCage();
        cage12.addCell(grid[4][3]);
        cage12.addCell(grid[5][3]);
        cage12.setTargetNumber(30,"x");
        cages.add(cage12);

        MathDokuCage cage13 = new MathDokuCage();
        cage13.addCell(grid[0][5]);
        cage13.addCell(grid[1][5]);
        cage13.addCell(grid[2][5]);
        cage13.setTargetNumber(8,"+");
        cages.add(cage13);

        MathDokuCage cage14 = new MathDokuCage();
        cage14.addCell(grid[3][5]);
        cage14.addCell(grid[4][5]);
        cage14.setTargetNumber(2,"÷");
        cages.add(cage14);

        MathDokuCage cage15 = new MathDokuCage();
        cage15.addCell(grid[5][5]);
        cage15.addCell(grid[5][4]);
        cage15.setTargetNumber(9,"+");
        cages.add(cage15);

        drawCages();
    }

    /**
     * Undoes the last operation.
     */
    public void undo(){
        undoStack.pop().undo();
    }

    /**
     * Redoes the last undone operation.
     */
    public void redo(){
        redoStack.pop().redo();
    }

    /**
     * Resets the grid and game back to when the grid was loaded.
     */
    public void clearAllCells(){
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
     * Pseudorandomly creates cages, then generates a sodoku solution, then generates target numbers in the cages, turning it into mathdoku.
     */
    public void generateNewGrid() {
        
        generateCages();
        //decide solution first before drawing and setting target numbers
        //fill single cages to make layout more random and not symmetrical

        for (MathDokuCage mathDokuCage : cages) {
            mathDokuCage.fillSingleCages(gridDimensions);
        }

        
        generateGeneralSolution();

        /*
        //now calculate maths targets
        for (MathDokuCage mathDokuCage : cages) {
            mathDokuCage.fillBigCages(gridDimensions);
        }
        */

        drawCages();
    }

    /**
     * Generate a sodoku solution.
     */
    private void generateGeneralSolution(){
        //generate solution numbers based on normal sudoku rules, taking into account the single cell targets
        //so that check works with solution numbers, we have to setNumber() instead of setSolutionNumber()
        //remember to switch at the end before the grid is rendered
        //never mind I see no easy way to do that without loads of faff and weird coupling
        //just basically copy the code again :(

        //In the unlikely event that there are two single cells with the same number, start the grid generation process over again
        

        if (checkSolutions()){
            generateNewGrid();
            return;
        }
        

        //DO NOT CHANGE SINGLE CAGES!!!
        Set<MathDokuCell> singleCells = new HashSet<MathDokuCell>();
        for (MathDokuCage mathDokuCage : cages) {
                if (cages.size() == 1){
                    singleCells.addAll(mathDokuCage);
                }
        }

        
        //TODO: decide if the outer do-while is necessary
        //TODO: I think this could be improved with sets for rows and columns, then you wouldnt have to try out values that have already been entered
        do {
            System.out.println("hiii");
            //try out solutions until no errors are found
            for (int i = 0; i < gridDimensions; i++) {
                for (int j = 0; j < gridDimensions; j++) {
                    MathDokuCell cell = grid[i][j];

                    //DO NOT CHANGE SINGLE CAGES!!!
                    if (!singleCells.contains(cell)){
                        for (int k = 1; k <= gridDimensions; k++) {
                            //try out a number
                            cell.setSolutionNumber(k);
                            //then check it hasnt broken any rules
                            if (checkSolutions()){
                                System.out.println(k);
                                //System.out.println(count);
                                break;
                            }
                        }
                    }
                }
            }
            //TODO: get rid of this or make it !checkSolutions()
        } while (checkSolutions());
        

        //have to do this to reset after using checkSolutions()
        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                MathDokuCell cell = grid[i][j];
                cell.setRowConflict(false);
                cell.setColumnConflict(false);
            }
        }
        check(true);
        

    }

    /**
     * Pseudorandomly generates a set of cages.
     */
    private void generateCages() {
        Random rand = new Random();
        int cellsUsed = 0;
        int maximumCells = gridDimensions*gridDimensions;
        Set<MathDokuCell> usedCellsSet = new HashSet<MathDokuCell>();

        while (cellsUsed < maximumCells) {
            //generate new cage from next available cell that isnt already in a cage. Update the cellsUsed count
            MathDokuCage cage = createCage(usedCellsSet, rand, cellsUsed, maximumCells);
            cellsUsed += cage.size();
            cages.add(cage);
            System.out.println(cellsUsed);
        }
    }
    
    //TODO: work out if i can remove cellsUsed and use usedCellsSet.size() instead
    /**
     * Generate a single cage from unused cells, restricting it's minimum and maximum size.
     * 
     * @param usedCellSet The set of cells that have already been used.
     * @param rand A random seed used to generate the size of the cage.
     * @param cellsUsed The current number of cells in the cage.
     * @param maximumCells The number of cells in the entire grid, gridDimensions*gridDimensions.
     * @return A MathDokuCage with cells in a valid configuration.
     */
    private MathDokuCage createCage(Set<MathDokuCell> usedCellsSet, Random rand, int cellsUsed, int maximumCells){
        // Generate random integers in range 1 to gridDimensions inclusive
        int numberToAdd;

        do {
            //restrict maximum size of cage and prevent it being 0
            numberToAdd = rand.nextInt(Math.floorDiv(gridDimensions*gridDimensions, 3));
        } while (numberToAdd == 0);

        //if there are only a few cells left, use those
        if ((cellsUsed + numberToAdd) > maximumCells){
            numberToAdd = maximumCells - cellsUsed;
        }

        System.out.println(numberToAdd);

        //begin recursive call to generate a cage from the last cell put in the usedCellsSet
        //return addToCage(seed, usedCellsSet, new ArrayList<MathDokuCell>(), 0, usedCellsSet.size(), 0, 0);

        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                
                //if the cell is not in a cage, begin a cage from this cell
                if (!usedCellsSet.contains(grid[i][j])){
                    return addToCage(numberToAdd, usedCellsSet, new MathDokuCage(), i, j, 0, 0);
                }
            }
        }
        //should never be reached
        return new MathDokuCage();
    }

    /**
     * Recursively add cells to a single cage, trying to get a pseudorandom pattern of cells.
     * <p>
     * The first cell is always unused, so that is successfully added to the cage, guaranteeing at least one cell in the cage.
     * The actual recursion then begins.
     * <p>
     * Mate just work it out, cba to explain recursion
     * 
     * @param numberToAdd The amount of cells that this cage should contain.
     * @param usedCellsSet The set of cells that have already been used.
     * @param cage The reference to the this cage that is getting cells added.
     * @param i The x coordinate that is used to find a new cell.
     * @param j The y coordinate that is used to find a new cell.
     * @param iToAdd The amount to add to the x coordinate in the next attempt.
     * @param jToAdd The amount to add to the y coordinate in the next attempt.
     * @return A MathDokuCage with cells in a valid configuration.
     */
    private MathDokuCage addToCage(int numberToAdd, Set<MathDokuCell> usedCellsSet, MathDokuCage cage, int i, int j, int iToAdd, int jToAdd){
        if (numberToAdd == 0){
            return cage;
        } else {
            MathDokuCell cell;
            try {
                cell = grid[i+iToAdd][j+jToAdd];
            } catch (ArrayIndexOutOfBoundsException e){
                //indexes provided are not valid, so begin again at the start of the next row/column
                //ensure tail recursion by passing everything

                //TODO: just accept it and return the cage
                return cage;
                /*
                if (j >= gridDimensions-1){
                    return addToCage(numberToAdd, usedCellsSet, cage, i, 0, 1, 0);
                } else {
                    return addToCage(numberToAdd, usedCellsSet, cage, 0, j, 0, 1);
                }
                */
            }
            
            if (!usedCellsSet.contains(cell)){
                //update how many cells have been put in the cage
                usedCellsSet.add(cell);
                cage.addCell(cell);
                //iterate right over 2 cells
                /*
                    135 etc
                    246
                */
                //prioritise checking below (unless you did that previously)
                if (jToAdd <= iToAdd) {
                    jToAdd++;
                }
                //then check right
                else if (jToAdd > iToAdd){
                    iToAdd++;
                    jToAdd--;
                }
                return addToCage(--numberToAdd, usedCellsSet, cage, i, j, iToAdd, jToAdd);
            }
            //cant use this cell, so search right
            //ensure tail recursion by passing everything
            return addToCage(--numberToAdd, usedCellsSet, cage, i, j, 1, 0);
        }
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
                    if (cell.getCage().equals(grid[i][j+1].getCage())){
                        //do nothing because the cage has a cell below
                    } else {
                        cell.setCageWall(0, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e){
                    cell.setCageWall(0, true);
                }

                try {
                    if (cell.getCage().equals(grid[i-1][j].getCage())){
                        //do nothing because the cage has a cell left
                    } else {
                        cell.setCageWall(1, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e){
                    cell.setCageWall(1, true);
                }

                try {
                    if (cell.getCage().equals(grid[i][j-1].getCage())){
                        //do nothing because the cage has a cell top
                    } else {
                        cell.setCageWall(2, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e){
                    grid[i][j].setCageWall(2, true);
                }

                try {
                    if (cell.getCage().equals(grid[i+1][j].getCage())){
                        //do nothing because the cage has a cell right
                    } else {
                        cell.setCageWall(3, true);
                    }
                } catch (ArrayIndexOutOfBoundsException e){
                    cell.setCageWall(3, true);
                }
            }
        }
    }

    /**
     * Add cell to matrix.
     * 
     * @param cell Cell to add.
     * @param x X index.
     * @param y Y index.
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
    public boolean checkMaths(boolean highlight){
        boolean check = true;

        for (MathDokuCage mathDokuCage : cages) {
            if (!mathDokuCage.checkMaths(highlight)){
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
                if (grid[i][j].getNumber() == ""){
                    return false;
                }
            }
        }
        return true;
    }

    //TODO: see if this can be made more elegant/efficient/use of the same code in both parts/do all highlighting at the end? (idk if that would actually improve performance or make it worse)
    /**
     * Checks the user's answers for row and column conflicts according to sudoku rules.
     * 
     * @param highlight Whether or not to highlight the cells according to the user's decision.
     * @return True if there are no conflicts.
     */
    public boolean check(boolean highlight){
        //have to initalise these to make the compiler stop complaining
        boolean noColumnConflict = false;
        boolean noRowConflict = false;

        for (int i = 0; i < gridDimensions; i++) {
            Set<Integer> columnSet = new HashSet<Integer>();
            Set<Integer> rowSet = new HashSet<Integer>();
            noColumnConflict = true;
            noRowConflict = true;
            for (int j = 0; j < gridDimensions; j++) {
                
                //attempt to add number to columnSet, if there is a conflict then highlight the column
                try {
                    if (!columnSet.add(Integer.parseInt(grid[i][j].getNumber()))){
                        for (int k = 0; k < gridDimensions; k++) {
                            if (highlight){
                                grid[i][k].highlight(Color.RED);
                            }
                            grid[i][k].setColumnConflict(true);
                            noColumnConflict = false;
                            
                        }
                        //only need to find the first conflict to highlight it, so move to the next column
                        //break;
                    } else if (noColumnConflict){
                        //if everything is correct unhighlight
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[i][k].setColumnConflict(false);
                            if (!grid[i][k].hasConflict()){
                                grid[i][k].highlight(Color.GREY);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    //Not all cells filled in so error thrown and caught
                }

                //attempt to add number to rowSet, if there is a conflict then highlight the row
                try {
                    if (!rowSet.add(Integer.parseInt(grid[j][i].getNumber()))){
                        for (int k = 0; k < gridDimensions; k++) {
                            if (highlight){
                                grid[k][i].highlight(Color.RED);
                            }
                            grid[k][i].setRowConflict(true);
                            noRowConflict = false;
                        }
                        //only need to find the first conflict to highlight it, so move to the next row
                        //break;
                    } else if (noRowConflict){
                        //if everything is correct unhighlight
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[k][i].setRowConflict(false);
                            if (!grid[k][i].hasConflict()){
                                grid[k][i].highlight(Color.GREY);
                            }
                        }
                    }  
                } catch (NumberFormatException e) {
                    //Not all cells filled in so error thrown and caught
                }
            }
        }
        return noColumnConflict && noRowConflict;
    }

    //TODO: idk try to merge with above, not likely to happen tbh
    /**
     * Checks the solution numbers for row and column conflicts according to sudoku rules, does not highlight.
     * 
     * @return True there are no conflicts.
     */
    public boolean checkSolutions(){
        //have to initalise these to make the compiler stop complaining
        boolean noColumnConflict = false;
        boolean noRowConflict = false;

        for (int i = 0; i < gridDimensions; i++) {
            Set<Integer> columnSet = new HashSet<Integer>();
            Set<Integer> rowSet = new HashSet<Integer>();
            noColumnConflict = true;
            noRowConflict = true;
            for (int j = 0; j < gridDimensions; j++) {
                
                //attempt to add number to columnSet, check if there is a conflict
                try {
                    if (!columnSet.add(grid[i][j].getSolutionNumber())){
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[i][k].setColumnConflict(true);
                            noColumnConflict = false;
                        }
                        //only need to find the first conflict, so move to the next column
                        //break;
                    } else if (noColumnConflict){
                        //everything is correct
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[i][k].setColumnConflict(false);
                        }
                    }
                } catch (NumberFormatException e) {
                    //Not all cells filled in so error thrown and caught
                }

                //attempt to add number to rowSet, check if there is a conflict
                try {
                    if (!rowSet.add(grid[j][i].getSolutionNumber())){
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[k][i].setRowConflict(true);
                            noRowConflict = false;
                        }
                        //only need to find the first conflict, so move to the next row
                        //break;
                    } else if (noRowConflict){
                        //everything is correct
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[k][i].setRowConflict(false);
                        }
                    }  
                } catch (NumberFormatException e) {
                    //Not all cells filled in so error thrown and caught
                }
            }
        }

        return noColumnConflict && noRowConflict;
    }
}