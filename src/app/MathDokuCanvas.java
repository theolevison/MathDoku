package app;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Handles drawing the cage walls and resizing the canvas.
 * 
 * @author Theo Levison
 */
public class MathDokuCanvas extends Canvas {
    private boolean[] cageWalls = {false, false, false, false};
    private Color defaultColor = Color.GREY;
    private boolean isSelected = false;

    /**
     * Constructor, adds listeners to width and height that allow the canvas to resize as the cell does.
     */
    public MathDokuCanvas(){
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }

    /**
     * Set the cage walls to be filled in or not.
     * 
     * @param index Which wall do you want to fill. 0 is bottom. 1 is left. 2 is top. 3 is right.
     * @param toggle Should the wall be filled or not
     */
    public void setCageWall(int index, boolean toggle) {
        cageWalls[index] = toggle;
        draw();
    }

    @Override
    public boolean isResizable() {
      return true;
    }
 
    @Override
    public double prefWidth(double height) {
      return getWidth();
    }
 
    @Override
    public double prefHeight(double width) {
      return getHeight();
    }

    /**
     * Color the cell whatever you want
     * 
     * @param color Choose which javaFX color you want the cell to be.
     */
    public void highlight(Color color) {
        defaultColor = color;
        //redraw canvas with correct highlighting
        draw();
    }

    /**
     * The user has stopped selecting this cell so unhighlight, returning to the cell's previous color.
     */
    public void unhighlight() {
        isSelected = false;
        //redraw canvas with correct highlighting
        draw();
    }

    /**
     * The cell has been selected by the user so highlight it yellow.
     * <p>
     * Doesn't overwrite the error highlighting, so once the cell is unselected it returns to the previous color.
     */
    public void selectHighlight(){
        isSelected = true;
        //redraw canvas with correct highlighting
        draw();
    }

    /**
     * Redraws the canvas.
     * <p>
     * Should be called after any update to the cell so that it is displayed to the user.
     */
    private void draw() {
        double width = getWidth();
        double height = getHeight();
   
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        if (isSelected){
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(defaultColor);
        }
        
        gc.fillRect(0, 0, width, height);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeRect(0, 0, width, height);

        //bottom
        if (cageWalls[0]) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(5.0);
            gc.strokeLine(0, height, width, height);
        }
        //left
        if (cageWalls[1]) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(5.0);
            gc.strokeLine(0, 0, 0, height);
        }
        //top
        if (cageWalls[2]) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(5.0);
            gc.strokeLine(0, 0, width, 0);
        }
        //right
        if (cageWalls[3]) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(5.0);
            gc.strokeLine(width, 0, width, height);
        }
    }
}