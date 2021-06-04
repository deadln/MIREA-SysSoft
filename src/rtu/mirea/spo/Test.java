package rtu.mirea.spo;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        ArrayList<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(2);
        a.add(3);
        ArrayList<Integer> b = new ArrayList<>();
        b.add(4);
        b.add(5);
        b.add(6);
        a.addAll(b);
        System.out.println(a.toString());
    }
}
