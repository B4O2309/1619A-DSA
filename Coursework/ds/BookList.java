package Coursework.ds;

import Coursework.algorithm.SortingAlgorithms;
import Coursework.model.Book;

public class BookList {

    private Node<Book> head;
    private Node<Book> tail;
    private int        size;

    public BookList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void addLast(Book book) {
        Node<Book> node = new Node<>(book);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail      = node;
        }
        size++;
    }

    // Sort list alphabetically by title — O(n log n), O(log n) space
    public void mergeSort() {
        if (size <= 1) return;
        head = SortingAlgorithms.mergeSort(head);
        // repair tail pointer after sort
        tail = head;
        while (tail.next != null) tail = tail.next;
    }

    public void clear() { head = null; tail = null; size = 0; }

    public boolean isEmpty() { return size == 0; }
    public int     size()    { return size; }

    public void display() {
        Node<Book> cur = head;
        int i = 1;
        while (cur != null) {
            System.out.printf("      %d. %s%n", i++, cur.data);
            cur = cur.next;
        }
    }

    public void forEachBook(java.util.function.Consumer<Book> action) {
        Node<Book> cur = head;
        while (cur != null) { action.accept(cur.data); cur = cur.next; }
    }
}