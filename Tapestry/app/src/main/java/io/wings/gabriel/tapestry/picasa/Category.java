package io.wings.gabriel.tapestry.picasa;

/**
 * Created by ABHISHEK on 2/9/2016.
 */
public class Category {

    String id;
    String title;
    String photoNo;

    public Category(){

    }

    public Category(String id, String title, String photoNo) {
        this.id = id;
        this.title = title;
        this.photoNo = photoNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoNo() {
        return photoNo;
    }

    public void setPhotoNo(String photoNo) {
        this.photoNo = photoNo;
    }



}
