package app;

import javafx.scene.layout.StackPane;

public class MathDokuModel {
    private MathDokuCell prevStack;
    private MathDokuCell currentStack;
    private int dimensions;

    public MathDokuCell getCurrentStack() {
        return this.currentStack;
    }

    public void setCurrentStack(MathDokuCell currentStack) {
        this.currentStack = currentStack;
    }

    public int getDimensions() {
        return this.dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public StackPane getPrevStack() {
        return prevStack;
    }

    public void setPrevStack(MathDokuCell prevStack) {
        this.prevStack = prevStack;
    }


    //TODO: could make a custom event? Then it could be listened for by multiple filters
}