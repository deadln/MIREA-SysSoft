package rtu.mirea.spo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Важная переменная: используется в выражении условия или выводится через print.
 * Идея для оптимизации: на этапе построения дерева определять "важность" переменных, а затем передавать в оптимизатор
 * высшего уровня важности, после чего помечать важными те переменные, которые были использованы для вычисления важных
 * переменных
 * t35 не переопределена ссылка +
 * t37 первая ссылка переопределена неправильно +
 * t42 вторая ссылка ошибается на +1
 * t52 не переопределена ссылка
 * t57 первая ссылка переопределена неправильно, вторая не переопределена
 *
 * t35 t42 t52 t57
 *
 * Заметки
 * На одну триаду могут ссылаться несколько триад
 * По какой-то причине какие-то ссылки не улавливаются программой
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
                }
                // типы данных, безусловный переход, вывод данных
                else if (tokens.get(i).getFirst().equals("VAR_TYPE") || tokens.get(i).getSecond().equals("!!") ||
                        tokens.get(i).getFirst().equals("PRINT_KW ")) {
                    Pair<Integer, Pair<String, String>> stack_elem = stack.remove(stack.size() - 1);
                    Pair<String, String> op1 = new Pair<>(stack_elem.getSecond());
                    // Переопределение ссылки перехода на триаду для безусловного перехода
                    if (tokens.get(i).getSecond().equals("!!")) {
                        // Если триада уже существует
                        if (triad_belongings.containsKey(stack_elem.getFirst())) {
                            op1 = new Pair("TRIAD", triad_belongings.get(stack_elem.getFirst()).toString());
                        }
                        // Иначе отложить назначение ссылки
                        else {
                            if(!delayed_reference.containsKey(Integer.parseInt(op1.getSecond()))){
                                delayed_reference.put(Integer.parseInt(op1.getSecond()), new HashSet<>());
//                                delayed_reference.put(Integer.parseInt(op1.getSecond()), triads.size());
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
                    // Создание новой триады и назначение операнду принадлежность к этой триаде
                    triads.add(new Triad(new Pair<>(tokens.get(i)), op1, null));
                    triad_belongings.put(stack_elem.getFirst(), triads.size() - 1);
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
                        if (triad_belongings.containsKey(stack_elem2.getFirst())) {
                            op2 = new Pair("TRIAD", triad_belongings.get(stack_elem2.getFirst()).toString());
                        } else { // Иначе отложить назначение ссылки
                            if(!delayed_reference.containsKey(Integer.parseInt(op2.getSecond()))){
                                delayed_reference.put(Integer.parseInt(op2.getSecond()), new HashSet<>());
//                                delayed_reference.put(Integer.parseInt(op2.getSecond()), triads.size());
                            }
                            delayed_reference.get(Integer.parseInt(op2.getSecond())).add(triads.size());
                        }

                        // Если триада уже существует (кажется, это не нужно, так как ссылкой является только второй операнд)
//                        if (triad_belongings.containsKey(stack_elem1.getFirst())) {
//                            op1 = new Pair("TRIAD", triad_belongings.get(stack_elem1.getFirst()).toString());
//                        } else { // Иначе отложить назначение ссылки
//                            if(delayed_reference.containsKey(Integer.parseInt(op1.getSecond()))){
//                                delayed_reference.put(Integer.parseInt(op1.getSecond()), new HashSet<>());
////                                delayed_reference.put(Integer.parseInt(op1.getSecond()), triads.size());
//                            }
//                            delayed_reference.get(Integer.parseInt(op1.getSecond())).add(triads.size());
//                        }
                    }

                    // Если на элемент существует отложенная ссылка (возможно не нужно, тк ссылка не может быть не на начало операции)
//                    if (delayed_reference.containsKey(stack_elem2.getFirst())) {
//                        Triad updated_triad = triads.get(delayed_reference.get((stack_elem2.getFirst())));
//                        // Замена ссылки для безусловного перехода
//                        if (updated_triad.getOp().getSecond().equals("!!")) {
//                            triads.set(delayed_reference.get(stack_elem2.getFirst()), new Triad(updated_triad.getOp(),
//                                    new Pair("TRIAD", Integer.toString(triads.size())), null));
//                        }
//                        // Замена ссылки для условных переходов
//                        else if (updated_triad.getOp().getSecond().equals("!F") || updated_triad.getOp().getSecond().equals("!T")){
//                            triads.set(delayed_reference.get(stack_elem2.getFirst()), new Triad(updated_triad.getOp(),
//                                    updated_triad.getA(), new Pair("TRIAD", Integer.toString(triads.size()))));
//                        }
//                    }

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

                    triads.add(new Triad(new Pair<>(tokens.get(i)), op1, op2));
                    // Не чинит сломанные, но ломает нормально работающую триаду
                    triad_belongings.put(stack_elem2.getFirst(), triads.size() - 1);
                    triad_belongings.put(stack_elem1.getFirst(), triads.size() - 1);

                }
                stack.add(new Pair(null, new Pair("TRIAD", Integer.toString(triads.size() - 1))));
            }

        }
        for (int i = 0; i < triads.size(); i++) {
            System.out.println("t" + i + ": " + triads.get(i));
        }

        return tokens;
    }
}
