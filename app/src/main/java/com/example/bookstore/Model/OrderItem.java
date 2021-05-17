package com.example.bookstore.Model;

import java.util.List;

public class OrderItem {
    private String timestamp;
    private String[] bookIds;
    private OrderStatus status;
    private double totalSum;

    public OrderItem(String[] bookIds, String timestamp,  OrderStatus status, double totalSum) {
        this.timestamp = timestamp;
        this.bookIds = bookIds;
        this.status = status;
        this.totalSum = totalSum;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String[] getBookIds() {
        return bookIds;
    }

    public void setBookIds(String[] bookIds) {
        this.bookIds = bookIds;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(double totalSum) {
        this.totalSum = totalSum;
    }
}