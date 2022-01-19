package rtu.mirea.spo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Важная переменная: используется в выражении условия или выводится через print.
 * Идея для оптимизации: на этапе построения дерева определять "важность" переменных, а затем передавать в оптимизатор
 * высшего уровня важности, после чего помечать важными те переменные, которые были использованы для вычисления важных
 * переменных
 *
 * Заметки
 * На одну триаду могут ссылаться несколько триад
 *
 * Цели оптимизаций триад
 * В t11 и t12 предвычислить константу и заменить на наё ссылку в t13
 * t19: предвычислить константу и заменить на неё ссылку в t20
 * Удалить переменую азъ: удалить триады t7 t8 t9 t46 t47 t48
 * Удалить переменную веди: удалить триады t13 t14 t15 t49 t50 t51
 */

public class TriadOptimizer {

    static ArrayList<Triad> triads;
    static ArrayList<Pair<Integer, Pair<String, String>>> stack;
    static HashMap<Integer, Integer> triad_belongings; // Номер элемента в ПОЛИЗ : номер триады, к которой элемент принадлежит
    static HashMap<Integer, HashSet<Integer>> delayed_reference; // Номер элемента в ПОЛИЗ: номер триады, в которой нужно обновить ссылку


    public static void init() {
        triads = new ArrayList<>();
        stack = new ArrayList<>();
        triad_belongings = new HashMap<>();
        delayed_reference = new HashMap<>();
    }

    public static ArrayList<Pair<String, String>> optimizeTriads(ArrayList<Pair<String, String>> tokens) {
        init();
        ArrayList<Pair<String, String>> tokens_copy = new ArrayList<>(tokens);
        generateTriads(tokens);
        optimizeConstants();

        return tokens_copy;
    }

