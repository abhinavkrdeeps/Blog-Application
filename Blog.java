package com.example.abhinav.myblog;

/**
 * Created by Abhinav on 07-09-2017.
 */

public class Blog {
    private String title,description,images,username,comments;
    Blog()
    {

    }
    Blog(String title, String description, String images, String username, String comments)
    {
        this.title=title;
        this.description=description;
        this.images=images;

        this.username = username;
        this.comments = comments;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getDesc() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
