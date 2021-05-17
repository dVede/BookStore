package com.example.bookstore.Model;


import java.util.List;

public class BookItem {
    private String imageUrl, title, isbn;
    private List<Author> authors;
    private double price;
    private int id;

    public BookItem(int id, String imageUrl, String title, String isbn, double price, List<Author> authors) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.price = price;;
        this.isbn = isbn;
        this.authors = authors;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}