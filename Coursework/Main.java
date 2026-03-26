package Coursework;

import Coursework.model.Book;
import Coursework.model.Order;
import Coursework.model.User;
import Coursework.service.BookstoreSystem;

import java.util.Scanner;

public class Main {

    private static class CancelException extends RuntimeException {
        CancelException() { super("Cancelled"); }
    }

    private static final Scanner         sc  = new Scanner(System.in);
    private static final BookstoreSystem sys = new BookstoreSystem();
    private static       User            me;

    public static void main(String[] args) {
        banner();
        while (true) {
            login();
            showWelcome();
            boolean sessionActive = true;
            while (sessionActive) {
                printMenu();
                String choice;
                try { choice = input("Option").trim(); }
                catch (CancelException e) { continue; }

                if (choice.equals("0")) {
                    System.out.println("\n  Logged out. Returning to login screen...");
                    sessionActive = false;
                } else if (choice.equals("99")) {
                    System.out.println("\n  Goodbye!");
                    sc.close();
                    return;
                } else {
                    dispatch(choice);
                }
            }
        }
    }

    // Login

    private static void login() {
        System.out.println("\n  Select your role:");
        System.out.println("    1. Customer");
        System.out.println("    2. Staff");
        System.out.println("    3. Admin");
        String roleChoice, username;
        try {
            roleChoice = input("  Role (1/2/3)").trim();
            username   = input("  Username").trim();
        } catch (CancelException e) {
            roleChoice = "1"; username = "guest";
        }
        if (username.isEmpty()) username = "guest";
        me = switch (roleChoice) {
            case "2" -> new User(username, User.Role.STAFF);
            case "3" -> new User(username, User.Role.ADMIN);
            default  -> new User(username, User.Role.CUSTOMER);
        };
        System.out.println("\n  Welcome, " + me + "!");
    }

    private static void showWelcome() {
        System.out.println("\n  Here are our top picks for you:");
        sys.showBestSellers();
        System.out.println("  (Browse by category or search to explore more books)");
    }

    // Menus

    private static void printMenu() {
        System.out.println("\n" + "=".repeat(56));
        System.out.printf("  User: %-46s%n", me);
        System.out.println("=".repeat(56));
        switch (me.getRole()) {
            case CUSTOMER -> customerMenu();
            case STAFF    -> staffMenu();
            case ADMIN    -> adminMenu();
        }
        System.out.println("  0.  Logout (switch role)");
        System.out.println("  99. Exit program");
        System.out.println("  (Type 'cancel' at any prompt to return here)");
        System.out.println("-".repeat(56));
    }

    private static void customerMenu() {
        System.out.println("  1. View best sellers");
        System.out.println("  2. Browse books by category");
        System.out.println("  3. Search books (keyword)");
        System.out.println("  4. Place order");
        System.out.println("  5. View my orders");
        System.out.println("  6. Track order by ID");
    }

    private static void staffMenu() {
        System.out.println("  1. View pending queue");
        System.out.println("  2. Process next order");
        System.out.println("  3. Undo last processed order");
        System.out.println("  4. Ship an order");
        System.out.println("  5. Search order by ID");
        System.out.println("  6. Search orders by customer name");
        System.out.println("  7. View all orders");
        System.out.println("  8. View undo stack");
        System.out.println("  9. System status");
    }

    private static void adminMenu() {
        System.out.println("  --- Order Management ---");
        System.out.println("  1. View pending queue");
        System.out.println("  2. Process next order");
        System.out.println("  3. Undo last processed order");
        System.out.println("  4. Ship an order");
        System.out.println("  5. Search order by ID");
        System.out.println("  6. Search orders by customer name");
        System.out.println("  7. View all orders");
        System.out.println("  8. View undo stack");
        System.out.println("  9. System status");
        System.out.println("  --- Catalog Management ---");
        System.out.println("  10. View full catalog");
        System.out.println("  11. Add book to catalog");
        System.out.println("  12. Remove book from catalog");
    }

    // Dispatch

