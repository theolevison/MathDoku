package app;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MathDokuModel {
    private MathDokuCell prevStack;
    private MathDokuCell currentStack;
    private int cellDimensions;
    private int gridDimensions;
    private MathDokuCell[][] grid;

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

    // TODO: Do all the maths here? Fill out a matrix and calculate the cages
    // afterwards
    // TODO: consider using map or something to check for duplicate values
    

    public void addCell(MathDokuCell cell, int x, int y) {
        grid[x][y] = cell;
    }

    public void check(){
        checkColumnns();
        checkRows();
        /*
        for (int i = 0; i < gridDimensions; i++) {
            Set<Integer> rowSet = new HashSet<Integer>();
            Set<Integer> columnSet = new HashSet<Integer>();
            for (int j = 0; j < gridDimensions; j++) {
                //attempt to add number to a set, if there is a conflict then highlight the row/column
                try {
                    //check rows
                    if (!rowSet.add(Integer.parseInt(grid[j][i].getNumber()))){
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[k][i].setRowConflict(true);
                        }
                        break;
                    } else {
                        grid[j][i].setRowConflict(false);
                    }
                } catch (NumberFormatException e) {
                    //Not all cells filled in so error thrown and caught
                }

                try {
                    //check columns
                    if(!columnSet.add(Integer.parseInt(grid[i][j].getNumber()))){
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[i][k].setColumnConflict(true);
                        }
                        break;
                    } else {
                        grid[i][j].setColumnConflict(false);
                    }
                } catch (NumberFormatException e) {
                    //Not all cells filled in so error thrown and caught
                }
            }
        }

        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                if (!grid[i][j].hasConflict()){
                    grid[i][j].highlight(Color.TRANSPARENT);
                } else {
                    grid[i][j].highlight(Color.RED);
                }
            }
        }
        */
    }

    //TODO: make these use the same type of for loop, for easier readability
    //check for rule breaking in the columns
    private void checkColumnns() {
        for (MathDokuCell[] mathDokuColumn : grid) {
            Set<Integer> set = new HashSet<Integer>();
            for (int i = 0; i < gridDimensions; i++) {
                //TODO: will using treeSet speed up search?

                //attempt to add number to set, if there is a conflict then highlight the row
                System.out.println(mathDokuColumn[i].getNumber());
                try {
                    if (!set.add(Integer.parseInt(mathDokuColumn[i].getNumber()))){
                        for (MathDokuCell cell : mathDokuColumn) {
                            cell.highlight(Color.RED);
                            cell.setColumnConflict(true);
                        }
                        //only need to find the first conflict to highlight it, so move to the next column
                        break;
                    } else {
                        //if everything is correct unhighlight
                        for (MathDokuCell cell : mathDokuColumn) {
                            cell.setColumnConflict(false);
                            if (!cell.hasConflict()){
                                cell.highlight(Color.TRANSPARENT);
                            }
                            //cell.highlight(Color.TRANSPARENT);
                        }
                    }  
                } catch (NumberFormatException e) {
                    //Not all cells filled in so error thrown and caught
                }

                
            }
        }
    }

    //only need to find the first conflict to highlight it
    private void checkRows() {
        for (int i = 0; i < gridDimensions; i++) {
            Set<Integer> set = new HashSet<Integer>();
            for (int j = 0; j < gridDimensions; j++) {
                MathDokuCell cell = grid[j][i];

                //attempt to add number to set, if there is a conflict then highlight the row
                //System.out.println(cell.getNumber());
                try {
                    if (!set.add(Integer.parseInt(cell.getNumber()))){
                        for (int k = 0; k < gridDimensions; k++) {
                            grid[k][i].highlight(Color.RED);
                            grid[k][i].setRowConflict(true);
                        }
                        //only need to find the first conflict to highlight it, so move to the next row
                        break;
                    } else {
                        //if everything is correct unhighlight
                        for (int k = 0; k < gridDimensions; k++) {
                            //grid[k][i].highlight(Color.TRANSPARENT);
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
    //TODO: could make a custom event? Then it could be listened for by multiple filters. Nah probably not tbh mate
}