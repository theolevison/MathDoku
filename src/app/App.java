package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class App extends Application {

    public void start(Stage stage){
        GridPane root = new GridPane();
        HBox buttons = new HBox();
        Button button1 = new Button("Submit");
        Button button2 = new Button("Cancel");
        TextField requestField = new TextField();

        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10, 10, 10, 10));

        root.add(requestField, 0, 0, 2, 1);
        root.add(buttons, 0, 1, 2, 1);
        buttons.getChildren().addAll(button1, button2);

        Scene scene = new Scene(root,450,150);
        stage.setScene(scene);
        stage.setTitle("Simple Form");

        stage.show();
    }

    public static void main(String[] args){
        launch();
    }
}