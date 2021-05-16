package com.example.bookstore.Model;

public class Book {
    private String isbn, imageUrl, title, year, pages, description;
    private double price;
    private int id, publisherId, numberInStock;

    public Book(String isbn, String imageUrl, String title, String year, String pages,
                int numberInStock, String description, double price, int id, int publisherId) {
        this.isbn = isbn;
        this.imageUrl = imageUrl;
        this.title = title;
        this.year = year;
        this.pages = pages;
        this.numberInStock = numberInStock;
        this.description = description;
        this.price = price;
        this.id = id;
        this.publisherId = publisherId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public int getNumberInStock() {
        return numberInStock;
    }

    public void setNumberInStock(int numberInStock) {
        this.numberInStock = numberInStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }
}
