package rtu.mirea.spo;

import java.util.ArrayList;
import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        ArrayList<Integer> lst = new ArrayList<>();
        lst.add(1);
        lst.add(2);
        lst.add(3);
        lst.set(1, 100);
        System.out.println(lst);

//        Pair<String, String> pair1 = new Pair<>("if", "else");
//        Pair<String, String> pair2 = new Pair<>(pair1);
//        pair1.setFirst("elif");
//        System.out.println(pair1);
//        System.out.println(pair2);

    }
}
