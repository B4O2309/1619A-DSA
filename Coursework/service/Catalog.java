package Coursework.service;

import Coursework.algorithm.SearchingAlgorithms;
import Coursework.algorithm.SortingAlgorithms;
import Coursework.ds.Node;
import Coursework.model.Book;

public class Catalog {

    private Node<Book> head;
    private int        size;

    private static final String[] CATEGORIES = {
        "Fiction", "Dystopian", "Fantasy", "Sci-Fi",
        "Romance", "Mystery", "Education", "History"
    };

    public Catalog() {
        head = null;
        size = 0;
        seedData();
    }

    // Seed 

    private void seedData() {
        insertSorted(new Book("Animal Farm",                               "George Orwell",        320_000,  55, "Fiction"));
        insertSorted(new Book("The Catcher in the Rye",                   "J.D. Salinger",        385_000,  35, "Fiction"));
        insertSorted(new Book("The Great Gatsby",                         "F. Scott Fitzgerald",  350_000,  40, "Fiction"));
        insertSorted(new Book("To Kill a Mockingbird",                    "Harper Lee",           290_000,  50, "Fiction"));
        insertSorted(new Book("Of Mice and Men",                          "John Steinbeck",       305_000,  30, "Fiction"));
        insertSorted(new Book("1984",                                     "George Orwell",        450_000,  30, "Dystopian"));
        insertSorted(new Book("Brave New World",                          "Aldous Huxley",        480_000,  25, "Dystopian"));
        insertSorted(new Book("Fahrenheit 451",                           "Ray Bradbury",         400_000,  28, "Dystopian"));
        insertSorted(new Book("The Hobbit",                               "J.R.R. Tolkien",       415_000,  40, "Fantasy"));
        insertSorted(new Book("The Lord of the Rings",                    "J.R.R. Tolkien",       545_000,  30, "Fantasy"));
        insertSorted(new Book("Harry Potter and the Philosopher's Stone", "J.K. Rowling",         399_000,  60, "Fantasy"));
        insertSorted(new Book("Dune",                                     "Frank Herbert",        610_000,  20, "Sci-Fi"));
        insertSorted(new Book("The Hitchhiker's Guide",                   "Douglas Adams",        370_000,  35, "Sci-Fi"));
        insertSorted(new Book("Ender's Game",                             "Orson Scott Card",     430_000,  25, "Sci-Fi"));
        insertSorted(new Book("Pride and Prejudice",                      "Jane Austen",          320_000,  45, "Romance"));
        insertSorted(new Book("Jane Eyre",                                "Charlotte Bronte",     350_000,  38, "Romance"));
        insertSorted(new Book("And Then There Were None",                 "Agatha Christie",      385_000,  42, "Mystery"));
        insertSorted(new Book("The Girl with the Dragon Tattoo",          "Stieg Larsson",        480_000,  22, "Mystery"));
        insertSorted(new Book("Thinking, Fast and Slow",                  "Daniel Kahneman",      575_000,  20, "Education"));
        insertSorted(new Book("Atomic Habits",                            "James Clear",          545_000,  50, "Education"));
        insertSorted(new Book("The 7 Habits of Highly Effective People",  "Stephen Covey",        510_000,  33, "Education"));
        insertSorted(new Book("Sapiens",                                  "Yuval Noah Harari",    545_000,  45, "History"));
        insertSorted(new Book("A Brief History of Time",                  "Stephen Hawking",      480_000,  28, "History"));
        seedSoldCounts();
    }

    private void seedSoldCounts() {
        setSold("Harry Potter and the Philosopher's Stone", 1240);
        setSold("Atomic Habits",                            980);
        setSold("1984",                                     870);
        setSold("To Kill a Mockingbird",                    810);
        setSold("Sapiens",                                  760);
        setSold("The Hobbit",                               720);
        setSold("Dune",                                     690);
        setSold("Pride and Prejudice",                      650);
        setSold("The Great Gatsby",                         600);
        setSold("Thinking, Fast and Slow",                  580);
    }

    private void setSold(String title, int n) {
        Book b = findByTitle(title);
        if (b != null) b.setSoldCount(n);
    }

    //  Add / Remove

    public void addBook(Book book) { insertSorted(book); }

    // Insert maintaining alphabetical order
    private void insertSorted(Book book) {
        Node<Book> node = new Node<>(book);
        if (head == null || book.compareTo(head.data) <= 0) {
            node.next = head;
            head      = node;
            size++;
            return;
        }
        Node<Book> cur = head;
        while (cur.next != null && cur.next.data.compareTo(book) < 0)
            cur = cur.next;
        node.next = cur.next;
        cur.next  = node;
        size++;
    }

