package app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javafx.scene.paint.Color;

public class MathDokuModel {
    private MathDokuCell prevStack;
    private MathDokuCell currentStack;
    private int cellDimensions;
    private int gridDimensions;
    private MathDokuCell[][] grid;
    private ArrayList<MathDokuCage> cages = new ArrayList<MathDokuCage>();

    public int getGridDimensions() {
        return this.gridDimensions;
    }

    public void setGridDimensions(int gridDimensions) {
        this.gridDimensions = gridDimensions;
        grid = new MathDokuCell[gridDimensions][gridDimensions];
    }

    public MathDokuCell getCurrentStack() {
        return this.currentStack;
    }

    public void setCurrentStack(MathDokuCell currentStack) {
        this.currentStack = currentStack;
    }

    public int getCellDimensions() {
        return this.cellDimensions;
    }

    public void setCellDimensions(int cellDimensions) {
        this.cellDimensions = cellDimensions;
    }

    public MathDokuCell getPrevStack() {
        return prevStack;
    }

    public void setPrevStack(MathDokuCell prevStack) {
        this.prevStack = prevStack;
    }

    public void generateNewGrid() {
        
        generateCages();
        //decide solution first before drawing and setting target numbers
        //fill single cages to make layout more random and not symmetrical

        for (MathDokuCage mathDokuCage : cages) {
            mathDokuCage.fillSingleCages(gridDimensions);
        }

        generateGeneralSolution();

        //now calculate maths targets
        for (MathDokuCage mathDokuCage : cages) {
            mathDokuCage.fillBigCages(gridDimensions);
        }

        drawCages();
    }

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

    }


    //TODO: make cage extend arraylist again, will make add nicer etc, but dont fuck with the drawing, atm i dont see an easier alternative to making the cells know their cage
    private void generateCages() {
        Random rand = new Random();
        int cellsUsed = 0;
        int maximumCells = gridDimensions*gridDimensions;
        Set<MathDokuCell> usedCellsSet = new HashSet<MathDokuCell>();

        while (cellsUsed < maximumCells) {
            //generate new cage from next available cell that isnt already in a cage. Update the cellsUsed count
            MathDokuCage cageGroup = createCage(usedCellsSet, rand, cellsUsed, maximumCells);
            cellsUsed += cageGroup.size();
            cages.add(cageGroup);
            System.out.println(cellsUsed);
        }
    }
    
    private MathDokuCage createCage(Set<MathDokuCell> usedCellsSet, Random rand, int cellsUsed, int maximumCells){
        // Generate random integers in range 1 to gridDimensions inlusive
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

    //recursively add to cage ahhhhh
    private MathDokuCage addToCage(int numberToAdd, Set<MathDokuCell> usedCellsSet, MathDokuCage cageGroup, int i, int j, int iToAdd, int jToAdd){
        if (numberToAdd == 0){
            return cageGroup;
        } else {
            MathDokuCell cell;
            try {
                cell = grid[i+iToAdd][j+jToAdd];
            } catch (ArrayIndexOutOfBoundsException e){
                //indexes provided are not valid, so begin again at the start of the next row/column
                //ensure tail recursion by passing everything
                if (j >= gridDimensions-1){
                    return addToCage(numberToAdd, usedCellsSet, cageGroup, i, 0, 1, 0);
                } else {
                    return addToCage(numberToAdd, usedCellsSet, cageGroup, 0, j, 0, 1);
                }
            }
            
            if (!usedCellsSet.contains(cell)){
                //update how many cells have been put in the cage
                usedCellsSet.add(cell);
                cageGroup.addCell(cell);
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
                //numberToAdd--;
                return addToCage(--numberToAdd, usedCellsSet, cageGroup, i, j, iToAdd, jToAdd);
            }
            //TODO: cant use this cell, so search right
            //ensure tail recursion by passing everything
            //numberToAdd--;
            return addToCage(--numberToAdd, usedCellsSet, cageGroup, i, j, 1, 0);
        }
    }

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

    public void addCell(MathDokuCell cell, int x, int y) {
        grid[x][y] = cell;
    }

    /*
    public void test(){
        for (int i = 0; i < gridDimensions; i++) {
            grid[i][0].highlight(Color.RED);
        }
    }
    */

    //TODO: see if this can be made more elegant/efficient/use of the same code in both parts/do all highlighting at the end? (idk if that would actually improve performance or make it worse)
    public void check(boolean highlight){
        //have to initalise these to make
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
                                grid[i][k].highlight(Color.TRANSPARENT);
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
                                grid[k][i].highlight(Color.TRANSPARENT);
                            }
                        }
                    }  
                } catch (NumberFormatException e) {
                    //Not all cells filled in so error thrown and caught
                }
            }
        }
    }

    //TODO: idk try to merge with above, not likely to happen tbh
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