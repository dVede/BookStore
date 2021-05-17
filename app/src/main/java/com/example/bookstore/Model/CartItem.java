package com.example.bookstore.Model;

public class CartItem {
    private BookItem bookItem;
    private int amount;

    public CartItem(BookItem bookItem, int amount) {
        this.bookItem = bookItem;
        this.amount = amount;
    }

    public BookItem getBookItem() {
        return bookItem;
    }

    public void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}