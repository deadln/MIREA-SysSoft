package rtu.mirea.spo;

import java.util.ArrayList;

public class LexNode {
    Pair<String, String> label;
    ArrayList<LexNode> children;

    public LexNode(Pair<String, String> label) {
        this.label = label;
        this.children = new ArrayList<>();
    }

    public void addChild(LexNode child)
    {
        children.add(child);
    }

    public Pair<String, String> getLabel() {
        return label;
    }

    public void showTree()
    {
        System.out.println("PARENT");
        System.out.println(label.toString());
        System.out.println("CHILDREN");
        for (LexNode node:
                children) {
            System.out.println(node.getLabel().toString());
        }
        for (LexNode node:
                children) {
            node.showTree();
        }
    }
}
