package rtu.mirea.spo;

public class Test {
    public static void main(String[] args) {
        MyHashSet<String> myset = new MyHashSet<String>();

        myset.add("abc");
        myset.add("bcd");
        myset.add("abd");
        myset.add("efg");
        myset.add("e");
        myset.add("ef");
        myset.add("eg");
        myset.add("g");
        myset.remove("efg");

        System.out.println(myset.toString());
        System.out.println(myset.contains("abc"));
        System.out.println(myset.contains("efg"));

    }
}
