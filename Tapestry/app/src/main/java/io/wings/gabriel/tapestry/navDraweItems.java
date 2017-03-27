package io.wings.gabriel.tapestry;

/**
 * Created by ABHISHEK on 2/10/2016.
 */
public class navDraweItems {


    private boolean shownotify;
    private String albumId,albumTitle,albumPhotCount;
    private boolean isResentAlbum = false ;
    private boolean active ;


    public boolean isShownotify() {
        return shownotify;
    }

    public void setShownotify(boolean shownotify) {
        this.shownotify = shownotify;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumPhotCount() {
        return albumPhotCount;
    }

    public void setAlbumPhotCount(String albumPhotCount) {
        this.albumPhotCount = albumPhotCount;
    }

    public boolean isResentAlbum() {
        return isResentAlbum;
    }

    public void setResentAlbum(boolean isResentAlbum) {
        this.isResentAlbum = isResentAlbum;
    }

    public navDraweItems() {

    }


    public navDraweItems(boolean shownotify, String albumId, String albumTitle, String albumPhotCount) {
        this.shownotify = shownotify;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.albumPhotCount = albumPhotCount;
    }


    public navDraweItems(String albumId, String albumTitle, String albumPhotCount, boolean isResentAlbum) {
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.albumPhotCount = albumPhotCount;
        this.isResentAlbum = isResentAlbum;
    }
}
