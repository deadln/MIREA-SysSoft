package rtu.mirea.spo;

import java.util.ArrayList;

public class LexTree {
    LexNode root;

    public LexTree(Pair<String, String> label) {
        this.root = new LexNode(label);
    }

    public LexTree(LexNode root) {
        this.root = root;
    }

    public void addExpr(LexNode node)
    {
        root.addChild(node);
    }

    public LexNode getRoot() {
        return root;
    }

    public void showTree()
    {
        root.showTree();
    }

}