    private static void dispatch(String choice) {
        switch (me.getRole()) {
            case CUSTOMER -> handleCustomer(choice);
            case STAFF    -> handleStaff(choice);
            case ADMIN    -> handleAdmin(choice);
        }
    }

    // Customer handlers

    private static void handleCustomer(String c) {
        try {
            switch (c) {
                case "1" -> sys.showBestSellers();
                case "2" -> browseByCategoryFlow();
                case "3" -> { String kw = input("  Keyword (title or author)"); sys.searchCatalog(kw); }
                case "4" -> placeOrderFlow();
                case "5" -> sys.viewMyOrders(me);
                case "6" -> sys.trackOrder(me, readInt("  Order ID"));
                default  -> System.out.println("  Invalid option.");
            }
        } catch (CancelException e) {
            System.out.println("  Cancelled — returned to menu.");
        }
    }

    private static void browseByCategoryFlow() {
        sys.showCategories();
        System.out.printf("  Select category (1-%d, or 0 to cancel): ", sys.getCategoryCount());
        String pick = sc.nextLine().trim();
        if (pick.equalsIgnoreCase("cancel")) throw new CancelException();
        if (pick.equals("0")) return;
        try {
            String category = sys.getCategoryByIndex(Integer.parseInt(pick));
            if (category == null) { System.out.println("  Invalid selection."); return; }
            sys.browseByCategory(category);
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input.");
        }
    }

    private static void placeOrderFlow() {
        System.out.println("\n  Browse books by category (optional):");
        sys.showCategories();
        System.out.printf("  Select category (1-%d, or 0 to skip): ", sys.getCategoryCount());
        String catPick = sc.nextLine().trim();
        if (catPick.equalsIgnoreCase("cancel")) throw new CancelException();
        if (!catPick.equals("0")) {
            try {
                String cat = sys.getCategoryByIndex(Integer.parseInt(catPick));
                if (cat != null) sys.browseByCategory(cat);
            } catch (NumberFormatException ignored) {}
        }

        String address = input("  Shipping address");
        Order  order   = sys.createOrder(me, address);

        System.out.println("\n  Add books to your order.");
        System.out.println("  (Enter book ID or partial title. Type 'done' to finish, 'cancel' to abort.)");

        while (true) {
            String bookInput = input("\n  Book ID or title").trim();
            if (bookInput.equalsIgnoreCase("done")) break;

            Book found = sys.searchBookForOrder(bookInput);
            if (found == null) {
                System.out.print("  Enter book ID to add (or 0 to skip): ");
                String idInput = sc.nextLine().trim();
                if (idInput.equalsIgnoreCase("cancel")) throw new CancelException();
                if (idInput.equals("0") || idInput.isEmpty()) continue;
                found = sys.searchBookForOrder(idInput);
                if (found == null) continue;
            }

            System.out.println("\n  +------------------------------------------+");
            System.out.printf ("  | ID    : %-32d|%n", found.getBookID());
            System.out.printf ("  | Title : %-32s|%n", truncate(found.getTitle(), 32));
            System.out.printf ("  | Author: %-32s|%n", truncate(found.getAuthor(), 32));
            System.out.printf ("  | Price : %-32s|%n", Book.vnd(found.getPrice()));
            System.out.printf ("  | Stock : %-32d|%n", found.getStock());
            System.out.println("  +------------------------------------------+");

            if (found.getStock() == 0) { System.out.println("  X Out of stock."); continue; }

            System.out.print("  Add to cart? (y/n): ");
            String addChoice = sc.nextLine().trim();
            if (addChoice.equalsIgnoreCase("cancel")) throw new CancelException();
            if (!addChoice.equalsIgnoreCase("y")) continue;

            int qty = readPositiveInt("  Quantity");
            sys.addBookToOrder(order, found, qty);

            if (!order.getBooks().isEmpty()) {
                System.out.printf("  Cart: %d book(s) — Running total: %s%n",
                        order.getBooks().size(), Book.vnd(order.getTotalPrice()));
            }

            System.out.print("  Add another book? (y/n): ");
            String moreChoice = sc.nextLine().trim();
            if (moreChoice.equalsIgnoreCase("cancel")) throw new CancelException();
            if (!moreChoice.equalsIgnoreCase("y")) break;
        }

        if (order.getBooks().isEmpty()) {
            System.out.println("  X No books added — order cancelled.");
            return;
        }

        System.out.println("\n  --- Order Summary ---");
        order.printDetail();
        System.out.print("  Confirm and place order? (y/n): ");
        String confirm = sc.nextLine().trim();
        if (confirm.equalsIgnoreCase("cancel")) throw new CancelException();

        if (confirm.equalsIgnoreCase("y")) {
            if (sys.submitOrder(order)) sys.displayStatus();
        } else {
            System.out.println("  Order discarded.");
        }
    }

