package app;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;

public class MathDokuCage extends ArrayList<MathDokuCell>{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //TODO: use trimToSize() to optimise storage

    private int targetNumber;
    private String sign;
    private boolean display = false;
    Random rand = new Random();

    public void setTargetNumber(int targetNumber, String sign) {
        this.targetNumber = targetNumber;
        this.sign = sign;
    }

    public void addCell(MathDokuCell cell) {
        add(cell);
        cell.setCage(MathDokuCage.this);
    }

    //TODO: make it unhiglight, probably by having a totally different square to highlight, no that they are totally separate systems
    public boolean checkMaths(){
        try {
            int sum;
            if (sign.equals("+")){
                sum = 0;
                for (MathDokuCell mathDokuCell : MathDokuCage.this){
                    sum += Integer.parseInt(mathDokuCell.getNumber());
                }
            } else if (sign.equals("-")){
                sum = 0;
                for (MathDokuCell mathDokuCell : MathDokuCage.this){
                    //TODO: complete subtraction checking
                    Integer.parseInt(mathDokuCell.getNumber());
                }
            } else if (sign.equals("x")){
                sum = 1;
                for (MathDokuCell mathDokuCell : MathDokuCage.this){
                    sum = sum * Integer.parseInt(mathDokuCell.getNumber());
                }
            } else {
                sum = 1;
                for (MathDokuCell mathDokuCell : MathDokuCage.this){
                    sum = Integer.parseInt(mathDokuCell.getNumber());
                }
            }

            if (sum == targetNumber){
                return true;
            } else {
                //highlight cells
                for (MathDokuCell mathDokuCell : MathDokuCage.this){
                    mathDokuCell.highlight(Color.ORANGE);
                }
                return false;
            }
        } catch (NumberFormatException e) {
            //cell not filled in yet so dont highlight as wrong
            return true;
        }
    }

    public void fillSingleCages(int gridDimensions){
        if (size() == 1){
            int num;
            do {
                num = rand.nextInt(gridDimensions);
                get(0).setSolutionNumber(num);
                targetNumber = num;
            } while (num == 0);
        }
    }

    public void fillBigCages(int gridDimensions){
        if (size() != 1){
            boolean division = true;
            int divisionSolution = 0;

            //check all division rules work before allowing division as an option
            int divisionQuotient = 1;

            //TODO: consider checking if all numbers are odd/even, if they are a mix then division cannot occur. This already does that, can it be optimised?
            //check if the numbers can divide each other without leaving remainders
            for (int i = 0; i < size(); i++) {
                for (int j = 0; j < size(); j++) {
                    int x = get(i).getSolutionNumber();
                    int y = get(j).getSolutionNumber();
                    //check if the remainder is zero and that the number is not the same
                    //TODO: make sure this logic is correct
                    if ((x%y)%divisionQuotient == 0 && x/y != 1){
                        //pair is divisible, save to test with the next value and skip the for loop
                        divisionQuotient = (x/y)/divisionQuotient;
                        break;
                    }
                }
            }
            //check that the divisonQuotient has actually changed
            if (divisionQuotient != 1){
                division = true;
                //divison solution is divisionQuotient
                divisionSolution = divisionQuotient;
            } else {
                division = false;
            }

            
            //now decide what sign to use
            while (true){
                int signNum = rand.nextInt(4);
                if (signNum == 0){
                    int sum = 0;
                    for (MathDokuCell mathDokuCell : MathDokuCage.this) {
                        sum += mathDokuCell.getSolutionNumber();
                    }
                    targetNumber = sum;
                    sign = "+";
                    break;
                }
                //more complicated
                //TODO: finish this
                else if (signNum == 1){
                    
                    targetNumber = 0;
                    sign = "-";
                    break;
                } else if (signNum == 2){
                    int sum = 1;
                    for (MathDokuCell mathDokuCell : MathDokuCage.this) {
                        sum = sum * mathDokuCell.getSolutionNumber();
                    }
                    targetNumber = sum;
                    sign = "x";
                    break;
                } 
                //division is the only sign that sometimes cannot be used
                else if (signNum == 3 && division){
                    targetNumber = divisionSolution;
                    sign = "÷";
                    break;
                }
            }
            
            //TODO: check that the sign can be used, if it cant then regenerate the random number and try again
        }
    }

    public String display(){
        if (display){
            return "";
        } else {
            display = true;
            return targetNumber + sign;
        }
    }
}