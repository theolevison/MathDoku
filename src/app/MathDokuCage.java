package app;

import java.util.ArrayList;
import java.util.Random;

public class MathDokuCage{
    private String targetNumber;
    private boolean display = false;
    private ArrayList<MathDokuCell> cells = new ArrayList<MathDokuCell>();
    Random rand = new Random();

    //TODO: decide if this should decide target numbers, or if signs should be decided after a valid layout has been found 
    //Problem with that, is that there is always a symmetrical layout that would probably be found first

    public void addCell(MathDokuCell cell) {
        cells.add(cell);
        cell.setCage(MathDokuCage.this);
    }

    public int size(){
        return cells.size();
    }

    public void fillSingleCages(int gridDimensions){
        if (cells.size() == 1){
            int num;
            do {
                num = rand.nextInt(gridDimensions);
                cells.get(0).setSolutionNumber(num);
                targetNumber = String.valueOf(num);
            } while (num == 0);
        }
    }

    public void fillBigCages(int gridDimensions){
        if (cells.size() != 1){
            boolean division = true;
            int divisionSolution = 0;

            //check all division rules work before allowing division as an option
            int divisionQuotient = 1;

            //TODO: consider checking if all numbers are odd/even, if they are a mix then division cannot occur. This already does that, can it be optimised?
            //check if the numbers can divide each other without leaving remainders
            for (int i = 0; i < cells.size(); i++) {
                for (int j = 0; j < cells.size(); j++) {
                    int x = cells.get(i).getSolutionNumber();
                    int y = cells.get(j).getSolutionNumber();
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
                int sign = rand.nextInt(4);
                if (sign == 0){
                    int sum = 0;
                    for (MathDokuCell mathDokuCell : cells) {
                        sum += mathDokuCell.getSolutionNumber();
                    }
                    targetNumber = String.valueOf(sum) + "+";
                    break;
                }
                //more complicated
                //TODO: finish this
                else if (sign == 1){
                    
                    targetNumber = "-";
                    break;
                } else if (sign == 2){
                    int sum = 1;
                    for (MathDokuCell mathDokuCell : cells) {
                        sum = sum * mathDokuCell.getSolutionNumber();
                    }
                    targetNumber = String.valueOf(sum) + "x";
                    break;
                } 
                //division is the only sign that sometimes cannot be used
                else if (sign == 3 && division){
                    targetNumber = String.valueOf(divisionSolution) + "÷";
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
            return targetNumber;
        }
    }
}