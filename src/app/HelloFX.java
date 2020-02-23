package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10, 10, 10, 10));

        Label xLabel = new Label("X=");

        TextField xField = new TextField();

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

        VBox mathsFunctions = new VBox();
        RadioButton xSquared = new RadioButton("x*x");
        RadioButton sin = new RadioButton("sin(x)");
        RadioButton cos = new RadioButton("cos(x)");
        RadioButton fib = new RadioButton("Fibonacci(x)");
        ToggleGroup toggle = new ToggleGroup();
        xSquared.setToggleGroup(toggle);
        sin.setToggleGroup(toggle);
        cos.setToggleGroup(toggle);
        fib.setToggleGroup(toggle);
        mathsFunctions.getChildren().addAll(xSquared, sin,cos,fib);
        mathsFunctions.setAlignment(Pos.CENTER_LEFT);
        mathsFunctions.setSpacing(10);

        Label equals = new Label("=");

        TextField equalsField = new TextField();

        root.add(xLabel, 0, 0, 1, 1);
        root.add(xField, 1, 0, 2, 1);
        root.add(plusMinus, 3, 0, 1, 1);
        root.add(funcLabel, 4, 0, 1, 1);
        root.add(mathsFunctions, 5, 0, 1, 1);
        root.add(equals, 6, 0, 1, 1);
        root.add(equalsField, 7, 0, 1, 1);

        Scene scene = new Scene(root,550,150);
        stage.setScene(scene);
        stage.setTitle("My Function Calculator");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
