package app;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MathDokuCanvas extends Canvas {
    private boolean[] cageWalls = {false, false, false, false};
    private Color defaultColor = Color.GREY;
    private boolean isSelected = false;

    public MathDokuCanvas(MathDokuModel mathDokuModel){
        //int dimensions = mathDokuModel.getCellDimensions();

        //setup the canvas for drawing
        //GraphicsContext gc = getGraphicsContext2D();

        //gc.strokeRect(0, 0, dimensions, dimensions);

        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }

    public void setCageWall(int index, boolean toggle) {
        cageWalls[index] = toggle;
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

    //update highlight color
    public void highlight(Color color) {
        defaultColor = color;
        //redraw canvas with correct highlighting
        draw();
    }

    public void unhighlight() {
        isSelected = false;
        //redraw canvas with correct highlighting
        draw();
    }

    public void selectHighlight(){
        isSelected = true;
        //redraw canvas with correct highlighting
        draw();
    }

    //everything you want to draw put in here
    private void draw() {
        double width = getWidth();
        double height = getHeight();
   
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        //Make cell boundary
        //gc.setStroke(Color.BLACK);
        //gc.setLineWidth(0.0);
        //gc.strokeRect(0, 0, width, height);

        /*
        gc.setStroke(Color.RED);
        gc.strokeLine(0, 0, width, height);
        gc.strokeLine(0, height, width, 0);
        */

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