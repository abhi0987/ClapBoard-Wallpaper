package io.wings.gabriel.tapestry.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.wings.gabriel.tapestry.app.AppConst;
import io.wings.gabriel.tapestry.picasa.Category;

/**
 * Created by ABHISHEK on 2/9/2016.
 */
public class PrefManager {


    private static final String TAG = PrefManager.class.getSimpleName();


    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context context;



    private static final String PREF_NAME = "WallPaperX";

    private static final String KEY_GOOGLE_USERNAME = "google_username";

    // No of grid columns
    private static final String KEY_NO_OF_COLUMNS = "no_of_columns";

    // Gallery directory name
    private static final String KEY_GALLERY_NAME = "gallery_name";

    // gallery albums key
    private static final String KEY_ALBUMS = "albums";


    private static final String KEY_SERVICE = "gif_service";


    public PrefManager(Context context) {
        this.context=context;
        pref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }

    public void setGoogleUsername(String googleUsername) {
        editor = pref.edit();

        editor.putString(KEY_GOOGLE_USERNAME, googleUsername);

        // commit changes
        editor.commit();
    }


    public String getGoogleUserName() {
        return pref.getString(KEY_GOOGLE_USERNAME, AppConst.PICASA_USER);
    }

    public void setNoOfGridColumns(int columns) {
        editor = pref.edit();

        editor.putInt(KEY_NO_OF_COLUMNS, columns);

        // commit changes
        editor.commit();
    }

    public int getNoOfGridColumns() {
        return pref.getInt(KEY_NO_OF_COLUMNS, AppConst.NUM_OF_COLUMNS);
    }

    public void setGalleryName(String galleryName) {
        editor = pref.edit();

        editor.putString(KEY_GALLERY_NAME, galleryName);

        // commit changes
        editor.commit();
    }

    public String getGalleryName() {
        return pref.getString(KEY_GALLERY_NAME, AppConst.SDCARD_DIR_NAME);
    }


    public void storeCategories(List<Category> albums){

        editor = pref.edit();
        Gson gson = new Gson();

        Log.d(TAG, "Albums: " + gson.toJson(albums));

        editor.putString(KEY_ALBUMS, gson.toJson(albums));

        editor.commit();
    }

    public List<Category> getCategories(){

        List<Category> albums = new ArrayList<Category>();

        if(pref.contains(KEY_ALBUMS)){
            String json = pref.getString(KEY_ALBUMS,null);
            Gson gson = new Gson();
            Category[] albumArry = gson.fromJson(json,Category[].class);
            albums = Arrays.asList(albumArry);
            albums = new ArrayList<Category>(albums);

        }else
            return null;


            List<Category> allAlbums=albums;

            Collections.sort(allAlbums, new Comparator<Category>() {
                @Override
                public int compare(Category lhs, Category rhs) {
                    return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                }
            });

        return allAlbums;

    }

    public void set_path(String path){
        editor = pref.edit();
        editor.putString(KEY_SERVICE,path);
        editor.commit();
    }

    public String get_path(){
        return pref.getString(KEY_SERVICE,null);
    }

    public class CustomComparator implements Comparator<Category> {
        @Override
        public int compare(Category c1, Category c2) {
            return c1.getTitle().compareTo(c2.getTitle());
        }
    }
}
