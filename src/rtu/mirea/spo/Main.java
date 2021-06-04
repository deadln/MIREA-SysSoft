package rtu.mirea.spo;

public class Main {
    public static void main(String[] args) {
        String[] lexer = new String[1];
        lexer[0] = "Lexer";
        String[] parser = new String[1];
        parser[0] = "Parser";
        String[] rpn = new String[1];
        rpn[0] = "RPN";
        String[] stack_machine = new String[1];
        stack_machine[0] = "StackMachine";
        System.out.println("****************************************РАБОТА ЛЕКСЕРА****************************************");
        Lexer.main(lexer);
        System.out.println("****************************************РАБОТА ПАРСЕРА****************************************");
        Parser.main(parser);
        System.out.println("****************************************РАБОТА ПРЕОБРАЗОВАТЕЛЯ ПОЛИЗ**************************");
        RPN.main(rpn);
        System.out.println("****************************************РАБОТА СТЕК-МАШИНЫ************************************");
        StackMachine.main(stack_machine);

    }
}
