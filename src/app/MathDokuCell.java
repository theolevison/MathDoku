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

/**
 * Handles undo/redo for itself, displaying numbers to the user and highlighting errors, encapsulates the actual highlighting and drawing.
 * <p>
 * Contains all the objects in the cell in a stackPane.
 * Controls resizing itself and encapsulates resizing those objects.
 * <p>
 * Ideally it would have no reference to mathDokuModel and be totally self contained, but it needs to be able to access the undo/redo stacks stored there, plus check for errors.

 * 
 * @author Theo Levison
 */
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

    /**
     * @param solutionNumber A number that is part of a solution to the math doku.
     */
    public void setSolutionNumber(int solutionNumber) {
        this.solutionNumber = solutionNumber;
    }

    /**
     * @return A number that is part of a solution to the math doku.
     */
    public int getSolutionNumber() {
        return solutionNumber;
    }

    /**
     * @param target A mathmatical target for the cage.
     */
    public void setTargetNumber(String target){
        targetNumber.setText(target);
    }

    /**
     * @return This cell's cage.
     */
    public MathDokuCage getCage() {
        return this.cage;
    }

    /**
     * @param cage This cage this cell belongs to.
     */
    public void setCage(MathDokuCage cage) {
        this.cage = cage;
    }

    /**
     * Checks if this cell has any conflicts with other cells in the same row or column.
     * @return If this cell has a conflict.
     */
    public boolean hasConflict() {
        return rowConflict || columnConflict;
    }

    /**
     * @param bool If this cell has conflict with another cell in the same row.
     */
    public void setRowConflict(boolean bool) {
        rowConflict = bool;
    }

    /**
     * @param bool If this cell has conflict with another cell in the same column.
     */
    public void setColumnConflict(boolean bool) {
        columnConflict = bool;
    }

    /**
     * @return The number this cell is displaying.
     */
    public String getNumber(){
        return number;
    }

    /**
     * Set the cage walls to be filled in or not, encapsulates MathDokuCanves methods.
     * 
     * @param index Which wall do you want to fill. 0 is bottom. 1 is left. 2 is top. 3 is right.
     * @param toggle Should the wall be filled or not
     */
    public void setCageWall(int index, boolean toggle) {
        mathDokuCanvas.setCageWall(index, toggle);
    }

    /**
     * Sets this cell to display nothing, and resets it's undo/redo stacks.
     */
    public void clearCell(){
        setNumber("");
        redoStack.clear();
        undoStack.clear();
    }

    /**
     * Undos the last operation on this cell.
     */
    public void undo(){
        redoStack.push(number);
        mathDokuModel.pushToRedoStack(this);
        setNumber(undoStack.pop());
        System.out.println("tried to undo");
    }

    /**
     * Redos the last undone operation on this cell.
     */
    public void redo(){
        undoStack.push(number);
        mathDokuModel.pushToUndoStack(this);
        setNumber(redoStack.pop());
        System.out.println("tried to redo");
    }

    /**
     * Displays the number the user entered in the cell.
     * <p>
     * Adds the previous number to the undo stack to allow it to be undone.
     * Checks if the new number has introduced any errors.
     * Checks if that move has solved the math doku problem.
     * Checks if the undo redo buttons should be enabled or disabled as a result of this operation.
     * 
     * @param newNumber The number the user wants to enter into this cell.
     */
    private void setNumber(String newNumber){
        undoStack.push(number);
        
        number = newNumber;
        mainNumber.setText(newNumber);
        
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

    /**
     * Constructor, adds all components the stackPane, adds event handler so that the user can select this cell.
     * <p>
     * Binds the canvas size to the stackPane size.
     * Sets fonts, text size and style.
     * 
     * @param mathDokuModel Reference to the MathDokuModel class.
     */
    public MathDokuCell(MathDokuModel mathDokuModel){
        this.mathDokuModel = mathDokuModel;
        int dimensions = mathDokuModel.getCellDimensions();

        //do resizable canvas
        //TODO: change panes so that canvas can actually resize as the window does. I think after that it should work
        mathDokuCanvas = new MathDokuCanvas();
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
        
        /**
         * Allows the user to select this cell.
         * <p>
         * Sets the currently selected cell variable, stored in mathDokuModel, to this cell.
         * Unhighlights the previously selected cell.
         * Highlights this cell.
         * Adds this cell to the previously selected stack for unhighlighting later.
         */
        class GridMouseClickHandler implements EventHandler<MouseEvent> {
        
            /**
             * Handles the user clicking on this cell.
             * 
             * @param arg0 A MouseEvent, not actually used for anything.
             */
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

    /**
     * Updates the number in this cell according to the user's input.
     * <p>
     * Ensure the number the user has entered is valid, and can be concatenated with the current contents of the cell.
     * If the user wants to delete, remove the right most value from the current contents of the cell.
     * 
     * @param input The input from the user, either a number or a request to delete.
     */
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
            //Make sure it is an integer, should always be caught before but better safe than sorry.
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

    /**
     * Encapsulates changing the color of the canvas.
     * <p>
     * Only used in mathDokuModel.
     * 
     * @param color What color the cell should be highlighted.
     */
    public void highlight(Color color) {
        mathDokuCanvas.highlight(color);
    }

    //
    /**
     * Encapsulates unhighlighting this cell.
     * <p>
     * Only used for unhighlighting previous cell.
     * 
     * @param color What color the cell should be highlighted.
     */
    public void unhighlight() {
        mathDokuCanvas.unhighlight();
    }
}