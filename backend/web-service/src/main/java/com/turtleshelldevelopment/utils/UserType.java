package com.turtleshelldevelopment.utils;

public class UserType {
    private final String name;
    private final int id;

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
