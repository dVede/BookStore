
package com.example.bookstore.Model;

public class User {
    private int id;
    private String login;
    private String email;
    private String address;
    private String telephone;
    private String token;

    public User(int id, String login, String email, String address, String telephone, String token) {
        this.id = id;
        this.login = login;
        this.token = token;
        this.email = email;
        this.address = address;
        this.telephone = telephone;
    }

    public User(int id, String login, String token) {
        this.id = id;
        this.login = login;
        this.token = token;
        this.email = null;
        this.address = null;
        this.telephone = null;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
   