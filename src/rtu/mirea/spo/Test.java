package rtu.mirea.spo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Test {
    public static void main(String[] args) {
        Pair<String, String> pair1 = new Pair<>("if", "else");
        Pair<String, String> pair2 = new Pair<>("if", "else");
        HashMap<Pair<String, String>, String> mp = new HashMap<>();
        mp.put(pair1, "1");
        System.out.println(mp.containsKey(pair2));


    }

    public static String stringAddition(String a, String b){
        return Float.toString(Float.parseFloat(a) + Float.parseFloat(b));
    }
}
