package Coursework.algorithm;

import Coursework.ds.Node;
import Coursework.model.Book;
import Coursework.model.Order;

public class SearchingAlgorithms {

    private SearchingAlgorithms() {}

    // Linear Search

    public static Order linearSearchByID(Node<Order> head, int orderID) {
        Node<Order> cur = head;
        while (cur != null) {
            if (cur.data.getOrderID() == orderID) return cur.data;
            cur = cur.next;
        }
        return null;
    }

    public static void linearSearchByName(Node<Order> head, String name) {
        Node<Order> cur = head;
        boolean any = false;
        while (cur != null) {
            if (cur.data.getCustomerName().equalsIgnoreCase(name)) {
                System.out.println("  " + cur.data);
                any = true;
            }
            cur = cur.next;
        }
        if (!any) System.out.println("  No orders found for: " + name);
    }

    public static void linearSearchByEmail(Node<Order> head, String email) {
        Node<Order> cur = head;
        boolean any = false;
        while (cur != null) {
            if (cur.data.getEmail().equalsIgnoreCase(email)) {
                System.out.println("  " + cur.data);
                any = true;
            }
            cur = cur.next;
        }
        if (!any) System.out.println("  You have no orders yet.");
    }

    // Binary Search

    public static Book binarySearchByTitle(Book[] arr, String title) {
        int lo = 0, hi = arr.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cmp = arr[mid].getTitle().compareToIgnoreCase(title);
            if (cmp == 0) 
                return arr[mid];
            else if (cmp < 0) 
                lo = mid + 1;
            else
                hi = mid - 1;
        }
        return null;
    }
}
