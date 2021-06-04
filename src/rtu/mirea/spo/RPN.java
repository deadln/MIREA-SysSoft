package rtu.mirea.spo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RPN {
    //static ArrayList<Pair<String, String>> tokens;
    static HashMap<String, Integer> opPriority;

    static void initOperatorPriorities()
    {
        opPriority = new HashMap<>();
        opPriority.put("целый", 0);
        opPriority.put("плавающий", 0);
        opPriority.put("срока", 0);
        opPriority.put("символъ", 0);
        opPriority.put("суть", 0);
        opPriority.put("испис", 0);
        opPriority.put("замет", 0);
        opPriority.put("#", 0);
        opPriority.put("*", 1);
        opPriority.put("/", 1);
        opPriority.put("+", 2);
        opPriority.put("-", 2);
        opPriority.put("==", 3);
        opPriority.put(">", 3);
        opPriority.put(">=", 3);
        opPriority.put("<", 3);
        opPriority.put("<=", 3);
        opPriority.put("=", 4);
        opPriority.put(";", 5);
    }

    public static ArrayList<Pair<String, String>> toRPN(ArrayList<Pair<String, String>> tokens_list)
    {
        ArrayList<Pair<String, String>> stack = new ArrayList<>();
        ArrayList<Pair<String, String>> rpn = new ArrayList<>();
        Pair<String, String> token;

        int i = 0;
        while(i < tokens_list.size())
        {
            token = tokens_list.get(i);
            i++;
            if(token.getFirst().equals("VAR") || token.getFirst().equals("NUMBER"))
            {
                rpn.add(token);
                if(stack.size() > 0 && stack.get(stack.size() - 1).getFirst().equals("VAR_TYPE"))
                    rpn.add(stack.remove(stack.size() - 1));
            }
            else if(token.getFirst().equals("OP") || token.getFirst().equals("LOGICAL_OP") ||
                    token.getFirst().equals("ASSIGN_OP") || token.getFirst().equals("VAR_TYPE") )
            {
                while(stack.size() > 0 && opPriority.get(stack.get(stack.size() - 1).getSecond()) != null &&
                        (opPriority.get(stack.get(stack.size() - 1).getSecond()) < opPriority.get(token.getSecond())))
                    rpn.add(stack.remove(stack.size() - 1));
                stack.add(token);
            }
            else if(token.getFirst().equals("L_BR"))
            {
                stack.add(token);
            }
            else if(token.getFirst().equals("R_BR"))
            {
                while(stack.size() > 0 && !stack.get(stack.size() - 1).getFirst().equals("L_BR"))
                    rpn.add(stack.remove(stack.size() - 1));
                stack.remove(stack.size() - 1);
            }
            else if(token.getFirst().equals("SEP"))
            {
                while(stack.size() > 0)
                    rpn.add(stack.remove(stack.size() - 1));
                rpn.add(token);
            }
        }
        while(stack.size() > 0)
            rpn.add(stack.remove(stack.size() - 1));
        return rpn;
    }

    public static ArrayList<Pair<String, String>> getRPN(ArrayList<Pair<String, String>> tokens) {
        ArrayList<Pair<String, String>> rpn = new ArrayList<>();


        initOperatorPriorities();
        System.out.println("Original expressions");
        System.out.println(tokens.toString());
        int i = 0, j, start, end;
        Pair<String, String> token;
        while(i < tokens.size())
        {
            token = tokens.get(i);
            if(token.getFirst().equals("IF_KW"))
            {
                ArrayList<Pair<String, String>> if_body;
                // Вычленение и перевод условия if
                int condition_start = i + 1;
                int condition_end = condition_start;
                while(condition_end < tokens.size() && !tokens.get(condition_end).getFirst().equals("R_BR"))
                    condition_end++;
                rpn.addAll(toRPN(new ArrayList<>(tokens.subList(condition_start, condition_end + 1))));
                // Вычленение и перевод тела if
                int expression_start = condition_end + 2;
                int expression_end = expression_start;
                while(expression_end < tokens.size() && !tokens.get(expression_end).getFirst().equals("R_S_BR"))
                    expression_end++;
                if_body = toRPN(new ArrayList<>(tokens.subList(expression_start, expression_end)));
                // Добавление перехода за пределы тела if
                rpn.add(new Pair<String, String>("NUMBER", Integer.toString(rpn.size() + if_body.size() + 4)));
                rpn.add(new Pair<String, String>("OP", "!F"));
                if(tokens.get(expression_end + 1).getFirst().equals("ELSE_KW"))
                {
                    if(tokens.get(expression_end + 2).getFirst().equals("IF_KW"))
                    {
                        ArrayList<Pair<String, String>> if_condition_2;
                        ArrayList<Pair<String, String>> if_body_2;
                        condition_start = expression_end + 3;
                        condition_end = condition_start;
                        while(condition_end < tokens.size() && !tokens.get(condition_end).getFirst().equals("R_BR"))
                            condition_end++;
                        if_condition_2 = toRPN(new ArrayList<>(tokens.subList(condition_start, condition_end + 1)));
                        expression_start = condition_end + 2;
                        expression_end = expression_start;
                        while(expression_end < tokens.size() && !tokens.get(expression_end).getFirst().equals("R_S_BR"))
                            expression_end++;
                        if_body_2 = toRPN(new ArrayList<>(tokens.subList(expression_start, expression_end)));
                        if_body.add(new Pair<String, String>("NUMBER", Integer.toString(rpn.size() + if_body.size() + 4 + if_condition_2.size() + if_body_2.size())));
                        if_body.add(new Pair<String, String>("OP", "!!"));
                        rpn.addAll(if_body);
                        rpn.addAll(if_condition_2);
                        rpn.add(new Pair<String, String>("NUMBER", Integer.toString(rpn.size() + if_body_2.size() + 2)));
                        rpn.add(new Pair<String, String>("OP", "!F"));
                        rpn.addAll(if_body_2);
                    }
                    else
                    {
                        ArrayList<Pair<String, String>> else_body;
                        expression_start = expression_end + 3;
                        expression_end = expression_start;
                        while(expression_end < tokens.size() && !tokens.get(expression_end).getFirst().equals("R_S_BR"))
                            expression_end++;
                        else_body = toRPN(new ArrayList<>(tokens.subList(expression_start, expression_end)));
                        if_body.add(new Pair<String, String>("NUMBER", Integer.toString(rpn.size() + if_body.size() + 2 + else_body.size())));
                        if_body.add(new Pair<String, String>("OP", "!!"));
                        rpn.addAll(if_body);
                        rpn.addAll(else_body);
                    }

                }


                i = expression_end;
            }
            else if(token.getFirst().equals("WHILE_KW"))
            {
                ArrayList<Pair<String, String>> while_body;
                int while_start = rpn.size();
                // Вычленение и перевод условия while
                int condition_start = i + 1;
                int condition_end = condition_start;
                while(condition_end < tokens.size() && !tokens.get(condition_end).getFirst().equals("R_BR"))
                    condition_end++;
                rpn.addAll(toRPN(new ArrayList<>(tokens.subList(condition_start, condition_end + 1))));
                // Вычленение и перевод тела while
                int expression_start = condition_end + 2;
                int expression_end = expression_start;
                while(expression_end < tokens.size() && !tokens.get(expression_end).getFirst().equals("R_S_BR"))
                    expression_end++;
                while_body = toRPN(new ArrayList<>(tokens.subList(expression_start, expression_end)));
                while_body.add(new Pair<String, String>("NUMBER", Integer.toString(while_start)));
                while_body.add(new Pair<String, String>("OP", "!!"));
                // Добавление перехода за пределы тела if
                rpn.add(new Pair<String, String>("NUMBER", Integer.toString(rpn.size() + while_body.size() + 2)));
                rpn.add(new Pair<String, String>("OP", "!F"));
                rpn.addAll(while_body);
                i = expression_end;
            }
            else if(token.getFirst().equals("DO_KW"))
            {
                ArrayList<Pair<String, String>> while_body;
                int while_start = rpn.size();
                int expression_start = i + 2;
                int expression_end = expression_start;
                while(expression_end < tokens.size() && !tokens.get(expression_end).getFirst().equals("R_S_BR"))
                    expression_end++;
                rpn.addAll(toRPN(new ArrayList<>(tokens.subList(expression_start, expression_end))));

                int condition_start = expression_end + 1;
                int condition_end = condition_start;
                while(condition_end < tokens.size() && !tokens.get(condition_end).getFirst().equals("R_BR"))
                    condition_end++;
                rpn.addAll(toRPN(new ArrayList<>(tokens.subList(condition_start, condition_end + 1))));
                rpn.add(new Pair<String, String>("NUMBER", Integer.toString(while_start)));
                rpn.add(new Pair<String, String>("OP", "!T"));
                i = condition_end + 1;
            }
            else
            {
                j = i;
                while(j < tokens.size() && !tokens.get(j).getFirst().equals("SEP")) // Убрать фиксированное ограничение после дебага!!!
                    j++;
                rpn.addAll(toRPN(new ArrayList<>(tokens.subList(i, j + 1))));
                i = j;
            }
            i++;
        }
        System.out.println("Reverse polish notation:");
        System.out.println(rpn.toString());
        System.out.println(rpn.size());
        return rpn;
    }
}
