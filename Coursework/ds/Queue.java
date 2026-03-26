package Coursework.ds;

public class Queue<T> {
    private Node<T> head; // dequeue from here
    private Node<T> tail; // enqueue here
    private int     size;

    public Queue() {
        head = null;
        tail = null;
        size = 0;
    }

    public void enqueue(T item) {
        Node<T> node = new Node<>(item);
        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail      = node;
        }
        size++;
    }

    public T dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty, cannot dequeue!");
        T data = head.data;
        head   = head.next;
        if (head != null) head.prev = null;
        else              tail = null;
        size--;
        return data;
    }

    public T peek() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty, cannot peek!");
        return head.data;
    }

    public boolean isEmpty() { return size == 0; }
    public int     size()    { return size; }

    public void display() {
        if (isEmpty()) { System.out.println("  [Queue is empty]"); return; }
        System.out.println("  Queue:");
        Node<T> cur = head;
        int i = 1;
        while (cur != null) {
            System.out.printf("    [%d] %s%n", i++, cur.data);
            cur = cur.next;
        }
    }

    public void enqueueAtFront(T item) {
        Node<T> node = new Node<>(item);
        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head      = node;
        }
        size++;
    }
}
