package rtu.mirea.spo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Test {
    public static void main(String[] args) {
        HashSet<Integer> lst = new HashSet<>();
        lst.add(1);
        lst.add(2);
        lst.add(3);
        for(var i : lst)
            System.out.println(i);

//        Pair<String, String> pair1 = new Pair<>("if", "else");
//        Pair<String, String> pair2 = new Pair<>(pair1);
//        pair1.setFirst("elif");
//        System.out.println(pair1);
//        System.out.println(pair2);

    }
}