    public boolean removeBook(int bookID) {
        if (head == null) return false;
        if (head.data.getBookID() == bookID) { head = head.next; size--; return true; }
        Node<Book> cur = head;
        while (cur.next != null) {
            if (cur.next.data.getBookID() == bookID) {
                cur.next = cur.next.next;
                size--;
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    // Best Sellers 

    public void showBestSellers(int topN) {
        if (head == null) { System.out.println("  Catalog is empty."); return; }

        // Build a shallow copy of the linked list — avoids modifying catalog order
        Node<Book> copyHead = null, copyTail = null;
        Node<Book> cur = head;
        while (cur != null) {
            Node<Book> newNode = new Node<>(cur.data);
            if (copyHead == null) { copyHead = newNode; copyTail = newNode; }
            else                  { copyTail.next = newNode; copyTail = newNode; }
            cur = cur.next;
        }

        // Sort copy by soldCount descending directly on linked list
        copyHead = SortingAlgorithms.mergeSort(copyHead,
                (a, b) -> Integer.compare(b.getSoldCount(), a.getSoldCount()));

        int limit = Math.min(topN, size);
        System.out.println("\n  +-----------------------------------------------------------+");
        System.out.printf ("  |        ★  TOP %d BEST SELLERS  ★%-23s|%n", limit, "");
        System.out.println("  +----+--------------------------------+---------------+------+");
        System.out.printf ("  | %-2s | %-30s | %-13s | %-4s |%n", "#", "Title", "Price", "Sold");
        System.out.println("  +----+--------------------------------+---------------+------+");
        Node<Book> node = copyHead;
        for (int i = 0; i < limit && node != null; i++, node = node.next) {
            Book b = node.data;
            System.out.printf("  | %-2d | %-30s | %13s | %-4d |%n",
                    i + 1, truncate(b.getTitle(), 30), Book.vnd(b.getPrice()), b.getSoldCount());
        }
        System.out.println("  +----+--------------------------------+---------------+------+");
        System.out.printf ("  | Showing %d of %d books. Browse by category for more.      |%n", limit, size);
        System.out.println("  +-----------------------------------------------------------+");
    }

    // Category

    public void showCategories() {
        System.out.println("\n  Available categories:");
        for (int i = 0; i < CATEGORIES.length; i++)
            System.out.printf("    %d. %s%n", i + 1, CATEGORIES[i]);
    }

    public int showByCategory(String category) {
        Node<Book> cur   = head;
        int        count = 0;
        System.out.println("\n  +------+----------------------------------+---------------+-------+");
        System.out.printf ("  |  %-60s|%n", "Category: " + category.toUpperCase());
        System.out.println("  +------+----------------------------------+---------------+-------+");
        System.out.printf ("  | %-4s | %-32s | %-13s | %-5s |%n", "ID", "Title", "Price", "Stock");
        System.out.println("  +------+----------------------------------+---------------+-------+");
        while (cur != null) {
            Book b = cur.data;
            if (b.getCategory().equalsIgnoreCase(category)) {
                System.out.printf("  | %-4d | %-32s | %13s | %-5d |%n",
                        b.getBookID(), truncate(b.getTitle(), 32), Book.vnd(b.getPrice()), b.getStock());
                count++;
            }
            cur = cur.next;
        }
        if (count == 0)
            System.out.printf("  | %-66s|%n", "  No books in this category.");
        System.out.println("  +------+----------------------------------+---------------+-------+");
        System.out.printf ("  | %d book(s) found.%-50s|%n", count, "");
        System.out.println("  +------+----------------------------------+---------------+-------+");
        return count;
    }

    // Search

    public int findByPartialTitle(String keyword) {
        String     kw    = keyword.toLowerCase();
        Node<Book> cur   = head;
        int        count = 0;

        System.out.println("\n  +------+----------------------------------+---------------+-------+");
        System.out.printf ("  |  Search: \"%-55s|%n", keyword + "\"");
        System.out.println("  +------+----------------------------------+---------------+-------+");
        System.out.printf ("  | %-4s | %-32s | %-13s | %-5s |%n", "ID", "Title", "Price", "Stock");
        System.out.println("  +------+----------------------------------+---------------+-------+");

        while (cur != null) {
            Book b = cur.data;
            if (b.getTitle().toLowerCase().contains(kw)) {
                System.out.printf("  | %-4d | %-32s | %13s | %-5d |%n",
                        b.getBookID(), truncate(b.getTitle(), 32), Book.vnd(b.getPrice()), b.getStock());
                count++;
            }
            cur = cur.next;
        }

        if (count == 0) System.out.printf("  | %-66s|%n", "  No results found.");
        System.out.println("  +------+----------------------------------+---------------+-------+");
        System.out.printf ("  | %d result(s).%-53s|%n", count, "");
        System.out.println("  +------+----------------------------------+---------------+-------+");
        return count;
    }

    public Book findByPartialTitle_first(String keyword) {
        String     kw  = keyword.toLowerCase();
        Node<Book> cur = head;
        while (cur != null) {
            if (cur.data.getTitle().toLowerCase().contains(kw)) return cur.data;
            cur = cur.next;
        }
        return null;
    }

    public Book findByID(int bookID) {
        Node<Book> cur = head;
        while (cur != null) {
            if (cur.data.getBookID() == bookID) return cur.data;
            cur = cur.next;
        }
        return null;
    }

    // Binary search by exact title — catalog always sorted alphabetically via insertSorted()
    public Book findByTitle(String title) {
        return SearchingAlgorithms.binarySearchByTitle(toArray(), title);
    }

    public void searchByKeyword(String keyword) {
        String kw  = keyword.toLowerCase();
        Node<Book> cur = head;
        boolean any = false;
        System.out.printf("%n  Search: \"%s\"%n", keyword);
        System.out.println("  +------+----------------------------------+---------------+-------+");
        while (cur != null) {
            Book b = cur.data;
            if (b.getTitle().toLowerCase().contains(kw)
                    || b.getAuthor().toLowerCase().contains(kw)) {
                System.out.printf("  | %-4d | %-32s | %13s | %-5d |%n",
                        b.getBookID(), truncate(b.getTitle(), 32), Book.vnd(b.getPrice()), b.getStock());
                any = true;
            }
            cur = cur.next;
        }
        if (!any) System.out.printf("  | %-66s|%n", "  No results found.");
        System.out.println("  +------+----------------------------------+---------------+-------+");
    }

    // Full Catalog

    public void displayAll() {
        System.out.println("\n  +------+----------------------------------+----------------------+");
        System.out.printf ("  | %-4s | %-32s | %-20s |%n", "ID", "Title", "Author");
        System.out.println("  +------+----------------------------------+----------------------+");
        Node<Book> cur = head;
        while (cur != null) {
            Book b = cur.data;
            System.out.printf("  | %-4d | %-32s | %-20s |%n",
                    b.getBookID(), truncate(b.getTitle(), 32), truncate(b.getAuthor(), 20));
            System.out.printf("  |      | %-13s  Stock:%-3d  %-12s | Sold: %-15d|%n",
                    Book.vnd(b.getPrice()), b.getStock(), b.getCategory(), b.getSoldCount());
            System.out.println("  +------+----------------------------------+----------------------+");
            cur = cur.next;
        }
        System.out.printf("  Total: %d books%n", size);
    }

    // Helpers

    public String resolveCategory(String input) {
        String trimmed = input.trim();
        try {
            int idx = Integer.parseInt(trimmed);
            return getCategoryByIndex(idx);
        } catch (NumberFormatException ignored) {}

        for (String cat : CATEGORIES)
            if (cat.equalsIgnoreCase(trimmed)) return cat;

        String lower        = trimmed.toLowerCase();
        String partialMatch = null;
        int    matchCount   = 0;
        for (String cat : CATEGORIES) {
            if (cat.toLowerCase().startsWith(lower)) {
                partialMatch = cat;
                matchCount++;
            }
        }
        if (matchCount == 1) return partialMatch;
        if (matchCount > 1) {
            System.out.println("  Multiple categories match — please be more specific:");
            for (String cat : CATEGORIES)
                if (cat.toLowerCase().startsWith(lower))
                    System.out.printf("    - %s%n", cat);
        }
        return null;
    }

    // Convert to array — used only by findByTitle() for binary search
    private Book[] toArray() {
        Book[]     arr = new Book[size];
        Node<Book> cur = head;
        for (int i = 0; i < size; i++) { arr[i] = cur.data; cur = cur.next; }
        return arr;
    }

    public String getCategoryByIndex(int index) {
        if (index < 1 || index > CATEGORIES.length) return null;
        return CATEGORIES[index - 1];
    }

    public int getCategoryCount() { return CATEGORIES.length; }
    public int size()             { return size; }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}