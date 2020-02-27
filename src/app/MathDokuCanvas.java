package app;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MathDokuCanvas extends Canvas {
    private boolean[] cageWalls = {false, false, false, false};

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

        //bottom
        if (cageWalls[0]) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(7.0);
            gc.strokeLine(0, height, width, height);
        }
        //left
        if (cageWalls[1]) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(7.0);
            gc.strokeLine(0, 0, 0, height);
        }
        //top
        if (cageWalls[2]) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(7.0);
            gc.strokeLine(0, 0, width, 0);
        }
        //right
        if (cageWalls[3]) {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(7.0);
            gc.strokeLine(width, 0, width, height);
        }
    }
}