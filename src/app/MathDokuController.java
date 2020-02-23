package app;

import javafx.scene.layout.StackPane;

public class MathDokuController {
    //TODO: wo are going to have to store last modified node anyway for undo/redo. So pass in prevNode here
    private StackPane prevStack;

    public void setPrevStack(StackPane stack) {
        prevStack = stack;
    }

    public StackPane getPrevStack() {
        return prevStack;
    }
}