package com.example.dora.huawei;

/**
 * Individual NavigationDrawer menu item. This is a basic data model.
 */
public class NavigationDrawerMenuItem {
    private int imageId;
    private String title;

    public NavigationDrawerMenuItem(int imageId, String title) {
        this.imageId = imageId;
        this.title = title;
    }

    public NavigationDrawerMenuItem() {
        // empty constructor
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
