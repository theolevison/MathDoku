package app;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;


/**
 * Handles creating the GUI and running the game of MathDoku, extends Application.
 * <p>
 * All other classes are instansiated here.
 * 
 * @author Theo Levison
 */
public class MathDoku extends Application {
    // use mathDukoController to store everything like prevStacks, dimensions etc
    private MathDokuModel mathDokuModel = new MathDokuModel(this);
    private Button undoButton;
    private Button redoButton;
    private int gridDimensions;

    /**
     * Allows the user to press the undo button if there are operations in the undo stack.
     * 
     * @param bool If the undo button should be enabled or not
     */
    public void enableDisableUndo(boolean bool){
        undoButton.setDisable(bool);
    }

    /**
     * Allows the user to press the redo button if there are operations in the redo stack.
     * 
     * @param bool If the redo button should be enabled or not
     */
    public void enableDisableRedo(boolean bool){
        redoButton.setDisable(bool);
    }

    /**
     * Start method for javaFX that sets up the GUI and begins the game.
     * <p>
     * Instansiates the GUI with buttons and grid and contains all the method handlers for each button.
     * I've tried to keep all javaFX components only accessable through this, exceptions include MathDokuCell and MathDokuCanvas.
     * Is called by javaFX once the launch method has been called.
     * 
     * @param stage A javaFX object that represents the GUI, all javaFX objects must be added to it.
     */
    @Override
    public void start(Stage stage) {

        // TODO: let the user specify dimensions, or use default of 6
        if (false) {

        } else {
            gridDimensions = 6;
        }

        mathDokuModel.setCellDimensions(60);
        mathDokuModel.setGridDimensions(gridDimensions);

        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10, 10, 10, 10));

        // undo redo buttons
        HBox undoRedoHBox = new HBox();
        undoButton = new Button("Undo");
        redoButton = new Button("Redo");
        undoButton.setMaxWidth(Double.MAX_VALUE);
        redoButton.setMaxWidth(Double.MAX_VALUE);
        undoButton.setMaxHeight(Double.MAX_VALUE);
        redoButton.setMaxHeight(Double.MAX_VALUE);
        undoRedoHBox.getChildren().addAll(undoButton, redoButton);
        undoRedoHBox.setAlignment(Pos.CENTER);
        undoRedoHBox.setSpacing(10);

        undoButton.setOnAction(e -> mathDokuModel.undo());
        redoButton.setOnAction(e -> mathDokuModel.redo());

        undoButton.setDisable(true);
        redoButton.setDisable(true);


        // load option buttons
        HBox loadOptionsHBox = new HBox();
        Button loadFileButton = new Button("Load from file");
        Button loadTextButton = new Button("Load from text");
        loadFileButton.setMaxWidth(Double.MAX_VALUE);
        loadTextButton.setMaxWidth(Double.MAX_VALUE);
        loadFileButton.setMaxHeight(Double.MAX_VALUE);
        loadTextButton.setMaxHeight(Double.MAX_VALUE);
        loadOptionsHBox.getChildren().addAll(loadFileButton, loadTextButton);
        loadOptionsHBox.setAlignment(Pos.CENTER);
        loadOptionsHBox.setSpacing(10);

        /**
         * Lets the user select save files and if valid, loads the resulting grid to the GUI.
         * <p>
         * Ensures that the save file is in the correct format and that cages are valid.
         * 
         * @author Theo Levison
         */
        class LoadFileEventHandler implements EventHandler<ActionEvent> {

            /**
             * Handles the user pressing the load file button by generating a file load dialog.
             * <p>
             * Checks the file selected by the user for formatting and valid cages, if the file is erroneous a warning dialog is generated and the system resets.
             * If the file is valid then the save is loaded into the grid.
             * The user is able to close the dialog if they no longer want to load a save.
             * 
             * @param arg0 an ActionEvent, not actually used for anything.
             */
            @Override
            public void handle(ActionEvent arg0) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select a MathDoku file");
                File file = fileChooser.showOpenDialog(stage);
                
                if (file != null){
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        List<String> list = new ArrayList<String>();

                        String tempString;
                        String text = "";
                        while ((tempString = br.readLine()) != null){
                            list.add(tempString);
                            text += tempString + System.lineSeparator();
                        }

                        br.close();

                        //check it meets the correct formatting, otherwise create an alert
                        if (isInvalid(text)){
                            Alert alert = new Alert(Alert.AlertType.WARNING, "The file you selected is not formatted correctly");
                            alert.setTitle("Incorrect format");
                            alert.setHeaderText("");

                            //set background
                            alert.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
                            alert.getDialogPane().setId("formatWarningDialog");

                            alert.showAndWait();

                            return;
                        }

                        //send the list of cages to mathDokuModel
                        mathDokuModel.generateFromList(list);
                    } catch (Exception e) {
                        
                    }
                }
            }
        }

        /**
         * Asks the user for a text based save, if valid, loads the resulting grid to the GUI.
         * <p>
         * Generates a dialog that has a text box for the user to enter text.
         * Ensures that the text provided is in the correct format and that cages are valid.
         * The user is unable to load the save until it matches the correct format.
         * 
         * @author Theo Levison
         */
        class LoadTextEventHandler implements EventHandler<ActionEvent> {

            /**
             * Handles the user pressing the load text button by generating a text based save load dialog.
             * <p>
             * Checks the text given by the user for formatting and valid cages, if the text is erroneous the user is unable to load the save.
             * If the text is valid then the save is loaded into the grid.
             * The user is able to close the dialog if they no longer want to load a save.
             * 
             * @param arg0 An ActionEvent, not actually used for anything.
             */
            @Override
            public void handle(ActionEvent arg0) {
                Dialog<String> textDialog = new Dialog<String>();
                textDialog.setTitle("Load from text");
                textDialog.setHeaderText("");

                textDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(10, 10, 10, 10));

                TextArea textArea = new TextArea();
                Label label = new Label("Please enter text with correct formatting");

                
                grid.add(label, 0, 0);
                grid.add(textArea, 1, 0);

                textDialog.getDialogPane().setContent(grid);

                //set focus on the textArea
                Platform.runLater(() -> textArea.requestFocus());

                textDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.OK) {
                        return textArea.getText();
                    }
                    return null;
                });

                //set background
                textDialog.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
                textDialog.getDialogPane().setId("textInputDialog");
                
                Button okButton = (Button) textDialog.getDialogPane().lookupButton(ButtonType.OK);

                //disable the okay button if the format is incorrect
                BooleanBinding isInvalidBinding = Bindings.createBooleanBinding(() -> isInvalid(textArea.getText()), textArea.textProperty());
                okButton.disableProperty().bind(isInvalidBinding);

                //display dialog
                Optional<String> result = textDialog.showAndWait();

                //send the list of cages to mathDokuModel
                result.ifPresent(resultString -> 
                {
                    List<String> list = new ArrayList<String>();
                    for (String line : resultString.split("\n")) {
                        list.add(line);
                    }
                    
                    mathDokuModel.generateFromList(list);
                });
                
            }
        }

        loadFileButton.setOnAction(new LoadFileEventHandler());
        loadTextButton.setOnAction(new LoadTextEventHandler());

        // clear button
        HBox clearHBox = new HBox();
        Button clearButton = new Button("Clear");
        loadFileButton.setMaxWidth(Double.MAX_VALUE);
        loadTextButton.setMaxHeight(Double.MAX_VALUE);
        clearHBox.getChildren().addAll(clearButton);
        clearHBox.setAlignment(Pos.CENTER);
        clearHBox.setSpacing(10);

        /**
         * Checks if the user wants to clear the grid.
         * 
         * @author Theo Levison
         */
        class ClearButtonEventHandler implements EventHandler<ActionEvent> {

             /**
             * Generates a confirmation dialog that asks the user if they want to clear the grid.
             * 
             * @param arg0 An ActionEvent, not actually used for anything.
             */
            @Override
            public void handle(ActionEvent arg0) {
                // ask the user for confirmation
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to clear the board?",
                        ButtonType.OK);
                alert.setTitle("Clear board");
                alert.setHeaderText("");

                //set background
                alert.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
                alert.getDialogPane().setId("confirmationDialog");
                
                alert.showAndWait().ifPresent(btnType -> {
                    if (btnType == ButtonType.OK){
                        //clear all cells
                        mathDokuModel.clearAllCells();
                    }
                });
            }
        }

        clearButton.setOnAction(new ClearButtonEventHandler());

        //show mistakes button
        HBox showMistakesHBox = new HBox();
        Button showMistakesButton = new Button("Show mistakes");
        showMistakesButton.setMaxWidth(Double.MAX_VALUE);
        showMistakesButton.setMaxHeight(Double.MAX_VALUE);
        showMistakesHBox.getChildren().addAll(showMistakesButton);
        showMistakesHBox.setAlignment(Pos.CENTER);
        showMistakesHBox.setSpacing(10);

        //button box
        VBox buttonVBox = new VBox();
        buttonVBox.getChildren().addAll(undoRedoHBox, loadOptionsHBox, clearHBox, showMistakesHBox);
        buttonVBox.setAlignment(Pos.CENTER);
        buttonVBox.setSpacing(10);

        /**
         * Lets the user press a button to enter numbers into the grid.
         * 
         * @author Theo Levison
         */
        class NumberButtonEventHandler implements EventHandler<ActionEvent> {

            /**
             * Handles the user pressing a number button to enter numbers into the grid.
             * 
             * @param arg0 An ActionEvent, used to find the number the user wants to enter into the grid.
             */
            @Override
            public void handle(ActionEvent arg0) {
                if (mathDokuModel.hasCurrentCell()){
                    EventTarget target = arg0.getTarget();

                    //get the text from the button
                    if (target instanceof Button){
                        Button button = (Button) arg0.getTarget();
                        mathDokuModel.getCurrentCell().updateNumber(button.getText());
                        
                        //TODO: check if the show errors button is toggled and pass it in as a parameter
                        mathDokuModel.check(true);
                    }
                }
            }
        }
        
        //generate number buttons
        VBox numberButtonsVBox = new VBox();
        numberButtonsVBox.setAlignment(Pos.CENTER);
        numberButtonsVBox.setSpacing(10);
        //ensure that only number buttons appear that are valid for the gridDimensions
        for (int i = 1; i < 10 && i <= gridDimensions; i++) {
            Button button = new Button(Integer.toString(i));
            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);
            button.setOnAction(new NumberButtonEventHandler());
            numberButtonsVBox.getChildren().add(button);
        }
        if (gridDimensions > 9){
            Button button = new Button("0");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setMaxHeight(Double.MAX_VALUE);
            button.setOnAction(new NumberButtonEventHandler());
            numberButtonsVBox.getChildren().add(button);
        }

        //grid box
        HBox gridHBox = new HBox();
        gridHBox.setAlignment(Pos.CENTER);
        gridHBox.setSpacing(0);

        //generate VBoxes and fill out the HBox with them
        VBox[] vboxArray = generateSquares(gridDimensions);
        for (int i = 0; i < vboxArray.length; i++) {
            gridHBox.getChildren().add(vboxArray[i]);
        }

        //TODO: allow the user to use arow keys to switch cells (longterm)
        /**
         * Lets the user press keys to enter and remove numbers in the grid.
         * 
         * @author Theo Levison
         */
        class KeyboardInputEventHandler implements EventHandler<KeyEvent> {

            /**
             * Handles the user press a key to enter numbers into the grid.
             * 
             * @param arg0 An KeyEvent, used to find the number/command the user wants to enter into the grid.
             */
            @Override
            public void handle(KeyEvent arg0) {
                if (mathDokuModel.hasCurrentCell()){
                    //check if they want to delete or enter a number
                    if (arg0.getCode() == KeyCode.BACK_SPACE) {
                        mathDokuModel.getCurrentCell().updateNumber("delete");
                    } else {
                        try {
                            //Make sure it is an integer
                            Integer.parseInt(arg0.getText());
                            mathDokuModel.getCurrentCell().updateNumber(arg0.getText());
                        } catch (NumberFormatException e) {
                            //Someone tried to type something that isn't a number or delete, ignore it
                        }
                    }
                }
            }
        }


        root.setOnKeyPressed(new KeyboardInputEventHandler());

        //add grid and buttons to GridPane
        root.add(numberButtonsVBox, 0, 0, 1, 1);
        root.add(gridHBox, 2, 0, 1, 1);
        root.add(buttonVBox, 4, 0, 1, 1);

        //TODO: make this read a button toggle or something, make a lil menu to load from file, default or generate randomly
        if (true){
            //mathDokuModel.generateDefaultGrid();
        } else {
            mathDokuModel.generateNewGrid();
        }

        Scene scene = new Scene(root,950,700);
        //scene.getStylesheets().add("/style.css");
        //new FileInputStream("style.css");
        //String stylesheet = getClass().getResource("style.css").toExternalForm();
        //stylesheet = new FileInputStream("style.css");
        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        //scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("MathDoku");

        stage.show();
    }

    /**
     * Generates the grid of MathDokuCells.
     * <p>
     * Adds each cell to a matrix in mathDokuModel.
     * 
     * @param dimensions The gridDimensions that are used to create the right amount of cells.
     * @return An array of VBoxes full of cells
     */
    private VBox[] generateSquares(int dimensions) {

        VBox[] listOfVBoxes = new VBox[dimensions];
        for (int i = 0; i < dimensions; i++) {
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(0);

            for (int j = 0; j < dimensions; j++) {
                MathDokuCell cell = new MathDokuCell(mathDokuModel);
                vBox.getChildren().add(cell);
                mathDokuModel.addCell(cell, i, j);
            }

            listOfVBoxes[i] = vBox;
        }
        return listOfVBoxes;
    }

    /**
     * Checks if format of saves are incorrect.
     * <p>
     * If the load attempt is successful then gridDimensions are updated.
     * 
     * @param text The save that the user is attempting to load.
     * @return If the save is invalid
     */
    private boolean isInvalid(String text){
        //first get the square root of the highest number to get dimensions
        List<Integer[]> allNumbersList = new ArrayList<Integer[]>();

        try {
            Set<Integer> usedCellsSet = new HashSet<Integer>();

            //check formatting
            for (String line : text.split(System.lineSeparator())) {
                String[] array1 = line.split(" ");
                String[] targetArray = array1[0].split("");
                String[] numberArray = array1[1].split(",");

                for (int i = 0; i < targetArray.length-1; i++) {
                    try {
                        Integer.parseInt(targetArray[i]);
                    } catch (NumberFormatException e) {
                        return true;
                    }
                }

                if (!targetArray[targetArray.length-1].matches("\\+|-|x|÷")){
                    return true;
                }

                //if there is only one cell
                if (numberArray.length == 0){
                    try {
                        Integer.parseInt(array1[1]);
                        allNumbersList.add(new Integer[] {Integer.parseInt(array1[1])});
                    } catch (NumberFormatException e) {
                        return true;
                    }
                } else {
                    Integer[] intArray = new Integer[numberArray.length];
                    
                    for (int i = 0; i < numberArray.length; i++) {
                        try {
                            Integer cell = Integer.parseInt(numberArray[i]);

                            //check the cell has not been used before
                            if (!usedCellsSet.add(cell)){
                                return true;
                            }

                            //add the cells so we can check if they are adjacent
                            intArray[i] = cell;

                        } catch (NumberFormatException e) {
                            return true;
                        }
                    }
                    allNumbersList.add(intArray);
                }
            }
            
            Integer highestNumber = 0;
            
            for (Integer[] list : allNumbersList) {
                for (Integer integer : list) {
                    //update highest number
                    highestNumber = integer >= highestNumber ? integer : highestNumber;
                }
            }

            //find new grid dimensions
            //this part only works once all cells have been typed out and the highest number is a valid grid dimension
            Integer tempGridDimensions = (int)Math.floor(Math.sqrt(highestNumber));
            System.out.println(tempGridDimensions);

            //check cages are valid
            for (Integer[] list : allNumbersList) {
                //only need to test half?
                for (int i = 0; i < list.length/2; i++) {
                    Integer cell1 = list[i];
                    boolean check = true;
                    for (int j = i+1; j < list.length; j++) {
                        Integer cell2 = list[j];
                        if (cell1 == cell2-1 || cell1 == cell2-tempGridDimensions){
                        check = false;
                        }
                    }
                    if (check){
                        return true;
                    }
                }
            }

            //save will be loaded so now update gridDimensions
            gridDimensions = tempGridDimensions;
            return false;
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
        
    }

    public static void main(String[] args) {
        launch();
    }
}