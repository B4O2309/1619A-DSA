package Coursework.model;

public class Book implements Comparable<Book> {
    private static int idCounter = 100;

    private int    bookID;
    private String title;
    private String author;
    private double price;      // stored in VND
    private int    stock;
    private String category;
    private int    soldCount;

    public Book(String title, String author, double price, int stock, String category) {
        this.bookID    = ++idCounter;
        this.title     = title;
        this.author    = author;
        this.price     = price;
        this.stock     = stock;
        this.category  = category;
        this.soldCount = 0;
    }

    // Format price as Vietnamese Dong (₫) — moved from Catalog to avoid circular dependency
    public static String vnd(double price) {
        long amount = Math.round(price);
        return String.format("%,d₫", amount).replace(',', '.');
    }

    // stock & sales

    public boolean hasStock(int qty) {
        return stock >= qty;
    }

    public boolean reserveStock(int qty) {
        if (!hasStock(qty)) return false;
        stock     -= qty;
        soldCount += qty;
        return true;
    }

    public void restoreStock(int qty) {
        stock     += qty;
        soldCount -= qty;
        if (soldCount < 0) soldCount = 0;
    }

    public void forceID(int id) { this.bookID = id; }

    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    // Getters / setters
    public int    getBookID()    { return bookID; }
    public String getTitle()     { return title; }
    public String getAuthor()    { return author; }
    public double getPrice()     { return price; }
    public int    getStock()     { return stock; }
    public String getCategory()  { return category; }
    public int    getSoldCount() { return soldCount; }

    public void setTitle(String t)    { this.title     = t; }
    public void setAuthor(String a)   { this.author    = a; }
    public void setPrice(double p)    { this.price     = p; }
    public void setStock(int s)       { this.stock     = s; }
    public void setCategory(String c) { this.category  = c; }
    public void setSoldCount(int n)   { this.soldCount = n; }

    @Override
    public String toString() {
        return String.format("[%d] %-34s | %-22s | %13s | Stock:%3d",
                bookID, title, author, vnd(price), stock);
    }
}
