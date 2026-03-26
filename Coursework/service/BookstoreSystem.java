package Coursework.service;

import Coursework.ds.OrderList;
import Coursework.ds.Queue;
import Coursework.ds.Stack;
import Coursework.model.Book;
import Coursework.model.Order;
import Coursework.model.User;

public class BookstoreSystem {

    private final Catalog      catalog   = new Catalog();
    private final Queue<Order> queue     = new Queue<>();
    private final Stack<Order> stack     = new Stack<>();
    private final OrderList    orderList = new OrderList();

    // Customer operations

    public void showBestSellers() {
        catalog.showBestSellers(5);
    }

    public void showCategories() {
        catalog.showCategories();
    }

    public int browseByCategory(String category) {
        return catalog.showByCategory(category);
    }

    public String getCategoryByIndex(int index) {
        return catalog.getCategoryByIndex(index);
    }

    public int getCategoryCount() {
        return catalog.getCategoryCount();
    }

    public void browseCatalog() {
        catalog.displayAll();
    }

    public void searchCatalog(String keyword) {
        catalog.searchByKeyword(keyword);
    }

    public Book searchBookForOrder(String input) {
        try {
            int  id = Integer.parseInt(input.trim());
            Book b  = catalog.findByID(id);
            if (b == null) System.out.printf("  X No book found with ID %d.%n", id);
            return b;
        } catch (NumberFormatException e) {
            int found = catalog.findByPartialTitle(input.trim());
            if (found == 0) return null;
            if (found == 1) return catalog.findByPartialTitle_first(input.trim());
            return null;
        }
    }

    // Phase 1 — add book to cart (no stock touched)
    public boolean addBookToOrder(Order order, Book book, int qty) {
        if (book == null) return false;
        if (order.addBook(book, qty)) {
            System.out.printf("  + Added: '%s' x%d  (%s each)%n",
                    book.getTitle(), qty, Book.vnd(book.getPrice()));
            return true;
        }
        return false;
    }

    public Order createOrder(User customer, String address) {
        return new Order(customer.getUsername(), address, customer.getUsername());
    }

    // Phase 2 — commit stock and enqueue
    public boolean submitOrder(Order order) {
        if (order.getBooks().isEmpty()) {
            System.out.println("  X Order cancelled — no books added.");
            return false;
        }
        if (!order.commitStock(catalog)) {
            System.out.println("  X Order could not be placed — stock changed. Please review your cart.");
            return false;
        }
        queue.enqueue(order);
        orderList.add(order);
        System.out.printf("%n  [QUEUE] Order #%d submitted. Queue size: %d%n",
                order.getOrderID(), queue.size());
        return true;
    }

    public void viewMyOrders(User customer) {
        orderList.searchByEmail(customer.getUsername());
    }

    public void trackOrder(User customer, int orderID) {
        Order o = orderList.findByID(orderID);
        if (o == null) {
            System.out.println("  X Order not found.");
        } else if (!o.getEmail().equalsIgnoreCase(customer.getUsername())) {
            System.out.println("  X That order does not belong to you.");
        } else {
            o.printDetail();
        }
    }

    // Staff operations

    public Order processNextOrder(User staff) {
        if (!staff.getRole().canProcessOrders()) {
            System.out.println("  X Access denied — Staff or Admin required.");
            return null;
        }
        if (queue.isEmpty()) {
            System.out.println("  X Queue is empty — no pending orders.");
            return null;
        }
        Order order = queue.dequeue();
        order.sortBooks();
        order.setStatus(Order.Status.PROCESSING);
        stack.push(order);
        System.out.printf("  [STACK] Order #%d processed by %s. Stack size: %d%n",
                order.getOrderID(), staff.getUsername(), stack.size());
        return order;
    }

    public Order undoLastProcessed(User staff) {
        if (!staff.getRole().canProcessOrders()) {
            System.out.println("  X Access denied.");
            return null;
        }
        if (stack.isEmpty()) {
            System.out.println("  X Nothing to undo — stack is empty.");
            return null;
        }
        Order top = stack.peek();
        if (!top.canBeUndone()) {
            System.out.printf("  X Cannot undo Order #%d — already %s.%n",
                    top.getOrderID(), top.getStatus());
            return null;
        }
        stack.pop();
        top.restoreStock(catalog);
        top.setStatus(Order.Status.PENDING);
        queue.enqueueAtFront(top);
        System.out.printf("  [UNDO] Order #%d returned to PENDING by %s. Queue size: %d%n",
                top.getOrderID(), staff.getUsername(), queue.size());
        return top;
    }

    public void shipOrder(User staff, int orderID) {
        if (!staff.getRole().canProcessOrders()) {
            System.out.println("  X Access denied."); return;
        }
        Order o = orderList.findByID(orderID);
        if (o == null) { System.out.println("  X Order not found."); return; }
        if (o.getStatus() != Order.Status.PROCESSING) {
            System.out.printf("  X Order #%d is %s — can only ship PROCESSING orders.%n",
                    orderID, o.getStatus());
            return;
        }
        o.setStatus(Order.Status.SHIPPED);
        if (!stack.isEmpty() && stack.peek().getOrderID() == orderID) stack.pop();
        System.out.printf("  [SHIP] Order #%d marked as SHIPPED.%n", orderID);
    }

    public void searchOrderByID(User staff, int orderID) {
        if (!staff.getRole().canViewAllOrders()) {
            System.out.println("  X Access denied."); return;
        }
        Order o = orderList.findByID(orderID);
        if (o == null) System.out.printf("  X Order #%d not found.%n", orderID);
        else           o.printDetail();
    }

    public void searchByCustomerName(User staff, String name) {
        if (!staff.getRole().canViewAllOrders()) {
            System.out.println("  X Access denied."); return;
        }
        orderList.searchByCustomerName(name);
    }

    public void viewAllOrders(User staff) {
        if (!staff.getRole().canViewAllOrders()) {
            System.out.println("  X Access denied."); return;
        }
        orderList.displayAll();
    }

    public void viewQueue(User staff) {
        if (!staff.getRole().canViewAllOrders()) {
            System.out.println("  X Access denied."); return;
        }
        queue.display();
    }

    public void viewStack(User staff) {
        if (!staff.getRole().canViewAllOrders()) {
            System.out.println("  X Access denied."); return;
        }
        stack.display();
    }

    // Admin operations

    public void addBook(User admin, String title, String author,
                        double price, int stock, String category) {
        if (!admin.getRole().canManageCatalog()) {
            System.out.println("  X Admin role required."); return;
        }
        catalog.addBook(new Book(title, author, price, stock, category));
        System.out.printf("  [CATALOG] '%s' added. Catalog size: %d%n", title, catalog.size());
    }

    public void removeBook(User admin, int bookID) {
        if (!admin.getRole().canManageCatalog()) {
            System.out.println("  X Admin role required."); return;
        }
        boolean ok = catalog.removeBook(bookID);
        System.out.println(ok ? "  [CATALOG] Book removed." : "  X Book ID not found.");
    }

    public void displayStatus() {
        System.out.printf("  [STATUS] Queue: %d pending | Stack depth: %d | Total orders: %d%n",
                queue.size(), stack.size(), orderList.size());
    }

    public Catalog getCatalog()                { return catalog; }
    public String  resolveCategory(String inp) { return catalog.resolveCategory(inp); }
}
