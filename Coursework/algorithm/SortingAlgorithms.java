package Coursework.algorithm;

import Coursework.ds.Node;
import java.util.Comparator;

public class SortingAlgorithms {

    private SortingAlgorithms() {}

    //  Linked List Merge Sort with natural ordering (Comparable)

    public static <T extends Comparable<T>> Node<T> mergeSort(Node<T> head) {
        if (head == null || head.next == null) return head;
        Node<T> mid = getMiddle(head);
        Node<T> rightHead = mid.next;
        mid.next = null;
        Node<T> left  = mergeSort(head);
        Node<T> right = mergeSort(rightHead);
        return merge(left, right);
    }

    private static <T extends Comparable<T>> Node<T> getMiddle(Node<T> head) {
        Node<T> slow = head;
        Node<T> fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    private static <T extends Comparable<T>> Node<T> merge(Node<T> l, Node<T> r) {
        Node<T> dummy = new Node<>(null);
        Node<T> cur   = dummy;
        while (l != null && r != null) {
            if (l.data.compareTo(r.data) <= 0) 
                { 
                    cur.next = l; 
                    l = l.next; 
                }
            else    
                { 
                    cur.next = r; 
                    r = r.next; 
                }
            cur = cur.next;
        }
        cur.next = (l != null) ? l : r;
        return dummy.next;
    }

    // Linked List Merge Sort with custom Comparator

    public static <T> Node<T> mergeSort(Node<T> head, Comparator<T> cmp) {
        if (head == null || head.next == null) return head;
        Node<T> mid = getMiddle(head, cmp);
        Node<T> rightHead = mid.next;
        mid.next = null;
        Node<T> left  = mergeSort(head, cmp);
        Node<T> right = mergeSort(rightHead, cmp);
        return merge(left, right, cmp);
    }

    private static <T> Node<T> getMiddle(Node<T> head, Comparator<T> cmp) {
        Node<T> slow = head;
        Node<T> fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    private static <T> Node<T> merge(Node<T> l, Node<T> r, Comparator<T> cmp) {
        Node<T> dummy = new Node<>(null);
        Node<T> cur   = dummy;
        while (l != null && r != null) {
            if (cmp.compare(l.data, r.data) <= 0) 
                { 
                    cur.next = l; 
                    l = l.next; 
                }
            else 
                { 
                    cur.next = r; 
                    r = r.next; 
                }
            cur = cur.next;
        }
        cur.next = (l != null) ? l : r;
        return dummy.next;
    }
}