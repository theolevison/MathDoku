package app;

public class PossibleSolutionList{
    private Boolean[] numberEnabled;
    private Integer[] priority;

    public PossibleSolutionList(Integer gridDimensions){
        priority = new Integer[gridDimensions];
        numberEnabled = new Boolean[gridDimensions];
        for (int i = 0; i < gridDimensions; i++) {
            numberEnabled[i] = true;
            priority[i] = gridDimensions*gridDimensions+1;
        }
    }

    public boolean add(Integer number, Integer priorityInt) {
        if (priorityInt <= priority[number-1]){
            numberEnabled[number-1] = true;
            priority[number-1] = priorityInt;
            return true;
        } else {
            return false;
        }   
    }

    public void setAbsoluteNumber(Integer number) {
        for (int i = 0; i < numberEnabled.length; i++) {
            numberEnabled[i] = false;
        }
        numberEnabled[number-1]=true;
    }

    public boolean remove(Integer number, Integer priorityInt) {
        //if (priorityInt <= priority[number-1]){
            numberEnabled[number-1] = false;
            //priority[number-1] = priorityInt;
            return true;
        /*} else {
            return false;
        } 
        */
    }

    public Integer get() {
        for (int i = 0; i < numberEnabled.length; i++) {
            if (numberEnabled[i]){
                return i+1;
            }
        }
        throw new Error("No numbers enabled as solutions");
    }

    public boolean isEmpty(){
        for (int i = 0; i < numberEnabled.length; i++) {
            if (numberEnabled[i]){
                return false;
            }
        }
        return true;
    }
}