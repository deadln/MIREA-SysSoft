package rtu.mirea.spo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    static HashMap<String, String> tokenPatterns;
    static HashMap<String, Integer> tokenPriorities;

    static void initTokenMaps(){
        // Регулярные выражения токенов
        tokenPatterns = new HashMap<>();
        tokenPatterns.put("^([a-zA-Z_]{1}[a-zA-Z_0-9]{0,})$|^([а-яА-Я_]{1}[а-яА-Я_0-9]{0,})$", "VAR");
        tokenPatterns.put("^\\+$|^-$|^\\*$|^/$|^#$", "OP");
        tokenPatterns.put("^\\==$|^>$|^>=$|^<$|^<=$", "LOGICAL_OP");
        tokenPatterns.put("^=$", "ASSIGN_OP");
        tokenPatterns.put("^0|[1-9&&[^\s]][0-9&&[^\s]]*$", "NUMBER");
        tokenPatterns.put("^ако$", "IF_KW");
        tokenPatterns.put("^инако$", "ELSE_KW");
        tokenPatterns.put("^покаместъ$", "WHILE_KW");
        tokenPatterns.put("^делати$", "DO_KW");

        tokenPatterns.put("^\s$", "WS");
        tokenPatterns.put("^\\($", "L_BR");
        tokenPatterns.put("^\\)$", "R_BR");
        tokenPatterns.put("^\\{$", "L_S_BR");
        tokenPatterns.put("^\\}$", "R_S_BR");
        tokenPatterns.put("^;$", "SEP");
        tokenPatterns.put("^целый$|^плавающий$|^срока$|^символъ$|^суть$|^испис$|^замет$", "VAR_TYPE");
        // Приоритеты токенов
        tokenPriorities = new HashMap<>();
        tokenPriorities.put("WS", 0);
        tokenPriorities.put("VAR", 1);
        tokenPriorities.put("VAR_TYPE", 2);
        tokenPriorities.put("IF_KW", 3);
        tokenPriorities.put("ELSE_KW", 3);
        tokenPriorities.put("WHILE_KW", 3);
        tokenPriorities.put("DO_KW", 3);

    }

    public static ArrayList<Pair<String, String>> getTokensList(String string) {
        initTokenMaps();

        ArrayList<Pair<String, String>> tokens = new ArrayList<>();
        StringBuilder accum = new StringBuilder();
        ArrayList<String> prevTokens = new ArrayList<>();
        boolean flag;

        for(int i = 0; i < string.length(); i++)
        {
            accum.append(string.charAt(i));
            System.out.println("ACCUM: " + accum);
            flag = false;
            for (var regex:
                    tokenPatterns.keySet()) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(accum);
                if(matcher.matches())
                {
                    System.out.println("REGEX: " + regex);
                    if(prevTokens.size() > 0 && !flag)
                    {
                        prevTokens.clear();
                    }
                    flag = true;
                    prevTokens.add(tokenPatterns.get(regex));
                }
            }
            if(!flag)
            {
                if(prevTokens.size() == 0)
                {
                    System.out.println("ОШИБКА: токен не распознан");
                    System.exit(10);
                }
                else if (prevTokens.size() > 1)
                {
                    String minn = prevTokens.get(0);
                    for (var tok:
                         prevTokens) {
                        if(tokenPriorities.get(tok) > tokenPriorities.get(minn))
                            minn = tok;
                    }
                    System.out.println("+TOKEN1: " + accum.substring(0, accum.length() - 1));
                    tokens.add(new Pair<>(minn, accum.substring(0, accum.length() - 1)));
                }
                else
                {
                    System.out.println("+TOKEN2: " + accum.substring(0, accum.length() - 1));
                    tokens.add(new Pair<>(prevTokens.get(0), accum.substring(0, accum.length() - 1)));
                }
                accum = new StringBuilder();
                prevTokens.clear();
                i--;
            }
        }

        if(prevTokens.size() == 0)
        {
            System.out.println("ОШИБКА: токен не распознан");
            System.exit(10);
        }
        else if (prevTokens.size() > 1)
        {
            System.out.println("ОШИБКА: неоднозначный токен");
            System.exit(11);
        }
        else
        {
            System.out.println("+TOKEN: " + accum);
            tokens.add(new Pair<String, String>(prevTokens.get(0), accum.toString()));
        }
        ArrayList<Pair<String, String>> spaceless_tokens = new ArrayList<>();
        for(var tok: tokens)
        {
            if(!tok.getFirst().equals("WS"))
                spaceless_tokens.add(tok);
        }
        return spaceless_tokens;
    }
}
