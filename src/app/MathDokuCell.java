package app;

import java.util.Stack;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MathDokuCell extends StackPane{
    private String number = "";
    private Label mainNumber;
    private MathDokuModel mathDokuModel;
    private boolean rowConflict = false;
    private boolean columnConflict = false;
    private MathDokuCanvas mathDokuCanvas;
    private MathDokuCage cage;
    private Label targetNumber;
    private int solutionNumber;
    private Stack<String> undoStack = new Stack<String>();
    private Stack<String> redoStack = new Stack<String>();

    public void setSolutionNumber(int solutionNumber) {
        this.solutionNumber = solutionNumber;
    }

    public int getSolutionNumber() {
        return solutionNumber;
    }

    public void setTargetNumber(String target){
        targetNumber.setText(target);
    }

    public MathDokuCage getCage() {
        return this.cage;
    }

    public void setCage(MathDokuCage cage) {
        this.cage = cage;
    }

    public boolean hasConflict() {
        return rowConflict || columnConflict;
    }

    public void setRowConflict(boolean bool) {
        rowConflict = bool;
    }

    public void setColumnConflict(boolean bool) {
        columnConflict = bool;
    }

    public String getNumber(){
        return number;
    }

    public void setCageWall(int index, boolean toggle) {
        mathDokuCanvas.setCageWall(index, toggle);
        //TODO: remove this when i dont need to debug cages
        //setNumber(String.valueOf(cage.size()));
    }

    public void clearCell(){
        setNumber("");
        redoStack.clear();
        undoStack.clear();
    }

    public void undo(){
        redoStack.push(number);
        mathDokuModel.pushToRedoStack(this);
        setNumber(undoStack.pop());
        System.out.println("tried to undo");
    }

    public void redo(){
        undoStack.push(number);
        mathDokuModel.pushToUndoStack(this);
        setNumber(redoStack.pop());
        System.out.println("tried to redo");
    }

    private void setNumber(String newNumber){
        undoStack.push(number);
        //mathDokuModel.pushToUndoStack(this);
        number = newNumber;
        mainNumber.setText(newNumber);
        //redoStack.push(newNumber);
        //TODO: check if the show errors button is toggled and pass it in as a parameter
        boolean rowsColumns = mathDokuModel.check(true);
        boolean maths = mathDokuModel.checkMaths(true);
        boolean allCellsFilled = mathDokuModel.checkAllCellsFilled();
        
        if (rowsColumns && maths && allCellsFilled){
            //TODO: make an actual winning animation or alert
            System.out.println("You won!!!! Yay");
        }
        mathDokuModel.toggleUndoRedo();
    }

    public MathDokuCell(MathDokuModel mathDokuModel){
        this.mathDokuModel = mathDokuModel;
        int dimensions = mathDokuModel.getCellDimensions();

        //do resizable canvas
        //TODO: change panes so that canvas can actually resize as the window does. I think after that it should work
        mathDokuCanvas = new MathDokuCanvas(mathDokuModel);
        getChildren().add(mathDokuCanvas);
 
        // Bind canvas size to stack pane size.
        mathDokuCanvas.widthProperty().bind(MathDokuCell.this.widthProperty());
        mathDokuCanvas.heightProperty().bind(MathDokuCell.this.heightProperty());


        //setup main number label and targetNumber
        mainNumber = new Label(number);
        mainNumber.setFont(new Font("Arial", dimensions / 2));
        mainNumber.setStyle("-fx-font-weight: bold");
        mainNumber.setMaxWidth(dimensions);
        targetNumber = new Label("12+");
        targetNumber.setFont(new Font("Arial", dimensions / 6));

        //add all nodes to the stack
        getChildren().addAll(mainNumber, targetNumber);
        setAlignment(Pos.CENTER);
        setMargin(targetNumber, new Insets(0, dimensions*5/8, dimensions*3/4, 0));
        
        class GridMouseClickHandler implements EventHandler<MouseEvent> {
        
            @Override
            public void handle(MouseEvent arg0) {
                
                mathDokuModel.setCurrentStack(MathDokuCell.this);
                
                //Unhighlight previous cell
                MathDokuCell prevCell = mathDokuModel.getPrevCell();
                if (prevCell != null) {
                    prevCell.unhighlight();
                }
                
                //Highlight the currently selected node by redrawing
                mathDokuCanvas.selectHighlight();


                //save stack for unhighlighting next time
                mathDokuModel.setPrevCell(MathDokuCell.this);

                
            }
        }

        addEventHandler(MouseEvent.MOUSE_CLICKED, new GridMouseClickHandler());
    }

    public void updateNumber(String input) {

        //If delete, delete the last number in the cell
        //TODO: can I make this more elegant?
        if (input.equals("delete")){
            if (number.length() > 0){
                String[] numberArray = number.split("");
                String concat = "";
                for (int i = 0; i < numberArray.length-1; i++) {
                    concat += numberArray[i];
                }
                //mainNumber.setText(concat);
                setNumber(concat);
                return;
            }
        }
    
        try {
            //Make sure it is an integer
            Integer.parseInt(input);
            
            //Input is a number, so concatenate it with the existing number
            //prevent the user inputting a number greater than the highest possible
            String newNumber = number+input;
            int intNumber = Integer.parseInt(newNumber);
            //dont allow 0 as an option if the dimensions are less than 10
            if (intNumber != 0 || mathDokuModel.getGridDimensions() > 9){
                if (Integer.parseInt(newNumber) <= mathDokuModel.getGridDimensions()){
                    //mainNumber.setText(newNumber);
                    setNumber(newNumber);
                }
            }

        } catch (NumberFormatException e) {
            //Someone tried to type a letter what a fool
        }
    }

    //only used in mathDokuModel
    public void highlight(Color color) {
        mathDokuCanvas.highlight(color);
    }

    //used for unhighlighting previous cell
    public void unhighlight() {
        mathDokuCanvas.unhighlight();
    }
}