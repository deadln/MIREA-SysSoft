package rtu.mirea.spo;

public class MyLinkedList<T> {
    LinkedListNode<T> root;

    public void add(T item)
    {
        if(root == null)
        {
            root = new LinkedListNode<T>(item);
        }
        else
        {
            LinkedListNode<T> iter = root;
            while(iter.getNext() != null)
                iter = iter.getNext();
            iter.setNext(new LinkedListNode<>(item));
        }
    }

    public T get(int number)
    {
        int c = 0;
        LinkedListNode<T> iter = root;
        while(iter != null && c < number)
        {
            iter = iter.getNext();
            c++;
        }
        if(iter != null)
            return iter.getValue();
        return null;
    }

    @Override
    public String toString()
    {
        LinkedListNode<T> iter = root;
        StringBuilder sb = new StringBuilder("[");
        while(iter != null)
        {
            sb.append(iter.getValue().toString() + " ");
            iter = iter.getNext();
        }
        sb.append("]");
        return sb.toString();
    }
}
