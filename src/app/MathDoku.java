package app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MathDoku extends Application {
    //use mathDukoController to store everything like prevStacks, dimensions etc
    private MathDokuModel mathDokuModel = new MathDokuModel();

    @Override
    public void start(Stage stage) {

        mathDokuModel.setDimensions(60);

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

        class TypeEventHandler implements EventHandler<KeyEvent> {

            @Override
            public void handle(KeyEvent arg0) {
                if (mathDokuModel.getCurrentStack()!=null){
                    String str = arg0.getCharacter();

                    try {
                        Integer.parseInt(str);
                        //Make sure it is an integer
                        //Input is a number, so concatenate it with the array in mathDokuModel
                        //mathDokuModel.getCurrentStack();
                        Node labelNode = mathDokuModel.getCurrentStack().getChildren().get(1);
            
                        if (labelNode instanceof Label){
                            Label mainNumber = (Label) labelNode;
                            
                            mainNumber.setText(mainNumber.getText()+str);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Someone tried to type a letter what a fool");
                    }

                    //TODO: handle events like delete
                }
            }
        } 

        root.setOnKeyTyped(new TypeEventHandler());;

        //add grid and buttons to GridPane
        root.add(gridHBox, 0, 0, 1, 1);
        root.add(buttonVBox, 2, 0, 1, 1);


        Scene scene = new Scene(root,950,700);
        stage.setScene(scene);
        stage.setTitle("MathDoku");

        stage.show();
    }

    
    private VBox[] generateSquares(int n) {

        VBox[] listOfVBoxes = new VBox[n];
        for (int i = 0; i < n; i++) {
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(0);

            for (int j = 0; j < n; j++) {
                vBox.getChildren().add(new MathDokuCell(mathDokuModel));
            }

            listOfVBoxes[i] = vBox;
        }
        return listOfVBoxes;
    }

    public static void main(String[] args) {
        launch();
    }
}