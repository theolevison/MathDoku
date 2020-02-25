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
    private MathDokuCell[][] grid;;

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

    public StackPane getPrevStack() {
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

    //check for rule breaking in the columns
    public void checkColumnns() {
        for (MathDokuCell[] mathDokuColumn : grid) {
            Set<Integer> set = new HashSet<Integer>();
            for (int i = 0; i < mathDokuColumn.length; i++) {
                //TODO: will using treeSet speed up search?

                //attempt to add number to set, if there is a conflict then highlight the row
                System.out.println(mathDokuColumn[i].getNumber());
                try {
                    if (!set.add(Integer.parseInt(mathDokuColumn[i].getNumber()))){
                        for (MathDokuCell cell : mathDokuColumn) {
                            cell.highlight(Color.RED);
                        }
                        //only need to find the first conflict to highlight it, so move to the next column
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Not all cells filled in so error thrown and caught");
                }

                
            }
        }
    }

    //only need to find the first conflict to highlight it
    private void checkRows() {
        
    }
    //TODO: could make a custom event? Then it could be listened for by multiple filters. Nah probably not tbh mate
}