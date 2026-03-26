package Coursework.ds;

import Coursework.model.Order;
import Coursework.algorithm.SearchingAlgorithms;

public class OrderList {

    private Node<Order> head;
    private int         size;

    public OrderList() { head = null; size = 0; }

    /** Prepend — newest order appears first — O(1) */
    public void add(Order order) {
        Node<Order> node = new Node<>(order);
        node.next = head;
        head      = node;
        size++;
    }

    // Linear search by order ID 
    public Order findByID(int orderID) {
        return SearchingAlgorithms.linearSearchByID(head, orderID);
    }

    // Print all orders matching customer name
    public void searchByCustomerName(String name) {
        SearchingAlgorithms.linearSearchByName(head, name);
    }

    // Print all orders belonging to a customer email
    public void searchByEmail(String email) {
        SearchingAlgorithms.linearSearchByEmail(head, email);
    }

    // Display all orders
    public void displayAll() {
        if (head == null) { System.out.println("  No orders in the system."); return; }
        Node<Order> cur = head;
        while (cur != null) {
            System.out.println("  " + cur.data);
            cur = cur.next;
        }
    }

    public int size() { return size; }
}
