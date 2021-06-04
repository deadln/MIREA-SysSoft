package rtu.mirea.spo;

import java.util.ArrayList;
import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("с",2);
        String s = "с";
        System.out.println(map.containsKey(s));
    }
}
