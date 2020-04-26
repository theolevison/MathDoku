package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;

/**
 * Encapsulates all the attributes that the cage needs.
 * <p>
 * Cage target is stored here, along with whether to display the target.
 * Contains all maths generation and maths checking.
 * 
 * @author Theo Levison
 */
public class MathDokuCage extends ArrayList<MathDokuCell>{
    
    private static final long serialVersionUID = 1L;
    //TODO: use trimToSize() to optimise storage

    private int targetNumber;
    private String sign;
    private boolean display = false;
    private Random rand = new Random();

    /**
     * Sets the cage's maths target.
     * 
     * @param targetNumber Target number.
     * @param sign Mathmatical operator used to get to the target number.
     */
    public void setTargetNumber(int targetNumber, String sign) {
        this.targetNumber = targetNumber;
        this.sign = sign;
    }

    /**
     * Gets the cage's maths target.
     * 
     * @return An array of the mathmatical operator used to get to the target number, followed by the target number.
     */
    public String[] getTarget(){
        return new String[]{sign, String.valueOf(targetNumber)};
    }

    /**
     * Adds a cell to the cage.
     * 
     * @param cell The cell to add.
     */
    public void addCell(MathDokuCell cell){
        add(cell);
        cell.setCage(MathDokuCage.this);
    }

