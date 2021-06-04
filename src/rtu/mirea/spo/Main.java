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
        for (var token:
                tokens) {
            System.out.print(token.getFirst() + "|" + token.getSecond() + "\n");
            //writer.write(token.getFirst() + "\n" + token.getSecond() + "\n");
        }
        System.out.println("****************************************РАБОТА ПАРСЕРА****************************************");
        Parser.main(parser);
        System.out.println("****************************************РАБОТА ПРЕОБРАЗОВАТЕЛЯ ПОЛИЗ**************************");
        RPN.main(rpn);
        System.out.println("****************************************РАБОТА СТЕК-МАШИНЫ************************************");
        StackMachine.main(stack_machine);*/

    }
}
