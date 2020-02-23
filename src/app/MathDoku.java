package app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class MathDoku extends Application {

    @Override
    public void start(Stage stage) {
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10, 10, 10, 10));

        //undo redo buttons
        HBox undoRedoHBox = new HBox();
        Button undoButton = new Button("Undo");
        Button redoButton = new Button("Redo");
        undoButton.setMaxWidth(Double.MAX_VALUE);
        redoButton.setMaxWidth(Double.MAX_VALUE);
        undoButton.setMaxHeight(Double.MAX_VALUE);
        redoButton.setMaxHeight(Double.MAX_VALUE);
        undoRedoHBox.getChildren().addAll(undoButton, redoButton);
        undoRedoHBox.setAlignment(Pos.CENTER);
        undoRedoHBox.setSpacing(10);

        //load option buttons
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

        //clear button
        HBox clearHBox = new HBox();
        Button clearButton = new Button("Clear");
        loadFileButton.setMaxWidth(Double.MAX_VALUE);
        loadTextButton.setMaxHeight(Double.MAX_VALUE);
        clearHBox.getChildren().addAll(clearButton);
        clearHBox.setAlignment(Pos.CENTER);
        clearHBox.setSpacing(10);

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


        /*
        class CalculationHandler implements EventHandler<ActionEvent> {

            @Override
            public void handle(ActionEvent arg0) {
                Double answer = Double.parseDouble(xField.getText());
                //TODO convert individual values to values stored in an array, then maybe use case switch, either way it will improve maintainability
                if (mathsFunctions.getValue().equals("x*x")){
                    answer = answer*answer;
                } else if (mathsFunctions.getValue().equals("sin(x)")){
                    answer = Math.sin(answer);
                } else if (mathsFunctions.getValue().equals("cos(x)")){
                    answer = Math.cos(answer);
                } else if (mathsFunctions.getValue().equals("Fibonacci(x)")){
                    //TODO make fibonacci sequence
                }
                equalsField.setText(String.valueOf(answer));
            }
        }

        mathsFunctions.setOnAction(new CalculationHandler());
        */


        //10*10 box for mathduko atm
        int nDimension = 10;
        
        //button box
        HBox gridHBox = new HBox();
        gridHBox.setAlignment(Pos.CENTER);
        gridHBox.setSpacing(0);

        //generate VBoxes and fill out the HBox with them
        VBox[] vboxArray = generateSquares(nDimension);
        for (int i = 0; i < vboxArray.length; i++) {
            gridHBox.getChildren().add(vboxArray[i]);
        }

        //add grid and buttons to GridPane
        root.add(gridHBox, 0, 0, 1, 1);
        root.add(buttonVBox, 2, 0, 1, 1);


        Scene scene = new Scene(root,700,700);
        stage.setScene(scene);
        stage.setTitle("MathDoku");

        stage.show();
    }

    //TODO: I would prefer a general node class than I can add to the grid which contains the canvas node etc
    private VBox[] generateSquares(int n) {
        VBox[] listOfVBoxes = new VBox[n];
        for (int i = 0; i < n; i++) {
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(0);

            for (int j = 0; j < n; j++) {
                int dimensions = 60;
                //setup the canvas for drawing
                Canvas canvas = new Canvas(dimensions, dimensions);
                GraphicsContext gc = canvas.getGraphicsContext2D();    
                gc.strokeRect(0, 0, dimensions, dimensions);

                //setup main number label and targetNumber
                Label mainNumber = new Label("2");
                mainNumber.setFont(new Font("Arial", dimensions / 2));
                mainNumber.setStyle("-fx-font-weight: bold");
                Label targetNumber = new Label("12+");
                targetNumber.setFont(new Font("Arial", dimensions / 8));

                //add all nodes to the stack
                StackPane stack = new StackPane();
                stack.getChildren().addAll(canvas, mainNumber, targetNumber);
                stack.setAlignment(Pos.CENTER);
                StackPane.setMargin(targetNumber, new Insets(0, dimensions*5/8, dimensions*3/4, 0));
                vBox.getChildren().add(stack);
            }

            listOfVBoxes[i] = vBox;
        }
        return listOfVBoxes;
    }

    public static void main(String[] args) {
        launch();
    }
}