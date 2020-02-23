package app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class FunctionCalculator extends Application {

    @Override
    public void start(Stage stage) {
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10, 10, 10, 10));


        Label xLabel = new Label("X=");

        TextField xField = new TextField("0");

        VBox plusMinus = new VBox();
        Button addButton = new Button("+1");
        Button minusButton = new Button("-1");
        addButton.setMaxWidth(Double.MAX_VALUE);
        minusButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setMaxHeight(Double.MAX_VALUE);
        minusButton.setMaxHeight(Double.MAX_VALUE);
        plusMinus.getChildren().addAll(addButton, minusButton);
        plusMinus.setAlignment(Pos.CENTER);
        plusMinus.setSpacing(10);


        Label funcLabel = new Label("Function:");

        ChoiceBox<String> mathsFunctions = new ChoiceBox<String>();
        mathsFunctions.getItems().addAll("x*x","sin(x)","cos(x)","Fibonacci(x)");
        mathsFunctions.setValue("x*x");

        Label equals = new Label("=");

        TextField equalsField = new TextField();

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

        class PlusMinusHandler implements EventHandler<ActionEvent> {
            private int add;

            public PlusMinusHandler(int i) {
                add = i;
            }

            @Override
            public void handle(ActionEvent arg0) {
                int returnVal=0;
                String content = xField.getText();
                if (content != null){
                    try {
                        returnVal = Integer.parseInt(content);
                        returnVal += add;
                    } catch (NumberFormatException e) {
                        //warn the user against entering a string
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Don't enter text in the number area you fool!", ButtonType.OK);
                        alert.show();
                        System.out.println("User entered something that was not a number, they were warned against it");
                    }
                }
                xField.setText(Integer.toString(returnVal));
                new CalculationHandler().handle(arg0);
                System.out.println(addButton.getAccessibleText());
            }
        }

        mathsFunctions.setOnAction(new CalculationHandler());

        addButton.setOnAction(new PlusMinusHandler(1));
        minusButton.setOnAction(new PlusMinusHandler(-1));
        

        root.add(xLabel, 0, 0, 1, 1);
        root.add(xField, 1, 0, 2, 1);
        root.add(plusMinus, 3, 0, 1, 1);
        root.add(funcLabel, 4, 0, 1, 1);
        root.add(mathsFunctions, 5, 0, 1, 1);
        root.add(equals, 6, 0, 1, 1);
        root.add(equalsField, 7, 0, 1, 1);

        Scene scene = new Scene(root,700,150);
        stage.setScene(scene);
        stage.setTitle("My Function Calculator");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}