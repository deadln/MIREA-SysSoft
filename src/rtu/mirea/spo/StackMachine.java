package rtu.mirea.spo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class StackMachine {
    static ArrayList<Pair<String, String>> stack;
    static HashMap<String, Integer> int_vars;
    static HashMap<String, Float> float_vars;
    static HashMap<String, Boolean> bool_vars;
    static HashMap<String, MyLinkedList<Integer>> list_vars;
    static HashMap<String, MyHashSet<Integer>> set_vars;

    public static void init()
    {
        //tokens = new ArrayList<>();
        stack = new ArrayList<>();
        int_vars = new HashMap<>();
        float_vars = new HashMap<>();
        bool_vars = new HashMap<>();
        list_vars = new HashMap<>();
        set_vars = new HashMap<>();
    }

    public static void execute(ArrayList<Pair<String, String>> tokens) {
        init();
        System.out.println(tokens);
        System.out.println(tokens.size());
        Pair<String, String> token;
        for(int i = 0; i < tokens.size(); i++)
        {
            token = tokens.get(i);
            System.out.println("Токен: " + token.toString());
            System.out.println("Переменные:");
            if(int_vars.size() > 0)
                System.out.println(int_vars.toString());
            if(float_vars.size() > 0)
                System.out.println(float_vars.toString());
            if(bool_vars.size() > 0)
                System.out.println(bool_vars.toString());
            if(list_vars.size() > 0)
                System.out.println(list_vars.toString());
            if(set_vars.size() > 0)
                System.out.println(set_vars.toString());
            if(token.getFirst().equals("VAR") || token.getFirst().equals("NUMBER") || token.getFirst().equals("BOOLEAN"))
            {
                stack.add(token);
            }
            else if(token.getFirst().equals("VAR_TYPE"))
            {
                Pair<String, String> variable = stack.remove(stack.size() - 1);
                if(token.getSecond().equals("целый"))
                {
                    int_vars.put(variable.getSecond(), 0);
                }
                else if(token.getSecond().equals("плавающий"))
                {
                    float_vars.put(variable.getSecond(), 0.0f);
                }
                else if(token.getSecond().equals("суть"))
                {
                    bool_vars.put(variable.getSecond(), false);
                }
                else if(token.getSecond().equals("испис"))
                {
                    list_vars.put(variable.getSecond(), new MyLinkedList<Integer>());
                }
                else if(token.getSecond().equals("замет"))
                {
                    set_vars.put(variable.getSecond(), new MyHashSet<Integer>());
                }
                stack.add(variable);
            }
            else if(token.getFirst().equals("OP"))
            {
                Pair<String, String> op1 = null, op2;
                op2 = stack.remove(stack.size() - 1);
                if(op2.getFirst().equals("VAR"))
                {
                    if(int_vars.containsKey(op2.getSecond()))
                        op2 = new Pair<String, String>("NUMBER", Integer.toString(int_vars.get(op2.getSecond())));
                    else if(float_vars.containsKey(op2.getSecond()))
                        op2 = new Pair<String, String>("NUMBER", Float.toString(float_vars.get(op2.getSecond())));
                    else if(bool_vars.containsKey(op2.getSecond()))
                        op2 = new Pair<String, String>("BOOLEAN", Boolean.toString(bool_vars.get(op2.getSecond())));

                }
                if(!token.getSecond().equals("!!"))
                {
                    op1 = stack.remove(stack.size() - 1);
                    if(op1.getFirst().equals("VAR"))
                    {
                        if(int_vars.containsKey(op1.getSecond()))
                            op1 = new Pair<String, String>("NUMBER", Integer.toString(int_vars.get(op1.getSecond())));
                        else if(float_vars.containsKey(op1.getSecond()))
                            op1 = new Pair<String, String>("NUMBER", Float.toString(float_vars.get(op1.getSecond())));
                        else if(bool_vars.containsKey(op1.getSecond()))
                            op1 = new Pair<String, String>("BOOLEAN", Boolean.toString(bool_vars.get(op1.getSecond())));

                    }
                }
                if(token.getSecond().equals("+"))
                {
                    if(op1 != null && list_vars.containsKey(op1.getSecond()))
                    {
                        list_vars.get(op1.getSecond()).add(Integer.parseInt(op2.getSecond()));
                        stack.add(op1);
                    }
                    else if(op1 != null && set_vars.containsKey(op1.getSecond()))
                    {
                        set_vars.get(op1.getSecond()).add(Integer.parseInt(op2.getSecond()));
                        stack.add(op1);
                    }
                    else
                        stack.add(new Pair<String, String>("NUMBER", Float.toString(Float.parseFloat(op1.getSecond()) + Float.parseFloat(op2.getSecond()))));
                }
                else if(token.getSecond().equals("-"))
                {
                    stack.add(new Pair<String, String>("NUMBER", Float.toString(Float.parseFloat(op1.getSecond()) - Float.parseFloat(op2.getSecond()))));
                }
                else if(token.getSecond().equals("*"))
                {
                    stack.add(new Pair<String, String>("NUMBER", Float.toString(Float.parseFloat(op1.getSecond()) * Float.parseFloat(op2.getSecond()))));
                }
                else if(token.getSecond().equals("/"))
                {
                    stack.add(new Pair<String, String>("NUMBER", Float.toString(Float.parseFloat(op1.getSecond()) / Float.parseFloat(op2.getSecond()))));
                }
                else if(token.getSecond().equals("#"))
                {
                    if(list_vars.containsKey(op1.getSecond()))
                    {
                        stack.add(new Pair<String, String>("NUMBER", Float.toString(list_vars.get(op1.getSecond()).get(Integer.parseInt(op2.getSecond())))));
                    }
                    else if(set_vars.containsKey(op1.getSecond()))
                    {
                        stack.add(new Pair<String, String>("BOOLEAN", Boolean.toString(set_vars.get(op1.getSecond()).contains(Integer.parseInt(op2.getSecond())))));
                    }

                }
                else if(token.getSecond().equals("!F"))
                {
                    if(op1.getSecond().equals("false"))
                        i = Integer.parseInt(op2.getSecond()) - 1;
                }
                else if(token.getSecond().equals("!T"))
                {
                    if(op1.getSecond().equals("true"))
                        i = Integer.parseInt(op2.getSecond()) - 1;
                }
                else if(token.getSecond().equals("!!"))
                {
                    i = Integer.parseInt(op2.getSecond()) - 1;
                }
            }
            else if(token.getFirst().equals("ASSIGN_OP"))
            {
                Pair<String, String> op1, op2;
                op2 = stack.remove(stack.size() - 1);
                op1 = stack.remove(stack.size() - 1);
                if(int_vars.containsKey(op1.getSecond()))
                {
                    int_vars.put(op1.getSecond(), Integer.parseInt(Integer.toString((int)Float.parseFloat(op2.getSecond()))));
                }
                else if(float_vars.containsKey(op1.getSecond()))
                {
                    float_vars.put(op1.getSecond(), Float.parseFloat(op2.getSecond()));
                }
                else if(bool_vars.containsKey(op1.getSecond()))
                {
                    bool_vars.put(op1.getSecond(), Boolean.parseBoolean(op2.getSecond()));
                }
            }
            else if(token.getFirst().equals("LOGICAL_OP"))
            {
                Pair<String, String> op1 = null, op2;
                op2 = stack.remove(stack.size() - 1);
                if(op2.getFirst().equals("VAR"))
                {
                    if(int_vars.containsKey(op2.getSecond()))
                        op2 = new Pair<String, String>("NUMBER", Integer.toString(int_vars.get(op2.getSecond())));
                    else if(float_vars.containsKey(op2.getSecond()))
                        op2 = new Pair<String, String>("NUMBER", Float.toString(float_vars.get(op2.getSecond())));
                    else if(bool_vars.containsKey(op2.getSecond()))
                        op2 = new Pair<String, String>("BOOLEAN", Boolean.toString(bool_vars.get(op2.getSecond())));

                }
                op1 = stack.remove(stack.size() - 1);
                if(op1.getFirst().equals("VAR"))
                {
                    if(int_vars.containsKey(op1.getSecond()))
                        op1 = new Pair<String, String>("NUMBER", Integer.toString(int_vars.get(op1.getSecond())));
                    else if(float_vars.containsKey(op1.getSecond()))
                        op1 = new Pair<String, String>("NUMBER", Float.toString(float_vars.get(op1.getSecond())));
                    else if(bool_vars.containsKey(op1.getSecond()))
                        op1 = new Pair<String, String>("BOOLEAN", Boolean.toString(bool_vars.get(op1.getSecond())));

                }
                if(token.getSecond().equals("=="))
                {
                    stack.add(new Pair<String, String>("BOOLEAN", Boolean.toString(Float.parseFloat(op1.getSecond()) == Float.parseFloat(op2.getSecond()))));
                }
                else if(token.getSecond().equals(">"))
                {
                    stack.add(new Pair<String, String>("BOOLEAN", Boolean.toString(Float.parseFloat(op1.getSecond()) > Float.parseFloat(op2.getSecond()))));
                }
                else if(token.getSecond().equals(">="))
                {
                    stack.add(new Pair<String, String>("BOOLEAN", Boolean.toString(Float.parseFloat(op1.getSecond()) >= Float.parseFloat(op2.getSecond()))));
                }
                else if(token.getSecond().equals("<"))
                {
                    stack.add(new Pair<String, String>("BOOLEAN", Boolean.toString(Float.parseFloat(op1.getSecond()) < Float.parseFloat(op2.getSecond()))));
                }
                else if(token.getSecond().equals("<="))
                {
                    stack.add(new Pair<String, String>("BOOLEAN", Boolean.toString(Float.parseFloat(op1.getSecond()) <= Float.parseFloat(op2.getSecond()))));
                }
            }
            else if(token.getFirst().equals("SEP"))
            {
                stack.clear();
            }
        }
        System.out.println("Переменные:");
        if(int_vars.size() > 0)
            System.out.println(int_vars.toString());
        if(float_vars.size() > 0)
            System.out.println(float_vars.toString());
        if(bool_vars.size() > 0)
            System.out.println(bool_vars.toString());
        if(list_vars.size() > 0)
            System.out.println(list_vars.toString());
        if(set_vars.size() > 0)
            System.out.println(set_vars.toString());

    }

}
