package rtu.mirea.spo;

public class Triad {
    private Pair<String, String> a;
    private Pair<String, String> b;
    private Pair<String, String> op;

    public Triad(Pair<String, String> op, Pair<String, String> a, Pair<String, String> b) {
        this.a = a;
        this.b = b;
        this.op = op;
    }

    public Pair<String, String> getA() {
        return a;
    }

    public Pair<String, String> getB() {
        return b;
    }

    public Pair<String, String> getOp() {
        return op;
    }

    public void setA(Pair<String, String> a) {
        this.a = a;
    }

    public void setB(Pair<String, String> b) {
        this.b = b;
    }

    public void setOp(Pair<String, String> op) {
        this.op = op;
    }

    @Override
    public String toString() {
        return "(" + op + ", " + a + ", " + b + ")";
    }
}