    // Staff handlers

    private static void handleStaff(String c) {
        try {
            switch (c) {
                case "1" -> { sys.viewQueue(me); sys.displayStatus(); }
                case "2" -> { Order o = sys.processNextOrder(me); if (o != null) { o.printDetail(); sys.displayStatus(); } }
                case "3" -> { Order u = sys.undoLastProcessed(me); if (u != null) { u.printDetail(); sys.displayStatus(); } }
                case "4" -> { sys.shipOrder(me, readInt("  Order ID to ship")); sys.displayStatus(); }
                case "5" -> sys.searchOrderByID(me, readInt("  Order ID"));
                case "6" -> sys.searchByCustomerName(me, input("  Customer name"));
                case "7" -> sys.viewAllOrders(me);
                case "8" -> sys.viewStack(me);
                case "9" -> sys.displayStatus();
                default  -> System.out.println("  Invalid option.");
            }
        } catch (CancelException e) {
            System.out.println("  Cancelled — returned to menu.");
        }
    }

    // Admin handlers

    private static void handleAdmin(String c) {
        if (c.matches("[1-9]")) { handleStaff(c); return; }
        try {
            switch (c) {
                case "10" -> sys.browseCatalog();
                case "11" -> {
                    System.out.println("\n  -- Add Book to Catalog --");
                    String title  = input("  Title");
                    String author = input("  Author");
                    double price  = readDouble("  Price in VND (e.g. 320000)");
                    int    stock  = readInt("  Stock quantity");
                    sys.showCategories();
                    String cat = null;
                    while (cat == null) {
                        String catInput = input("  Category (number or name)");
                        cat = sys.resolveCategory(catInput);
                        if (cat == null)
                            System.out.println("  X Invalid category. Enter a number (1-"
                                    + sys.getCategoryCount() + ") or a category name.");
                    }
                    System.out.printf("  Category selected: %s%n", cat);
                    sys.addBook(me, title, author, price, stock, cat);
                }
                case "12" -> {
                    sys.browseCatalog();
                    sys.removeBook(me, readInt("  Book ID to remove"));
                }
                default -> System.out.println("  Invalid option.");
            }
        } catch (CancelException e) {
            System.out.println("  Cancelled — returned to menu.");
        }
    }

    // Helpers

    private static void banner() {
        System.out.println("  +==================================================+");
        System.out.println("  |     ONLINE BOOKSTORE MANAGEMENT SYSTEM           |");
        System.out.println("  +==================================================+");
    }

    private static String input(String prompt) {
        System.out.print("  " + prompt + ": ");
        String line = sc.nextLine();
        if (line.trim().equalsIgnoreCase("cancel")) throw new CancelException();
        return line;
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            try {
                int v = Integer.parseInt(input(prompt).trim());
                if (v > 0) return v;
                System.out.println("  X Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("  X Please enter a valid integer.");
            }
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(input(prompt).trim()); }
            catch (NumberFormatException e) { System.out.println("  X Please enter a valid integer."); }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try { return Double.parseDouble(input(prompt).trim()); }
            catch (NumberFormatException e) { System.out.println("  X Please enter a valid number."); }
        }
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
