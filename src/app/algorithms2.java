package app;

import java.util.ArrayList;
import java.util.List;

public class algorithms2 {

    public void findX(int x) {
        List<Integer> list = new ArrayList<Integer>() ;
        for (Integer i = 0; i < 100000; i++) {

            String str = i.toString() + "00000";
            String[] array = str.split("");
            
            if ((2*Integer.parseInt(array[0]) + 3*Integer.parseInt(array[1]) + 5*Integer.parseInt(array[2]) + 7*Integer.parseInt(array[3]) + 11*Integer.parseInt(array[4]))%47 == x){
                list.add(i);
            }
        }

        System.out.println(x+": " + list.size());
        //System.out.println(x+": " + list);
    }

    public static void main(String[] args) {
        algorithms2 ting = new algorithms2();

        for (int i = 0; i < 47; i++) {
            ting.findX(i);
        }   
    }
}