    public static void generateTriads(ArrayList<Pair<String, String>> tokens)
    {
        for(int i = 0; i < tokens.size(); i++){
            if(tokens.get(i).getSecond().equals(("!!")) || tokens.get(i).getSecond().equals(("!F")) ||
                    tokens.get(i).getSecond().equals(("!T"))) {
                Pair<String, String> new_ref = new Pair<>(tokens.get(i-1));
                // Смещение ссылки номера в ПОЛИЗ на первый исполняемый оператор
                while(!tokens.get(Integer.parseInt(new_ref.getSecond())).getFirst().equals("OP") &&
                        !tokens.get(Integer.parseInt(new_ref.getSecond())).getFirst().equals("VAR_TYPE") &&
                        !tokens.get(Integer.parseInt(new_ref.getSecond())).getFirst().equals("PRINT_KW") &&
                        !tokens.get(Integer.parseInt(new_ref.getSecond())).getFirst().equals("ASSIGN_OP") &&
                        !tokens.get(Integer.parseInt(new_ref.getSecond())).getFirst().equals("LOGICAL_OP")){
                    new_ref.setSecond(Integer.toString(Integer.parseInt(new_ref.getSecond()) + 1));
                }
                tokens.set(i-1, new_ref);
            }
        }

        for (int i = 0; i < tokens.size(); i++) {
            if(delayed_reference.size() > 0){
                System.out.println(delayed_reference);
            }
            // Элементы операций: значения и переменные
            if (tokens.get(i).getFirst().equals("NUMBER") || tokens.get(i).getFirst().equals("VAR")) {
                stack.add(new Pair(i, new Pair(tokens.get(i))));
            } else {
                // Унарные операторы
                // ;
                if (tokens.get(i).getFirst().equals("SEP")) {
                    triads.add(new Triad(new Pair<>(tokens.get(i)), null, null));
                    triad_belongings.put(i, triads.size() - 1);
                }
                // типы данных, безусловный переход, вывод данных
                else if (tokens.get(i).getFirst().equals("VAR_TYPE") || tokens.get(i).getSecond().equals("!!") ||
                        tokens.get(i).getFirst().equals("PRINT_KW ")) {
                    Pair<Integer, Pair<String, String>> stack_elem = stack.remove(stack.size() - 1);
                    Pair<String, String> op1 = new Pair<>(stack_elem.getSecond());
                    // Переопределение ссылки перехода на триаду для безусловного перехода
                    if (tokens.get(i).getSecond().equals("!!")) {
                        // Если триада уже существует
                        if (triad_belongings.containsKey(Integer.parseInt(op1.getSecond()))) {
                            op1 = new Pair("TRIAD", triad_belongings.get(Integer.parseInt(op1.getSecond())).toString());
                        }
                        // Иначе отложить назначение ссылки
                        else {
                            if(!delayed_reference.containsKey(Integer.parseInt(op1.getSecond()))){
                                delayed_reference.put(Integer.parseInt(op1.getSecond()), new HashSet<>());
                            }
                            delayed_reference.get(Integer.parseInt(op1.getSecond())).add(triads.size());
                        }
                    }
                    // Если на элемент существует отложенная ссылка
                    if (delayed_reference.containsKey(stack_elem.getFirst())) {
                        for(var triad_num : delayed_reference.get(stack_elem.getFirst())) {
                            Triad updated_triad = triads.get(triad_num);
                            // Замена ссылки для безусловного перехода
                            if (updated_triad.getOp().getSecond().equals("!!")) {
                                triads.set(triad_num, new Triad(updated_triad.getOp(),
                                        new Pair("TRIAD", Integer.toString(triads.size())), null));
                            }
                            // Замена ссылки для условных переходов
                            else if (updated_triad.getOp().getSecond().equals("!F") || updated_triad.getOp().getSecond().equals("!T")) {
                                triads.set(triad_num, new Triad(updated_triad.getOp(),
                                        updated_triad.getA(), new Pair("TRIAD", Integer.toString(triads.size()))));
                            }
                        }
                    }
                    // Если на оператор существует отложенная ссылка
                    if (delayed_reference.containsKey(i)) {
                        for(var triad_num : delayed_reference.get(i)) {
                            Triad updated_triad = triads.get(triad_num);
                            // Замена ссылки для безусловного перехода
                            if (updated_triad.getOp().getSecond().equals("!!")) {
                                triads.set(triad_num, new Triad(updated_triad.getOp(),
                                        new Pair("TRIAD", Integer.toString(triads.size())), null));
                            }
                            // Замена ссылки для условных переходов
                            else if (updated_triad.getOp().getSecond().equals("!F") || updated_triad.getOp().getSecond().equals("!T")) {
                                triads.set(triad_num, new Triad(updated_triad.getOp(),
                                        updated_triad.getA(), new Pair("TRIAD", Integer.toString(triads.size()))));
                            }
                        }
                    }
                    // Создание новой триады и назначение операнду принадлежность к этой триаде
                    triads.add(new Triad(new Pair<>(tokens.get(i)), op1, null));
                    triad_belongings.put(stack_elem.getFirst(), triads.size() - 1);
                    triad_belongings.put(i, triads.size() - 1);
                }
                // Бинарные операторы
                else {
                    Pair<Integer, Pair<String, String>> stack_elem2 = stack.remove(stack.size() - 1);
                    Pair<String, String> op2 = new Pair<>(stack_elem2.getSecond());

                    Pair<Integer, Pair<String, String>> stack_elem1 = stack.remove(stack.size() - 1);
                    Pair<String, String> op1 = new Pair<>(stack_elem1.getSecond());

                    // Переопределение ссылки перехода на триаду для условного перехода
                    if (tokens.get(i).getSecond().equals("!F") || tokens.get(i).getSecond().equals("!T")) {
                        // Если триада уже существует
                        if (triad_belongings.containsKey(Integer.parseInt(op2.getSecond()))) {
                            op2 = new Pair("TRIAD", triad_belongings.get(Integer.parseInt(op2.getSecond())).toString());
                        } else {
                            // Иначе отложить назначение ссылки
                            if(!delayed_reference.containsKey(Integer.parseInt(op2.getSecond()))){
                                delayed_reference.put(Integer.parseInt(op2.getSecond()), new HashSet<>());
                            }
                            delayed_reference.get(Integer.parseInt(op2.getSecond())).add(triads.size());
                        }

                    }

                    // Если на элемент существует отложенная ссылка
                    if (delayed_reference.containsKey(stack_elem1.getFirst())) {
                        for(var triad_num : delayed_reference.get(stack_elem1.getFirst())) {
                            Triad updated_triad = triads.get(triad_num);
                            // Замена ссылки для безусловного перехода
                            if (updated_triad.getOp().getSecond().equals("!!")) {
                                triads.set(triad_num, new Triad(updated_triad.getOp(),
                                        new Pair("TRIAD", Integer.toString(triads.size())), null));
                            }
                            // Замена ссылки для условных переходов
                            else if (updated_triad.getOp().getSecond().equals("!F") || updated_triad.getOp().getSecond().equals("!T")){
                                triads.set(triad_num, new Triad(updated_triad.getOp(),
                                        updated_triad.getA(), new Pair("TRIAD", Integer.toString(triads.size()))));
                            }
                        }
                    }

                    // Если на оператор существует отложенная ссылка
                    if (delayed_reference.containsKey(i)) {
                        for(var triad_num : delayed_reference.get(i)) {
                            Triad updated_triad = triads.get(triad_num);
                            // Замена ссылки для безусловного перехода
                            if (updated_triad.getOp().getSecond().equals("!!")) {
                                triads.set(triad_num, new Triad(updated_triad.getOp(),
                                        new Pair("TRIAD", Integer.toString(triads.size())), null));
                            }
                            // Замена ссылки для условных переходов
                            else if (updated_triad.getOp().getSecond().equals("!F") || updated_triad.getOp().getSecond().equals("!T")) {
                                triads.set(triad_num, new Triad(updated_triad.getOp(),
                                        updated_triad.getA(), new Pair("TRIAD", Integer.toString(triads.size()))));
                            }
                        }
                    }

                    triads.add(new Triad(new Pair<>(tokens.get(i)), op1, op2));
                    triad_belongings.put(stack_elem2.getFirst(), triads.size() - 1);
                    triad_belongings.put(stack_elem1.getFirst(), triads.size() - 1);
                    triad_belongings.put(i, triads.size() - 1);
                }
                stack.add(new Pair(null, new Pair("TRIAD", Integer.toString(triads.size() - 1))));
            }

        }
        for (int i = 0; i < triads.size(); i++) {
            System.out.println("t" + i + ": " + triads.get(i));
        }
    }

