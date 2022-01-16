package rtu.mirea.spo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Чтение файла с исходным кодом
        StringBuilder sb = new StringBuilder();
        try(FileReader reader = new FileReader("input.txt"))
        {
            int c;
            while((c = reader.read()) != -1) {
                sb.append((char) c);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String source = sb.toString();
        source = source.replaceAll("\\s+"," ");
        System.out.println("****************************************РАБОТА ЛЕКСЕРА****************************************");
        ArrayList<Pair<String, String>> tokens = Lexer.getTokensList(source);
        System.out.println("**************");
        System.out.println("СПИСОК ТОКЕНОВ");
        System.out.println("**************");
        for (var token:
                tokens) {
            System.out.print(token.getFirst() + "|" + token.getSecond() + "\n");
        }
        System.out.println("***********************РАБОТА ПАРСЕРА И ПОСТРОЕНИЕ СИНТАКСИЧЕСКОГО ДЕРЕВА*********************");
        LexTree tree = Parser.getSyntaxTree(tokens);
        System.out.println("--------------------------------");
        System.out.println("ДЕРЕВО");
        System.out.println("--------------------------------");
        tree.showTree();
        System.out.println("******************************ОПТИМИЗАЦИЯ СИНТАКСИЧЕСКОГО ДЕРЕВА******************************");
        tree = TreeOptimizer.optimizeTree(tree);
        System.out.println("--------------------------------");
        System.out.println("ОПТИМИЗИРОВАННОЕ ДЕРЕВО");
        System.out.println("--------------------------------");
        tree.showTree();
        System.out.println("*********************************ПРЕОБРАЗОВАНИЕ ДЕРЕВА В ПОЛИЗ********************************");
        // ArrayList<Pair<String, String>> rpn = RPN.getRPN(tree);
        ArrayList<Pair<String, String>> rpn = RPN.getRPN(tokens);
        if(rpn == null)
        {
            System.out.println("СИНТАКСИЧЕСКАЯ ОШИБКА");
            System.exit(1);
        }
        System.out.println("ПОЛИЗ:");
        System.out.println(rpn.toString());
        System.out.println(rpn.size());
        System.out.println("******************************ОПТИМИЗАЦИЯ ПОЛИЗА С ПОМОЩЬЮ ТРИАД******************************");
        rpn = TriadOptimizer.optimizeTriads(rpn);
        System.out.println("ОПТИМИЗИРОВАННЫЙ ПОЛИЗ:");
        System.out.println(rpn.toString());
        System.out.println(rpn.size());
        System.out.println("****************************************РАБОТА СТЕК-МАШИНЫ************************************");
        StackMachine.execute(rpn);

    }
}
