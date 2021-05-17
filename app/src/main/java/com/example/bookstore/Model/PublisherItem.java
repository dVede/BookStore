package com.example.bookstore.Model;

import java.util.List;

public class PublisherItem {
  private Publisher publisher;
  private List<BookItem> bookItems;

  public PublisherItem(Publisher publisher, List<BookItem> bookItems) {
    this.publisher = publisher;
    this.bookItems = bookItems;
  }

  public List<BookItem> getBookItems() {
    return bookItems;
  }

  public void setBookItems(List<BookItem> bookItems) {
    this.bookItems = bookItems;
  }

  public Publisher getPublisher() {
    return publisher;
  }

  public void setPublisher(Publisher publisher) {
    this.publisher = publisher;
  }
}