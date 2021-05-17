package com.example.bookstore.Model;

import java.util.List;

public class BookDetailed {
    private Book book;
    private List<Author> authors;
    private Publisher publisher;
    private Boolean isWishlisted;
    private List<Genre> genres;
    private int votesNumber;
    private float averageRating;

    public BookDetailed(Book book, List<Author> authors, Publisher publisher, List<Genre> genres, Boolean isWishlisted,
                        int votesNumber, float averageRating) {
        this.genres = genres;
        this.isWishlisted = isWishlisted;
        this.book = book;
        this.authors = authors;
        this.publisher = publisher;
        this.votesNumber = votesNumber;
        this.averageRating = averageRating;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<Author> getAuthor() {
        return authors;
    }

    public void setAuthor(List<Author> authors) {
        this.authors = authors;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Boolean getWishlisted() {
        return isWishlisted;
    }

    public void setWishlisted(Boolean wishlisted) {
        isWishlisted = wishlisted;
    }

    public List<Genre> getGenre() {
        return genres;
    }

    public void setGenre(List<Genre> genres) {
        this.genres = genres;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public int getVotesNumber() {
        return votesNumber;
    }

    public void setVotesNumber(int votesNumber) {
        this.votesNumber = votesNumber;
    }
}