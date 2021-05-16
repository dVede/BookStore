package com.example.bookstore.Model;

import java.util.List;

public class AuthorItem {
    private Author author;
    private List<BookItem> bookItems;

    public AuthorItem(Author author, List<BookItem> bookItems) {
        this.author = author;
        this.bookItems = bookItems;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<BookItem> getBookItems() {
        return bookItems;
    }

    public void setBookItems(List<BookItem> bookItems) {
        this.bookItems = bookItems;
    }
}
