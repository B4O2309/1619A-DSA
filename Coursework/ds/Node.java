package Coursework.ds;

public class Node<T> {
    public T       data; // The data stored in the node, of type T
    public Node<T> next; // A reference to the next node in the list
    public Node<T> prev; // A reference to the previous node in the list

    public Node(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}
