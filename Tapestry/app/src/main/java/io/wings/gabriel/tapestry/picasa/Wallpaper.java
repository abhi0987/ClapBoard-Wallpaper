package io.wings.gabriel.tapestry.picasa;

import java.io.Serializable;

/**
 * Created by ABHISHEK on 2/9/2016.
 */
public class Wallpaper implements Serializable {

    private static final long serialVersionUid = 1L;

    private String url,photoJson;
    private int width , height;

    public Wallpaper( String photoJson, String url,int width,int height) {
        this.width = width;
        this.photoJson = photoJson;
        this.height = height;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotoJson() {
        return photoJson;
    }

    public void setPhotoJson(String photoJson) {
        this.photoJson = photoJson;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
