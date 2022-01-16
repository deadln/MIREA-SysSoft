package rtu.mirea.spo;

import java.util.ArrayList;
import java.util.HashMap;

public class TriadOptimizer {

    static ArrayList<Triad> triads;
    static ArrayList<Pair<Pair<String, String>, Integer>> stack;
    static HashMap<Integer, Integer> indexes;


    public static void init()
    {
        triads = new ArrayList<>();
        stack = new ArrayList<>();
        indexes = new HashMap<>();
    }

    public static ArrayList<Pair<String, String>> optimizeTriads(ArrayList<Pair<String, String>> tokens)
    {
        //init();

        return tokens;

        /*for (int i = 0; i < tokens.size(); i++)
        {
            if(tokens.get(i).getFirst().equals("NUMBER") || tokens.get(i).getFirst().equals("VAR"))
            {
                stack.add(new Pair(tokens.get(i)));
            }

        }*/
    }
}
