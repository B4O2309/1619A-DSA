package Coursework.model;

import Coursework.ds.BookList;
import Coursework.service.Catalog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order implements Comparable<Order> {

    private static int idCounter = 1000;

    public enum Status { PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED }

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int           orderID;
    private final String        customerName;
    private final String        shippingAddress;
    private final String        customerEmail;
    private final LocalDateTime placedAt;
    private       Status        status;
    private       double        totalPrice;   // VND
    private final BookList      books;

    public Order(String customerName, String shippingAddress, String customerEmail) {
        this.orderID         = ++idCounter;
        this.customerName    = customerName;
        this.shippingAddress = shippingAddress;
        this.customerEmail   = customerEmail;
        this.placedAt        = LocalDateTime.now();
        this.status          = Status.PENDING;
        this.totalPrice      = 0.0;
        this.books           = new BookList();
    }

    // Phase 1: build cart (no stock touched)

    public boolean addBook(Book catalogBook, int qty) {
        if (catalogBook == null || qty <= 0) return false;

        if (!catalogBook.hasStock(qty)) {
            System.out.printf("  X Insufficient stock for '%s' (available: %d, requested: %d)%n",
                    catalogBook.getTitle(), catalogBook.getStock(), qty);
            return false;
        }

        Book snap = new Book(catalogBook.getTitle(), catalogBook.getAuthor(),
                             catalogBook.getPrice(), qty, catalogBook.getCategory());
        snap.forceID(catalogBook.getBookID());
        books.addLast(snap);
        totalPrice += catalogBook.getPrice() * qty;
        return true;
    }

    // Phase 2: commit on submit

    public boolean commitStock(Catalog catalog) {
        boolean[] ok = {true};
        books.forEachBook(snap -> {
            Book cat = catalog.findByID(snap.getBookID());
            if (cat == null || !cat.hasStock(snap.getStock())) {
                System.out.printf("  X '%s' no longer has enough stock. Order not placed.%n",
                        snap.getTitle());
                ok[0] = false;
            }
        });
        if (!ok[0]) return false;

        books.forEachBook(snap -> {
            Book cat = catalog.findByID(snap.getBookID());
            if (cat != null) cat.reserveStock(snap.getStock());
        });
        return true;
    }

    // Sort books alphabetically by title directly on linked list — O(n log n), O(log n) space
    public void sortBooks() {
        books.mergeSort();
    }

    public void restoreStock(Catalog catalog) {
        books.forEachBook(snap -> {
            Book cat = catalog.findByID(snap.getBookID());
            if (cat != null) cat.restoreStock(snap.getStock());
        });
    }

    public boolean canBeUndone() { return status == Status.PROCESSING; }

    // getters / setters
    public int      getOrderID()          { return orderID; }
    public String   getCustomerName()     { return customerName; }
    public String   getShippingAddress()  { return shippingAddress; }
    public String   getEmail()            { return customerEmail; }
    public Status   getStatus()           { return status; }
    public double   getTotalPrice()       { return totalPrice; }
    public BookList getBooks()            { return books; }
    public void     setStatus(Status s)   { this.status = s; }

    @Override
    public int compareTo(Order other) {
        return Integer.compare(this.orderID, other.orderID);
    }

    @Override
    public String toString() {
        return String.format("Order#%d | %-15s | %d book(s) | %13s | %-12s | %s",
                orderID, customerName, books.size(),
                Book.vnd(totalPrice), status, placedAt.format(FMT));
    }

    public void printDetail() {
        System.out.println("  +----------------------------------------------------+");
        System.out.printf ("  | Order   : #%-39d|%n", orderID);
        System.out.printf ("  | Customer: %-39s|%n", customerName);
        System.out.printf ("  | Address : %-39s|%n", shippingAddress);
        System.out.printf ("  | Status  : %-39s|%n", status);
        System.out.printf ("  | Total   : %-39s|%n", Book.vnd(totalPrice));
        System.out.println("  +----------------------------------------------------+");
        System.out.println("  | Books (sorted by title when processed):            |");
        books.display();
        System.out.println("  +----------------------------------------------------+");
    }
}