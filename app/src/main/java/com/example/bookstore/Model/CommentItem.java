package com.example.bookstore.Model;

public class CommentItem {
    private Comment comment;
    private String username;

    public CommentItem(Comment comment, String username) {
        this.comment = comment;
        this.username = username;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
