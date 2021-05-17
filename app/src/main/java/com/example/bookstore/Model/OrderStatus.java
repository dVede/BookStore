package com.example.bookstore.Model;

import org.jetbrains.annotations.NotNull;

public enum OrderStatus {
    WAITING_FOR_PAYMENT("Waiting for payment"),
    IN_PROGRESS("In progress"),
    COMPLETED("Completed"),
    CANCELLED("Canceled");

    private final String name;

    private OrderStatus(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @NotNull
    public String toString() {
        return this.name;
    }

}