package com.example.avcinteractivemapapp;

public class Question {
    private String title;
    private final String description;
    private final boolean hasLink;

    public Question(String title, String description, boolean hasLink) {
        this.title = title;
        this.description = description;
        this.hasLink = hasLink;
    }

    public boolean hasLink() { return hasLink; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
}
