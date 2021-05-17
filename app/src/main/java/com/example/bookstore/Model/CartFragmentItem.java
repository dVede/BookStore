package com.example.bookstore.Model;

import java.util.List;

public class CartFragmentItem {
    private List<CartItem> cartItem;
    private double totalPrice;

    public CartFragmentItem(List<CartItem> cartItem, double totalPrice) {
        this.cartItem = cartItem;
        this.totalPrice = totalPrice;
    }

    public List<CartItem> getCartItem() {
        return cartItem;
    }

    public void setCartItem(List<CartItem> cartItem) {
        this.cartItem = cartItem;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}