    /**
     * Checks if the numbers entered in the cage meet the math target.
     * 
     * @param highlight Whether or not to highlight.
     * @return If the maths checks out.
     */
    public boolean checkMaths(boolean highlight){
        try {
            float sum;
            if (sign.equals("")){
                //single cage
                sum = Integer.parseInt(get(0).getNumber());
            } else if (sign.equals("+")){
                sum = 0;
                for (MathDokuCell mathDokuCell : this){
                    sum += Integer.parseInt(mathDokuCell.getNumber());
                }
            } else if (sign.equals("-")){
                sum = 0;
                ArrayList<Integer> list = new ArrayList<Integer>();
                //sort low to high first (for this can be any order). Does prevent negative targets.
                for (MathDokuCell mathDokuCell : this){
                    list.add(Integer.parseInt(mathDokuCell.getNumber()));
                }
                list.sort(null); //low to high.
                //then check subtraction.
                for (Integer integer : list) {
                    sum = integer-sum;
                }
            } else if (sign.equals("x")){
                sum = 1;
                for (MathDokuCell mathDokuCell : this){
                    sum = sum * Integer.parseInt(mathDokuCell.getNumber());
                }
            } else {
                sum = 1;
                List<Integer> list = new ArrayList<Integer>();
                //sort low to high first
                for (MathDokuCell mathDokuCell : this){
                    list.add(Integer.parseInt(mathDokuCell.getNumber()));
                }
                list.sort(null); //must be low to high otherwise it doesnt work.
                //then check division.
                for (Integer integer : list) {
                    sum = (float)integer/sum;
                }
                //otherwise use Collections.sort(list).

            }

            if (sum == targetNumber){
                return true;
            } else if (highlight){
                //highlight cells
                for (MathDokuCell mathDokuCell : this){
                    mathDokuCell.highlight(Color.ORANGE);
                }
                return false;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            //cell not filled in yet so dont highlight as wrong.
            return true;
        }
    }

    //TODO: make this merge with above
    public boolean checkMathsSolutions(){

        for (MathDokuCell mathDokuCell : this){
            if (mathDokuCell.getPossibleSolutionNumber() == 0){
                //not finished solving yet so return true;
                return true;
            }
        }
        
        float sum;
        if (sign.equals("")){
            //single cage
            sum = get(0).getPossibleSolutionNumber();
        } else if (sign.equals("+")){
            sum = 0;
            for (MathDokuCell mathDokuCell : this){
                sum += mathDokuCell.getPossibleSolutionNumber();
            }
        } else if (sign.equals("-")){
            sum = 0;
            ArrayList<Integer> list = new ArrayList<Integer>();
            //sort low to high first (for this can be any order). Does prevent negative targets.
            for (MathDokuCell mathDokuCell : this){
                list.add(mathDokuCell.getPossibleSolutionNumber());
            }
            list.sort(null); //low to high.
            //then check subtraction.
            for (Integer integer : list) {
                sum = integer-sum;
            }
        } else if (sign.equals("x")){
            sum = 1;
            for (MathDokuCell mathDokuCell : this){
                sum = sum * mathDokuCell.getPossibleSolutionNumber();
            }
        } else {
            sum = 1;
            List<Integer> list = new ArrayList<Integer>();
            //sort low to high first
            for (MathDokuCell mathDokuCell : this){
                list.add(mathDokuCell.getPossibleSolutionNumber());
            }
            list.sort(null); //must be low to high otherwise it doesnt work.
            //then check division.
            for (Integer integer : list) {
                sum = (float)integer/sum;
            }
            //otherwise use Collections.sort(list).

        }

        return sum == targetNumber;
    }

    /**
     * If the cage size is 1, generate a target number.
     * <p>
     * Separated from fillBigCages so that random generation is easier.
     * 
     * @param gridDimensions Dimensions of the grid, to ensure that the random number generated is a valid target.
     */
    public void fillSingleCages(int gridDimensions){
        if (size() == 1){
            int num;
            do {
                num = rand.nextInt(gridDimensions);
            } while (num == 0);
            get(0).setFinalSolutionNumber(num);
            //add number with highest priority
            get(0).getPossibleSolutionList().setAbsoluteNumber(num);
            targetNumber = num;
            sign = "";
        }
    }

    /**
     * Generates a mathmatical target from the numbers generated by the solution finder.
     * <p>
     * Ensures the target is valid by trying the calculations, if not the target is regenerated.
     * 
     * @param gridDimensions Dimensions of the grid, to ensure that the random number generated is a valid target.
     */
    public void fillBigCages(int gridDimensions){
        if (size() != 1){
            boolean division = true;
            int divisionSolution = 0;

            //check all division rules work before allowing division as an option.
            int divisionQuotient = 1;
            sign = "error";

            //check if the numbers can divide each other without leaving remainders.
            for (int i = 0; i < size(); i++) {
                for (int j = 0; j < size(); j++) {
                    int x = get(i).getFinalSolutionNumber();
                    int y = get(j).getFinalSolutionNumber();
                    //check if the remainder is zero and that the number is not the same.
                    //TODO: check all division combinations

                    try {
                        if ((x%y)%divisionQuotient == 0 && x/y != 1 && x/y != 0){
                            //pair is divisible, save to test with the next value and skip the for loop.
                            divisionQuotient = (x/y)/divisionQuotient;
                            break;
                        }
                    } catch (ArithmeticException e) {
                        System.out.println(x);
                        System.out.println(y);
                        System.out.println(divisionQuotient);
                    }                    
                }
            }

            //check that the divisonQuotient has actually changed.
            if (divisionQuotient != 1){
                division = true;
                //divison solution is divisionQuotient.
                divisionSolution = divisionQuotient;
            } else {
                division = false;
            }

            
            //now decide what sign to use.
            while (true){
                int signNum = rand.nextInt(4);
                if (signNum == 0){
                    int sum = 0;
                    for (MathDokuCell mathDokuCell : MathDokuCage.this) {
                        sum += mathDokuCell.getFinalSolutionNumber();
                    }
                    targetNumber = sum;
                    sign = "+";
                    break;
                }
                //more complicated.
                //TODO: finish this.
                else if (signNum == 1){

                    ArrayList<Integer> list = new ArrayList<Integer>();
                    //sort low to high first (for this can be any order). Does prevent negative targets.
                    for (MathDokuCell mathDokuCell : this){
                        list.add(mathDokuCell.getFinalSolutionNumber());
                    }
                    list.sort(null); //low to high.
                    //then check subtraction.
                    for (Integer integer : list) {
                        targetNumber = integer-targetNumber;
                    }
                    
                    sign = "-";
                    break;
                } else if (signNum == 2){
                    int sum = 1;
                    for (MathDokuCell mathDokuCell : MathDokuCage.this) {
                        sum = sum * mathDokuCell.getFinalSolutionNumber();
                    }
                    targetNumber = sum;
                    sign = "x";
                    break;
                } 
                //division is the only sign that sometimes cannot be used.
                else if (signNum == 3 && division){
                    targetNumber = divisionSolution;
                    sign = "÷";
                    break;
                }
            }
            
            //TODO: check that the sign can be used, if it cant then regenerate the random number and try again.
        }
    }

    /**
     * Only lets the first cell in the cage display the target.
     * 
     * @return What to display in the grid, either nothing or the target.
     */
    public String display(){
        if (display){
            return "";
        } else {
            display = true;
            return targetNumber + sign;
        }
    }
}