package rtu.mirea.spo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Важная переменная: используется в выражении условия или выводится через print.
 * Идея для оптимизации: на этапе построения дерева определять "важность" переменных, а затем передавать в оптимизатор
 * высшего уровня важности, после чего помечать важными те переменные, которые были использованы для вычисления важных
 * переменных
 */

public class TriadOptimizer {

    static ArrayList<Triad> triads;
    static ArrayList<Pair<Integer, Pair<String, String>>> stack;
    static HashMap<Integer, Integer> triad_belongings; // Номер элемента в ПОЛИЗ : номер триады, к которой элемент принадлежит
    static HashMap<Integer, Integer> delayed_reference; // Номер элемента в ПОЛИЗ: номер триады, в которой нужно обновить ссылку


    public static void init()
    {
        triads = new ArrayList<>();
        stack = new ArrayList<>();
        triad_belongings = new HashMap<>();
        delayed_reference = new HashMap<>();
    }

    public static ArrayList<Pair<String, String>> optimizeTriads(ArrayList<Pair<String, String>> tokens)
    {
        init();


        for (int i = 0; i < tokens.size(); i++)
        {
            if(tokens.get(i).getFirst().equals("NUMBER") || tokens.get(i).getFirst().equals("VAR"))
            {
                stack.add(new Pair(i, new Pair(tokens.get(i))));
            }
            else {
                // Унарные операторы
                if(tokens.get(i).getFirst().equals("SEP")){
                    triads.add(new Triad(new Pair<>(tokens.get(i)), null, null));
                }
                else if(tokens.get(i).getFirst().equals("VAR_TYPE") || tokens.get(i).getSecond().equals("!!") ||
                        tokens.get(i).getFirst().equals("PRINT_KW ")){
                    Pair<Integer, Pair<String, String>> stack_elem = stack.remove(stack.size() - 1);
                    Pair<String, String> op1 = new Pair<>(stack_elem.getSecond());
                    if(tokens.get(i).getSecond().equals("!!")){
                        if(triad_belongings.containsKey(stack_elem.getFirst())){
                            op1 = new Pair("TRIAD", triad_belongings.get(stack_elem.getFirst()).toString());
                        }
                        else{
                            delayed_reference.put(Integer.parseInt(op1.getSecond()), triads.size());
                        }
                    }
                    if(delayed_reference.containsKey(stack_elem.getFirst())){
                        Triad updated_triad = triads.get(delayed_reference.get((stack_elem.getFirst())));
                        if(updated_triad.getOp().getSecond().equals("!!")){
                            triads.set(delayed_reference.get(stack_elem.getFirst()), new Triad(updated_triad.getOp(), new Pair("TRIAD", Integer.toString(triads.size())), null));
                        }
                    }
                    triads.add(new Triad(new Pair<>(tokens.get(i)), op1, null));
                    triad_belongings.put(stack_elem.getFirst(), triads.size() - 1);
                }
                // Бинарные операторы
                else{
                    Pair<Integer, Pair<String, String>> stack_elem2 = stack.remove(stack.size() - 1);
                    Pair<String, String> op2 = new Pair<>(stack_elem2.getSecond());
                    if(delayed_reference.containsKey(stack_elem2.getFirst())){
                        Triad updated_triad = triads.get(delayed_reference.get((stack_elem2.getFirst())));
                        if(updated_triad.getOp().getSecond().equals("!!")){
                            triads.set(delayed_reference.get(stack_elem2.getFirst()), new Triad(updated_triad.getOp(), new Pair("TRIAD", Integer.toString(triads.size())), null));
                        }
                    }

                    Pair<Integer, Pair<String, String>> stack_elem1 = stack.remove(stack.size() - 1);
                    Pair<String, String> op1 = new Pair<>(stack_elem1.getSecond());
                    if(delayed_reference.containsKey(stack_elem1.getFirst())){
                        Triad updated_triad = triads.get(delayed_reference.get((stack_elem1.getFirst())));
                        if(updated_triad.getOp().getSecond().equals("!!")){
                            triads.set(delayed_reference.get(stack_elem1.getFirst()), new Triad(updated_triad.getOp(), new Pair("TRIAD", Integer.toString(triads.size())), null));
                        }
                    }

                    triads.add(new Triad(new Pair<>(tokens.get(i)), op1, op2));

                }
                stack.add(new Pair(null, new Pair("TRIAD", Integer.toString(triads.size() - 1))));
            }

        }
        for(int i = 0;i < triads.size(); i++){
            System.out.println("t" + i + ": " + triads.get(i));
        }

        return tokens;
    }
}
