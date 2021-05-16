package com.example.bookstore.Model;

public class Comment{
    private int bookId, consumerId, commentId;
    private String comment;

    public Comment(int commentId, int bookId, int consumerId, String comment) {
        this.commentId = commentId;
        this.bookId = bookId;
        this.consumerId = consumerId;
        this.comment = comment;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(int consumerId) {
        this.consumerId = consumerId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
}
