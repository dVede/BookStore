package com.example.bookstore.Model;

import org.jetbrains.annotations.NotNull;

public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final String name;

    private Role(String s) {
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
