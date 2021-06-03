package rtu.mirea.spo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RPN {
    static ArrayList<Pair<String, String>> tokens;
    static ArrayList<Pair<String, String>> stack;
    static ArrayList<Pair<String, String>> rpn;
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

    public static void main(String[] args) {
        tokens = new ArrayList<>();
        stack = new ArrayList<>();
        rpn = new ArrayList<>();
        initOperatorPriorities();
        StringBuilder sb = new StringBuilder();
        try (FileReader reader = new FileReader("output.txt")) {
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] string = sb.toString().split("\\n");
        for (int i = 0; i < string.length; i += 2) {
            if (!string[i].equals("WS"))
                tokens.add(new Pair<>(string[i], string[i + 1]));
        }
        System.out.println(tokens.toString());
        int i = 0;
        Pair<String, String> token;
        //while(i < tokens.size())
        while(i < 34)
        {
            token = tokens.get(i);
            i++;
            System.out.println("TOKEN" + token.toString());
            System.out.println("STACK" + stack.toString());
            System.out.println("RPN" + rpn.toString());
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
                System.out.println("BRACERS");
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
            System.out.println("------------------------------------------");
        }
        while(stack.size() > 0)
            rpn.add(stack.remove(stack.size() - 1));
        System.out.println(rpn.toString());
    }
}
