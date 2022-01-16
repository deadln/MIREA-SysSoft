package rtu.mirea.spo;

public class Triad {
    private String a;
    private String b;
    private String op;

    public Triad(String a, String b, String op) {
        this.a = a;
        this.b = b;
        this.op = op;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public String getOp() {
        return op;
    }

    public void setA(String a) {
        this.a = a;
    }

    public void setB(String b) {
        this.b = b;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
