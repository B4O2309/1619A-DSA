package Coursework.ds;

public class Stack<T> {
    private Node<T> top; // Head of the stack
    private int     size;

    public Stack() {
        top  = null;
        size = 0;
    }   

    public void push(T item) {
        Node<T> node = new Node<>(item);
        node.next = top;
        top       = node;
        size++;
    }

    public T pop() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty, cannot pop!");
        T data = top.data;
        top    = top.next;
        size--;
        return data;
    }

    public T peek() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty — cannot peek");
        return top.data;
    }

    public boolean isEmpty() { return size == 0; }
    public int     size()    { return size; }

    public void display() {
        if (isEmpty()) { System.out.println("  [Stack is empty]"); return; }
        System.out.println("  Stack:");
        Node<T> cur = top;
        int i = 1;
        while (cur != null) {
            System.out.printf("    [%d] %s%n", i++, cur.data);
            cur = cur.next;
        }
    }
}
