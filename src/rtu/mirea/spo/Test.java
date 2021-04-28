package rtu.mirea.spo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        Integer ass = null;
        if(ass == null)
            System.out.println("ass");
        LexNode node;
        if((node = new LexNode(new Pair("ass", "we can"))) != null)
        {
            System.out.println("PINGAS");
        }
    }
}
