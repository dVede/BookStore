package com.example.bookstore.Model;

public class Genre {
    private int id;
    private Integer parentId;
    private String genre;

    public Genre(int id, Integer parentId, String genre) {
        this.id = id;
        this.parentId = parentId;
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
