package app;

import java.util.ArrayList;

public class MathDokuCage{
    private String targetNumber = "12+";
    private boolean display = false;

    public String display(){
        if (display){
            return "";
        } else {
            display = true;
            return targetNumber;
        }
    }
}