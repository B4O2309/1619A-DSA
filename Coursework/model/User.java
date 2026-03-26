package Coursework.model;

public class User {
    public enum Role {
        CUSTOMER,
        STAFF,
        ADMIN;

        public boolean canProcessOrders() {
            return this == STAFF || this == ADMIN;
        }

        public boolean canManageCatalog() {
            return this == ADMIN;
        }

        public boolean canViewAllOrders() {
            return this == STAFF || this == ADMIN;
        }
    }

    public final String username;
    public final Role role;

    public User(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() { return username; }
    public Role   getRole()     { return role; }

    @Override
    public String toString() {
        return username + " [" + role + "]";
    }
}
