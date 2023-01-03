package com.example.avcinteractivemapapp;

public class Question {
    private String title;
    private String description;
    private boolean hasLink;
    private boolean expanded;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Question(String title, String description, boolean hasLink) {
        this.title = title;
        this.description = description;
        this.hasLink = hasLink;
        //Whenever a new question is instantiated, we want it to be unexpanded by default
        this.expanded = false;
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

    public void setDescription(String description) {
        this.description = description;
    }
}
