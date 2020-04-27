package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.text.Text;

/**
 * Handles creating the GUI and running the game of MathDoku, extends
 * Application.
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

    private final int MINIMUM_MAIN_WINDOW_WIDTH = 925;
    private final int MINIMUM_MAIN_WINDOW_HEIGHT = 500;

    private final int WINDOW_WIDTH = 925;
    private final int WINDOW_HEIGHT = 700;

    /**
     * Allows the user to press the undo button if there are operations in the undo
     * stack.
     * 
     * @param bool If the undo button should be enabled or not
     */
    public void enableDisableUndo(boolean bool) {
        undoButton.setDisable(bool);
    }

    /**
     * Allows the user to press the redo button if there are operations in the redo
     * stack.
     * 
     * @param bool If the redo button should be enabled or not
     */
    public void enableDisableRedo(boolean bool) {
        redoButton.setDisable(bool);
    }

    /**
     * Start method for javaFX that sets up the GUI and gets the user to select a
     * game.
     * <p>
     * Handles loading games from saves. Is called by javaFX once the launch method
     * has been called.
     * 
     * @param stage A javaFX object that represents the GUI, all javaFX objects must
     *              be added to it.
     */
    @Override
    public void start(Stage stage) {

        // TODO: add slider to select grid dimensions when generating a random game

        // setup title page
        Text title = new Text(10, 20, "MathDoku");
        title.setId("title");

        // load option buttons
        HBox loadOptionsHBox = new HBox();
        Button loadFileButton = new Button("Load from file");
        Button loadTextButton = new Button("Load from text");
        Button autoGenerateButton = new Button("Generate new game");
        loadFileButton.setMaxWidth(Double.MAX_VALUE);
        loadTextButton.setMaxWidth(Double.MAX_VALUE);
        autoGenerateButton.setMaxWidth(Double.MAX_VALUE);
        autoGenerateButton.setMaxHeight(Double.MAX_VALUE);
        loadFileButton.setMaxHeight(Double.MAX_VALUE);
        loadTextButton.setMaxHeight(Double.MAX_VALUE);
        loadOptionsHBox.getChildren().addAll(loadFileButton, loadTextButton, autoGenerateButton);
        loadOptionsHBox.setAlignment(Pos.CENTER);
        loadOptionsHBox.setSpacing(10);

        loadOptionsHBox.setMaxHeight(100);
        loadOptionsHBox.setMaxWidth(WINDOW_WIDTH);


        // grid slider
        HBox sliderHBox = new HBox();
        Slider slider = new Slider(0, 10, 6);
        slider.setMaxHeight(Double.MAX_VALUE);
        slider.setMaxHeight(Double.MAX_VALUE);
        sliderHBox.getChildren().addAll(slider);
        sliderHBox.setAlignment(Pos.CENTER);
        sliderHBox.setSpacing(10);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1); 
        slider.setBlockIncrement(1); 
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
  

        sliderHBox.setMaxHeight(100);
        sliderHBox.setMaxWidth(WINDOW_WIDTH);

        // loadOptionsHBox.setPrefSize(window_width, window_height);
        // loadOptionsHBox.setMaxSize(Region.USE_COMPUTED_SIZE,
        // Region.USE_COMPUTED_SIZE);

        /**
         * Lets the user select save files and if valid, loads the resulting grid to the
         * GUI.
         * <p>
         * Ensures that the save file is in the correct format and that cages are valid.
         * 
         * @author Theo Levison
         */
        class LoadFileEventHandler implements EventHandler<ActionEvent> {

            /**
             * Handles the user pressing the load file button by generating a file load
             * dialog.
             * <p>
             * Checks the file selected by the user for formatting and valid cages, if the
             * file is erroneous a warning dialog is generated and the system resets. If the
             * file is valid then the save is loaded into the grid. The user is able to
             * close the dialog if they no longer want to load a save.
             * 
             * @param arg0 an ActionEvent, not actually used for anything.
             */
            @Override
            public void handle(ActionEvent arg0) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select a MathDoku file");
                File file = fileChooser.showOpenDialog(stage);

                if (file != null) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        List<String> list = new ArrayList<String>();

                        String tempString;
                        String text = "";
                        while ((tempString = br.readLine()) != null) {
                            list.add(tempString);
                            text += tempString + System.lineSeparator();
                        }

                        br.close();

                        // check it meets the correct formatting, otherwise create an alert
                        if (isInvalid(text)) {
                            Alert alert = new Alert(Alert.AlertType.WARNING,
                                    "The file you selected is not formatted correctly");
                            alert.setTitle("Incorrect format");
                            alert.setHeaderText("");

                            // set background
                            alert.getDialogPane().getStylesheets()
                                    .add(this.getClass().getResource("style.css").toExternalForm());
                            alert.getDialogPane().setId("formatWarningDialog");

                            alert.showAndWait();

                            return;
                        }

                        // update dimensions and start the main game
                        mathDokuModel.setCellDimensions(0.07);
                        mathDokuModel.setGridDimensions(gridDimensions);
                        startMainGame(stage, list);
                    } catch (Exception e) {

                    }
                }
            }
        }

        /**
         * Asks the user for a text based save, if valid, loads the resulting grid to
         * the GUI.
         * <p>
         * Generates a dialog that has a text box for the user to enter text. Ensures
         * that the text provided is in the correct format and that cages are valid. The
         * user is unable to load the save until it matches the correct format.
         * 
         * @author Theo Levison
         */
        class LoadTextEventHandler implements EventHandler<ActionEvent> {

            /**
             * Handles the user pressing the load text button by generating a text based
             * save load dialog.
             * <p>
             * Checks the text given by the user for formatting and valid cages, if the text
             * is erroneous the user is unable to load the save. If the text is valid then
             * the save is loaded into the grid. The user is able to close the dialog if
             * they no longer want to load a save.
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

                // set focus on the textArea
                Platform.runLater(() -> textArea.requestFocus());

                textDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.OK) {
                        return textArea.getText();
                    }
                    return null;
                });

                // set background
                textDialog.getDialogPane().getStylesheets()
                        .add(this.getClass().getResource("style.css").toExternalForm());
                textDialog.getDialogPane().setId("textInputDialog");

                Button okButton = (Button) textDialog.getDialogPane().lookupButton(ButtonType.OK);

                // disable the okay button if the format is incorrect
                BooleanBinding isInvalidBinding = Bindings.createBooleanBinding(() -> isInvalid(textArea.getText()),
                        textArea.textProperty());
                okButton.disableProperty().bind(isInvalidBinding);

                // display dialog
                Optional<String> result = textDialog.showAndWait();

                // send the list of cages to mathDokuModel
                result.ifPresent(resultString -> {
                    List<String> list = new ArrayList<String>();
                    for (String line : resultString.split("\n")) {
                        list.add(line);
                    }

                    // update dimensions and start the main game
                    mathDokuModel.setCellDimensions(0.07);
                    mathDokuModel.setGridDimensions(gridDimensions);
                    startMainGame(stage, list);
                });

            }
        }

        loadFileButton.setOnAction(new LoadFileEventHandler());
        loadTextButton.setOnAction(new LoadTextEventHandler());

        autoGenerateButton.setOnAction(e -> {
            //TODO: let the user choose this with a slider or something.
            gridDimensions = (int)slider.getValue();
            mathDokuModel.setCellDimensions(0.07);
            mathDokuModel.setGridDimensions(gridDimensions);
            startMainGame(stage, new ArrayList<String>());
        });

        // set title scene

        GridPane titleRoot = new GridPane();
        titleRoot.setAlignment(Pos.CENTER);
        titleRoot.setHgap(10);
        titleRoot.setVgap(10);
        titleRoot.setPadding(new Insets(10, 10, 10, 10));

        titleRoot.setPrefSize(WINDOW_WIDTH * 0.6, WINDOW_HEIGHT * 0.6);
        titleRoot.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        stage.setMinHeight(300);
        stage.setMinWidth(650);

        titleRoot.add(title, 0, 0, 1, 1);
        titleRoot.add(loadOptionsHBox, 0, 1, 1, 1);
        titleRoot.add(sliderHBox, 0, 2, 1, 1);

        Scene titleScene = new Scene(titleRoot, WINDOW_WIDTH, WINDOW_HEIGHT);

        // stage.minWidthProperty().bind(titleScene.heightProperty().multiply(1.5));
        // stage.minHeightProperty().bind(titleScene.widthProperty().divide(1.5));

        titleScene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());

        stage.setScene(titleScene);
        stage.setTitle("MathDoku");

        stage.show();
    }

    /**
     * Start method for javaFX that sets up the GUI and begins the game.
     * <p>
     * Instansiates the GUI with buttons and grid and contains all the method
     * handlers for each button. I've tried to keep all javaFX components only
     * accessable through this, exceptions include MathDokuCell and MathDokuCanvas.
     * Is called by javaFX once the launch method has been called.
     * 
     * @param stage A javaFX object that represents the GUI, all javaFX objects must
     *              be added to it.
     * @param list  The list of grid cages that should be loaded.
     */
    public void startMainGame(Stage stage, List<String> list) {
        

        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(20, 20, 20, 20));

        Scene scene = new Scene(root,WINDOW_WIDTH, WINDOW_HEIGHT);

        // button box
        VBox buttonVBox = new VBox();
        buttonVBox.setAlignment(Pos.CENTER);
        buttonVBox.setSpacing(10);

        // undo redo buttons
        HBox undoRedoHBox = new HBox();
        undoButton = new Button("Undo");
        redoButton = new Button("Redo");

        undoRedoHBox.getChildren().addAll(undoButton, redoButton);
        undoRedoHBox.setAlignment(Pos.CENTER);
        undoRedoHBox.setSpacing(10);

        undoButton.setOnAction(e -> mathDokuModel.undo());
        redoButton.setOnAction(e -> mathDokuModel.redo());

        undoButton.setDisable(true);
        redoButton.setDisable(true);

        undoButton.setMaxWidth(70);
        undoButton.setMaxHeight(70);
        undoButton.prefWidthProperty().bind(undoRedoHBox.widthProperty());
        undoButton.prefHeightProperty().bind(undoRedoHBox.widthProperty());

        redoButton.setMaxWidth(70);
        redoButton.setMaxHeight(70);
        redoButton.prefWidthProperty().bind(undoRedoHBox.widthProperty());
        redoButton.prefHeightProperty().bind(undoRedoHBox.widthProperty());

        undoRedoHBox.prefWidthProperty().bind(buttonVBox.prefWidthProperty());
        // undoRedoHBox.prefHeightProperty().bind(root.prefHeightProperty());

        /*
         * undoRedoHBox.heightProperty().addListener((observable, oldValue, newValue) ->
         * { undoButton.prefHeight((double)newValue);
         * redoButton.prefHeight((double)newValue); });
         * 
         * undoRedoHBox.widthProperty().addListener((observable, oldValue, newValue) ->
         * { undoButton.prefWidth((double)newValue);
         * redoButton.prefWidth((double)newValue); });
         */

        // undoRedoHBox.prefWidthProperty().bind(root.widthProperty());
        undoRedoHBox.setMinWidth(150);
        // undoRedoHBox.prefHeightProperty().bind(root.heightProperty());

        // clear button
        HBox clearHBox = new HBox();
        Button clearButton = new Button("Clear");
        clearHBox.getChildren().addAll(clearButton);
        clearHBox.setAlignment(Pos.CENTER);
        clearHBox.setSpacing(10);

        clearButton.setMaxWidth(70);
        clearButton.setMaxHeight(70);
        clearButton.prefWidthProperty().bind(clearHBox.widthProperty());
        clearButton.prefHeightProperty().bind(clearHBox.widthProperty());

        clearHBox.prefWidthProperty().bind(buttonVBox.prefWidthProperty());
        // clearHBox.prefHeightProperty().bind(root.prefHeightProperty());

        // clearHBox.prefWidthProperty().bind(root.widthProperty());
        // clearHBox.prefHeightProperty().bind(root.heightProperty());

        /**
         * Checks if the user wants to clear the grid.
         * 
         * @author Theo Levison
         */
        class ClearButtonEventHandler implements EventHandler<ActionEvent> {

            /**
             * Generates a confirmation dialog that asks the user if they want to clear the
             * grid.
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

                // set background
                alert.getDialogPane().getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
                alert.getDialogPane().setId("confirmationDialog");

                alert.showAndWait().ifPresent(btnType -> {
                    if (btnType == ButtonType.OK) {
                        // clear all cells
                        mathDokuModel.clearAllCells();
                    }
                });
            }
        }

        clearButton.setOnAction(new ClearButtonEventHandler());

        // show mistakes button
        HBox showMistakesHBox = new HBox();
        ToggleButton showMistakesButton = new ToggleButton("Show mistakes");
        showMistakesHBox.getChildren().addAll(showMistakesButton);
        showMistakesHBox.setAlignment(Pos.CENTER);
        showMistakesHBox.setSpacing(10);

        showMistakesButton.setMaxWidth(140);
        showMistakesButton.setMaxHeight(70);
        showMistakesButton.prefWidthProperty().bind(showMistakesHBox.widthProperty());
        showMistakesButton.prefHeightProperty().bind(showMistakesHBox.widthProperty());

        showMistakesButton.setOnAction(e -> {
            mathDokuModel.setHighlight(showMistakesButton.isSelected());
            mathDokuModel.check();
            mathDokuModel.checkMaths();
        });

        // showMistakesHBox.prefWidthProperty().bind(buttonVBox.prefWidthProperty());

        // solve button
        HBox solveHBox = new HBox();
        Button solveButton = new Button("Solve");
        solveHBox.getChildren().addAll(solveButton);
        solveHBox.setAlignment(Pos.CENTER);
        solveHBox.setSpacing(10);

        solveButton.setMaxWidth(140);
        solveButton.setMaxHeight(70);
        solveButton.prefWidthProperty().bind(solveHBox.widthProperty());
        solveButton.prefHeightProperty().bind(solveHBox.widthProperty());

        solveButton.setOnAction(e -> mathDokuModel.solve(true));

        solveHBox.prefWidthProperty().bind(buttonVBox.prefWidthProperty());

        // hint button
        HBox hintHBox = new HBox();
        Button hintButton = new Button("Hint");
        hintHBox.getChildren().addAll(hintButton);
        hintHBox.setAlignment(Pos.CENTER);
        hintHBox.setSpacing(10);

        hintButton.setMaxWidth(140);
        hintButton.setMaxHeight(70);
        hintButton.prefWidthProperty().bind(hintHBox.widthProperty());
        hintButton.prefHeightProperty().bind(hintHBox.widthProperty());

        hintButton.setOnAction(e -> mathDokuModel.hint());

        hintHBox.prefWidthProperty().bind(buttonVBox.prefWidthProperty());


        // textSize buttons
        HBox textSizeHBox = new HBox();
        Button smallTextButton = new Button("T");
        Button midTextButton = new Button("T");
        Button largeTextButton = new Button("T");
        textSizeHBox.getChildren().addAll(smallTextButton, midTextButton, largeTextButton);
        textSizeHBox.setAlignment(Pos.CENTER);
        textSizeHBox.setSpacing(10);

        smallTextButton.setMaxWidth(70);//45
        smallTextButton.setMaxHeight(70);
        smallTextButton.setStyle("-fx-font-size:15");
        smallTextButton.prefWidthProperty().bind(textSizeHBox.widthProperty());
        smallTextButton.prefHeightProperty().bind(textSizeHBox.widthProperty());

        midTextButton.setMaxWidth(70);//45
        midTextButton.setMaxHeight(70);
        midTextButton.setStyle("-fx-font-size:20");
        midTextButton.prefWidthProperty().bind(textSizeHBox.widthProperty());
        midTextButton.prefHeightProperty().bind(textSizeHBox.widthProperty());

        largeTextButton.setMaxWidth(70);//45
        largeTextButton.setMaxHeight(70);
        largeTextButton.setStyle("-fx-font-size:25");
        largeTextButton.prefWidthProperty().bind(textSizeHBox.widthProperty());
        largeTextButton.prefHeightProperty().bind(textSizeHBox.widthProperty());

        //set text size scaled to the cell size
        smallTextButton.setOnAction(e -> mathDokuModel.setCellDimensions(0.1));
        midTextButton.setOnAction(e -> mathDokuModel.setCellDimensions(0.12));
        largeTextButton.setOnAction(e -> mathDokuModel.setCellDimensions(0.15));

        textSizeHBox.prefWidthProperty().bind(buttonVBox.prefWidthProperty());

        // add button boxes to other box
        buttonVBox.getChildren().addAll(undoRedoHBox, clearHBox, showMistakesHBox, solveHBox, hintHBox, textSizeHBox);

        buttonVBox.prefWidthProperty().bind(root.prefWidthProperty());
        buttonVBox.prefHeightProperty().bind(root.prefHeightProperty());

        // buttonVBox.prefWidthProperty().bind(root.widthProperty());
        // buttonVBox.prefWidth(30);
        // buttonVBox.prefHeightProperty().bind(root.heightProperty());

        /**
         * Lets the user press a button to enter numbers into the grid.
         * 
         * @author Theo Levison
         */
        class NumberButtonEventHandler implements EventHandler<ActionEvent> {

            /**
             * Handles the user pressing a number button to enter numbers into the grid.
             * 
             * @param arg0 An ActionEvent, used to find the number the user wants to enter
             *             into the grid.
             */
            @Override
            public void handle(ActionEvent arg0) {
                if (mathDokuModel.hasCurrentCell()) {
                    EventTarget target = arg0.getTarget();

                    // get the text from the button
                    if (target instanceof Button) {
                        Button button = (Button) arg0.getTarget();
                        mathDokuModel.getCurrentCell().updateNumber(button.getText());

                        // TODO: check if the show errors button is toggled and pass it in as a
                        // parameter
                        mathDokuModel.check();
                    }
                }
            }
        }

        // generate number buttons
        VBox numberButtonsVBox = new VBox();
        numberButtonsVBox.setAlignment(Pos.CENTER);
        numberButtonsVBox.setSpacing(10);

        numberButtonsVBox.prefWidthProperty().bind(root.prefWidthProperty());
        numberButtonsVBox.prefHeightProperty().bind(root.prefHeightProperty());

        // ensure that only number buttons appear that are valid for the gridDimensions
        for (int i = 1; i < 10 && i <= gridDimensions; i++) {
            Button button = new Button(Integer.toString(i));
            button.setMaxWidth(70);
            button.setMaxHeight(70);
            button.prefWidthProperty().bind(numberButtonsVBox.widthProperty());
            button.prefHeightProperty().bind(numberButtonsVBox.widthProperty());
            button.setOnAction(new NumberButtonEventHandler());
            numberButtonsVBox.getChildren().add(button);
        }
        if (gridDimensions > 9) {
            Button button = new Button("0");
            button.setMaxWidth(70);
            button.setMaxHeight(70);
            button.prefWidthProperty().bind(numberButtonsVBox.widthProperty());
            button.prefHeightProperty().bind(numberButtonsVBox.widthProperty());
            button.setOnAction(new NumberButtonEventHandler());
            numberButtonsVBox.getChildren().add(button);
        }

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(new Insets(10, 10, 10, 10));

        // Generates the grid of MathDokuCells.
        // Adds each cell to a matrix in mathDokuModel.
        for (int i = 0; i < gridDimensions; i++) {
            for (int j = 0; j < gridDimensions; j++) {
                MathDokuCell cell = new MathDokuCell(mathDokuModel);
                
                cell.winProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue){
                        scene.getRoot().setId("victoryRoyale");
                    }
                });

                grid.add(cell, i, j, 1, 1);
                mathDokuModel.addCell(cell, i, j);

                // Bind canvas size to stack pane size.
                grid.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
                    cell.setRealWidth((double) newValue / (grid.getColumnCount() + 2));
                });

                grid.prefHeightProperty().addListener((observable, oldValue, newValue) -> {
                    cell.setRealHeight((double) newValue / (grid.getRowCount() + 2));
                });
            }
        }

        // Bind grid size to parent grid size.
        grid.prefWidthProperty().bind(root.prefWidthProperty());
        grid.prefHeightProperty().bind(root.prefHeightProperty());

        // TODO: allow the user to use arow keys to switch cells (longterm)
        /**
         * Lets the user press keys to enter and remove numbers in the grid.
         * 
         * @author Theo Levison
         */
        class KeyboardInputEventHandler implements EventHandler<KeyEvent> {

            /**
             * Handles the user press a key to enter numbers into the grid.
             * 
             * @param arg0 An KeyEvent, used to find the number/command the user wants to
             *             enter into the grid.
             */
            @Override
            public void handle(KeyEvent arg0) {
                if (mathDokuModel.hasCurrentCell()) {
                    // check if they want to delete or enter a number
                    if (arg0.getCode() == KeyCode.BACK_SPACE) {
                        mathDokuModel.getCurrentCell().updateNumber("delete");
                    } else {
                        try {
                            // Make sure it is an integer
                            Integer.parseInt(arg0.getText());
                            mathDokuModel.getCurrentCell().updateNumber(arg0.getText());
                        } catch (NumberFormatException e) {
                            // Someone tried to type something that isn't a number or delete, ignore it
                        }
                    }
                }
            }
        }

        root.setOnKeyPressed(new KeyboardInputEventHandler());

        /*
        //grid box
        HBox numberAndGridHBox = new HBox();
        numberAndGridHBox.setAlignment(Pos.CENTER);
        numberAndGridHBox.setSpacing(0);

        numberAndGridHBox.getChildren().addAll(numberButtonsVBox, gridHBox);
        */

        //add grid and buttons to GridPane
        root.add(numberButtonsVBox, 0, 0, 1, 1);
        root.add(grid, 1, 0, 1, 1);
        //root.add(numberAndGridHBox, 0, 0, 1, 1);
        root.add(buttonVBox, 2, 0, 1, 1);

        //fill grid here

        //TODO: let mathDokuModel make the decision which one to generate by passing in list
        

        if (list.isEmpty()){
            //generate random
            //mathDokuModel.generateDefault2Grid();
            //mathDokuModel.generateDefault3Grid();
            //mathDokuModel.generateDefault6Grid();
            mathDokuModel.generateNewGrid();
        } else {
            mathDokuModel.generateFromList(list);
        }

        
        
        stage.setMinHeight(MINIMUM_MAIN_WINDOW_HEIGHT);
        stage.setMinWidth(MINIMUM_MAIN_WINDOW_WIDTH);
        
        //Bind main grid to window size.
        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        //root.setGridLinesVisible(true);
        
        /*
        root.widthProperty().addListener(e -> {
            System.out.println("1 " + buttonVBox.widthProperty());
            System.out.println("2 " + buttonVBox.prefWidthProperty());
            System.out.println("3 " + numberButtonsVBox.widthProperty());
            System.out.println("4 " + numberButtonsVBox.prefWidthProperty());
        });
        */

        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        //scene.getRoot().setId("confetti");
        
        stage.setScene(scene);
        stage.setTitle("MathDoku");

        stage.show();
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
                String[] array = line.split(" ");
                String[] targetArray = array[0].split("");
                String[] numberArray = array[1].split(",");

                //allow single cages with no maths symbol.
                if (targetArray.length == 1){
                    try {
                        Integer.parseInt(targetArray[0]);
                    } catch (NumberFormatException e) {
                        return true;
                    }
                } else {  
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
                }


                //if there is only one cell
                if (numberArray.length == 1){
                    try {
                        Integer.parseInt(numberArray[0]);
                        allNumbersList.add(new Integer[] {Integer.parseInt(array[1])});
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
            Integer count = 0;
            
            for (Integer[] list : allNumbersList) {
                for (Integer integer : list) {
                    //update highest number
                    count++;
                    highestNumber = integer >= highestNumber ? integer : highestNumber;
                }
            }

            //find new grid dimensions
            //this part only works once all cells have been typed out and the highest number is a valid grid dimension
            Double tempGridDimensions = Math.sqrt(highestNumber);

            //check it is a square and that the highest number matches the dimensions
            if (Math.floor(Math.sqrt(count)) == tempGridDimensions){
                System.out.println(tempGridDimensions);
            } else {
                return true;
            }

            //check cages are valid
            for (Integer[] list : allNumbersList) {
                //only need to test half?
                //sort so that the order of cages doesnt matter
                Arrays.sort(list);
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
            gridDimensions = (int)Math.floor(tempGridDimensions);
            return false;
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
        
    }

    public static void main(String[] args) {
        launch();
    }
}