    public static void optimizeConstants(){
        for(int i = 0; i < triads.size(); i++){
            // Если обнаружено вычисление константы
            if(triads.get(i).getOp().getFirst().equals("OP") && triads.get(i).getA().getFirst().equals("NUMBER") &&
                    triads.get(i).getB().getFirst().equals("NUMBER")){
                // Вычисляем значение константы
                String a = triads.get(i).getA().getSecond();
                String b = triads.get(i).getB().getSecond();
                String op = triads.get(i).getOp().getSecond();
                String res = "0";
                if(op.equals("+"))
                    res = stringAddition(a, b);
                else if(op.equals("-"))
                    res = stringSubmission(a, b);
                else if(op.equals("*"))
                    res = stringMultiplication(a, b);
                else if(op.equals("/"))
                    res = stringDivision(a, b);

                Pair<String, String> constant = new Pair("NUMBER", res);
                // Заменяем триаду с вычислением константы на саму константу в других триадах
                for(int j = 0; j < triads.size(); j++){
                    if(triads.get(j).getOp().getFirst().equals("OP") || triads.get(j).getOp().getFirst().equals("ASSIGN_OP")){
                        if(triads.get(j).getA() != null && triads.get(j).getA().getFirst().equals("TRIAD") && triads.get(j).getA().getSecond().equals(Integer.toString(i))){
//                            triads.get(j).setA(constant);
                            triads.set(j, new Triad(triads.get(j).getOp(), constant, triads.get(j).getB()));
                        }
                        if(triads.get(j).getB() != null && triads.get(j).getB().getFirst().equals("TRIAD") && triads.get(j).getB().getSecond().equals(Integer.toString(i))){
//                            triads.get(j).setB(constant);
                            triads.set(j, new Triad(triads.get(j).getOp(), triads.get(j).getA(), constant));
                        }
                    }
                }
                // Удаляем триаду с вычислением константы
                triads.remove(i);
                // Изменяем ссылки во всех триадах
                fixReferences(i);
                i--;
            }
        }
        for (int i = 0; i < triads.size(); i++) {
            System.out.println("t" + i + ": " + triads.get(i));
        }
    }

    public static String stringAddition(String a, String b){
        return Float.toString(Float.parseFloat(a) + Float.parseFloat(b));
    }

    public static String stringSubmission(String a, String b){
        return Float.toString(Float.parseFloat(a) - Float.parseFloat(b));
    }

    public static String stringMultiplication(String a, String b){
        return Float.toString(Float.parseFloat(a) * Float.parseFloat(b));
    }

    public static String stringDivision(String a, String b){
        return Float.toString(Float.parseFloat(a) + Float.parseFloat(b));
    }

    public static void fixReferences(int deleted_index){
        for(int i = 0; i < triads.size(); i++){
            if(triads.get(i).getA() != null && triads.get(i).getA().getFirst().equals("TRIAD") &&
                    Integer.parseInt(triads.get(i).getA().getSecond()) > deleted_index){
//                triads.get(i).getA().setSecond(Integer.toString(Integer.parseInt(triads.get(i).getA().getSecond()) - 1));
                triads.set(i, new Triad(triads.get(i).getOp(),
                        new Pair("TRIAD", Integer.toString(Integer.parseInt(triads.get(i).getA().getSecond()) - 1)),
                        triads.get(i).getB()));
            }
            if(triads.get(i).getB() != null && triads.get(i).getB().getFirst().equals("TRIAD") &&
                    Integer.parseInt(triads.get(i).getB().getSecond()) > deleted_index){
//                triads.get(i).getB().setSecond(Integer.toString(Integer.parseInt(triads.get(i).getB().getSecond()) - 1));
                triads.set(i, new Triad(triads.get(i).getOp(), triads.get(i).getA(),
                        new Pair("TRIAD", Integer.toString(Integer.parseInt(triads.get(i).getB().getSecond()) - 1))));
            }
        }
    }
}
