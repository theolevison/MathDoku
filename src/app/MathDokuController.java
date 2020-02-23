package app;

import javafx.scene.layout.StackPane;

public class MathDokuController {
    private StackPane prevStack;
    private int dimensions;

    public int getDimensions() {
        return this.dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public StackPane getPrevStack() {
        return prevStack;
    }

    public void setPrevStack(StackPane stack) {
        prevStack = stack;
    }
}