package com.example.bookstore.Model;

import java.util.List;

public class GenreItem {
    private Genre genre;
    private List<BookItem> bookItems;

    public GenreItem(Genre genre, List<BookItem> bookItems) {
        this.genre = genre;
        this.bookItems = bookItems;
    }

    public List<BookItem> getBookItems() {
        return bookItems;
    }

    public void setBookItems(List<BookItem> bookItems) {
        this.bookItems = bookItems;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}
