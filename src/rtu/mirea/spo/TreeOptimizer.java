package rtu.mirea.spo;

import java.util.ArrayList;

/**
 * Удалять if-else если их тела пусты
 * При непустом if удалять else если его тело пустое
 * При непустом else не удалять if, даже если он пуст
 * Удалять циклы если их тела пусты
 */

public class TreeOptimizer {
    public static LexTree optimizeTree(LexTree tree){
        tree = new LexTree(treeTraversal(tree.getRoot()));
        return tree;
    }

    public static LexNode treeTraversal(LexNode node){
        if(node.getLabel().getFirst().equals("VAR") || node.getLabel().getFirst().equals("VAR_TYPE") ||
                node.getLabel().getFirst().equals("IF_KW") || node.getLabel().getFirst().equals("ELSE_KW") ||
                node.getLabel().getFirst().equals("WHILE_KW") || node.getLabel().getFirst().equals("DO_KW") ||
                node.getLabel().getFirst().equals("OP") || node.getLabel().getFirst().equals("LOGICAL_OP") ||
                node.getLabel().getFirst().equals("ASSIGN_OP") || node.getLabel().getFirst().equals("NUMBER") ||
                node.getLabel().getFirst().equals("L_BR") || node.getLabel().getFirst().equals("R_BR") ||
                node.getLabel().getFirst().equals("L_S_BR") || node.getLabel().getFirst().equals("R_S_BR") ||
                node.getLabel().getFirst().equals("SEP") || node.getLabel().getFirst().equals("PRINT_KW")) {
            return node;
        }
        else {
            for(int i = 0; i < node.getChildren().size(); i++){
                if(node.getChildren().get(i).getLabel().getFirst().equals("if_expr")){
                    node.getChildren().set(i, optimizeIf(node.getChildren().get(i)));
                    if(node.getChildren().get(i) == null) {
                        node.getChildren().remove(i);
                        i--;
                        return null;
                    }
                }
                else if(node.getChildren().get(i).getLabel().getFirst().equals("while_expr") ||
                        node.getChildren().get(i).getLabel().getFirst().equals("do_while_expr")){
                    node.getChildren().set(i, optimizeWhile(node.getChildren().get(i)));
                    if(node.getChildren().get(i) == null) {
                        node.getChildren().remove(i);
                        i--;
                        return null;
                    }
                }
                else{
                    LexNode subNode = treeTraversal(node.getChildren().get(i));
                    if(subNode == null) {
                        node.getChildren().remove(i);
                        i--;
                    }
                }
            }
        }
        return node;
    }

    public static LexNode optimizeIf(LexNode expr){
        if(expr.getChildren().get(1).getChildren().size() < 3){ // пустой if
            System.out.println("EMPTY IF");
            if(expr.getChildren().size() > 2){ // есть else
                System.out.println("\tELSE IS PRESENT");
                if(expr.getChildren().get(3).getChildren().size() < 3){ // пустой else
                    System.out.println("\t\tEMPTY ELSE");
                    return null;
                }
            }
            else { // нет else
                return null;
            }
        }
        else if(expr.getChildren().size() > 2 && expr.getChildren().get(3).getChildren().size() < 3){ // пустой else
            System.out.println("EMPTY ELSE");
            expr.getChildren().remove(2);
            expr.getChildren().remove(2);
            return expr;
        }
        return expr;
    }

    public static LexNode optimizeWhile(LexNode expr){
        if(expr.getChildren().get(1).getChildren().size() < 3){ // пустой while
            System.out.println("EMPTY WHILE");
            return null;
        }
        return expr;
    }


}
