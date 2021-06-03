package rtu.mirea.spo;

public class LinkedListNode<T> {
    private T value;
    LinkedListNode<T> next;

    public LinkedListNode() {
    }

    public LinkedListNode(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public LinkedListNode<T> getNext() {
        return next;
    }

    public void setNext(LinkedListNode<T> next) {
        this.next = next;
    }
}
