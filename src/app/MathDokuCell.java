package app;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class MathDokuCell extends StackPane{

    public MathDokuCell(MathDokuController mathDokuController){
        int dimensions = mathDokuController.getDimensions();

        //setup the canvas for drawing
        Canvas canvas = new Canvas(dimensions, dimensions);
        GraphicsContext gc = canvas.getGraphicsContext2D();    
        Rectangle rect = new Rectangle(0,0,dimensions,dimensions);
        //rect.setStroke(Color.BLACK);
        rect.setFill(Color.TRANSPARENT);
        gc.strokeRect(0, 0, dimensions, dimensions);

        //setup main number label and targetNumber
        Label mainNumber = new Label("2");
        mainNumber.setFont(new Font("Arial", dimensions / 2));
        mainNumber.setStyle("-fx-font-weight: bold");
        Label targetNumber = new Label("12+");
        targetNumber.setFont(new Font("Arial", dimensions / 8));

        //add all nodes to the stack
        getChildren().addAll(canvas, mainNumber, targetNumber, rect);
        setAlignment(Pos.CENTER);
        setMargin(targetNumber, new Insets(0, dimensions*5/8, dimensions*3/4, 0));
        

        class GridMouseClickHandler implements EventHandler<MouseEvent> {

            public GridMouseClickHandler() {
            }
        
            @Override
            public void handle(MouseEvent arg0) {
                //TODO: now start listening for keypresses and react to them
                //hand existing class target node to change or some other way
                //have a think about it
        
                Node labelNode = getChildren().get(1);
                if (labelNode instanceof Label){
                    Label mainNumber = (Label) labelNode;
                    mainNumber.setText("4");
                }
        
                //Unhighlight previous cell
                try {
                    Node prevNode = mathDokuController.getPrevStack().getChildren().get(3);
                    if (prevNode instanceof Rectangle){
                        Rectangle rect = (Rectangle) prevNode;
                        rect.setStroke(Color.TRANSPARENT);
                    }
                } catch (NullPointerException e) {
                    System.out.println("No previous moves so cant unhighlight cell");
                }
        
        
                //TODO: decide if I want to use Canvas or Rectangle. Canvas is more flexible, but harder to change colour etc
        
                //Highlight the currently selected node by redrawing
                Node canvasNode = getChildren().get(0);
                if (canvasNode instanceof Canvas){
                    Canvas canvas = (Canvas) canvasNode;
                    canvas.getGraphicsContext2D().setStroke(Color.YELLOW);
                }
        
                //Highlight the currently selected node by changinc colour of rect
                Node rectNode = getChildren().get(3);
                if (rectNode instanceof Rectangle){
                    Rectangle rect = (Rectangle) rectNode;
                    rect.setStroke(Color.YELLOW);
                    //save stack for unhighlighting next time
                    mathDokuController.setPrevStack(MathDokuCell.this);
                }
            }
        }

        addEventHandler(MouseEvent.MOUSE_CLICKED, new GridMouseClickHandler());
    
    }
}