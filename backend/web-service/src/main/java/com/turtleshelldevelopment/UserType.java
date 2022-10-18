package com.turtleshelldevelopment;

public class UserType {
    private String name;
    private int id;

    public UserType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }
}
