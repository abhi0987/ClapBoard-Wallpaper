package io.wings.gabriel.tapestry.app;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import io.wings.gabriel.tapestry.utils.LruBitmapCache;
import io.wings.gabriel.tapestry.utils.PrefManager;


import java.io.File;

/**
 * Created by ABHISHEK on 2/9/2016.
 */
public class AppController extends Application {

    public  static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    private static AppController mInstance;

    private PrefManager pref;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        pref = new PrefManager(this);

    }

    public static  AppController getInstance(){

        return mInstance;
    }

    public PrefManager getPrefManger() {
        if (pref == null) {
            pref = new PrefManager(this);
        }

        return pref;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

  public ImageLoader getImageLoader(){

      getRequestQueue();
      if(mImageLoader==null){

          getLruBitmapCache();
          mImageLoader = new ImageLoader(this.mRequestQueue,mLruBitmapCache);
      }

      return this.mImageLoader;

  }




    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public void clearCache() {
        long size = 0;
        File cache = getCacheDir();
        //cache.;

        //clear SD cache
        File[] files = getCacheDir().listFiles();
        for (File f : files) {
            size = size + f.length();
            f.delete();
        }
        clearApplicationData();
        Toast.makeText(this, " Cache Cleared!", Toast.LENGTH_SHORT).show();
    }


    public void clearApplicationData()
    {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()){
            String[] childeren = appDir.list();
            for (String s : childeren){
                if(!s.equals("lib"))
                {
                    deleteDir(new File(appDir,s));
                    Log.i("TAG","File /data/data/APP_PACKAGE/" + s + " Deleted!");
                }
            }
        }
    }

    public static boolean deleteDir(File dir)
    {
        if (dir != null && dir.isDirectory()){
            String[] childeren = dir.list();
            for (int i = 0; i < childeren.length; i++) {
                boolean success = deleteDir(new File(dir, childeren[i]));
                if (!success)
                {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    public void getCacheSize() {

        //clear memory cache

        long size = 0;
        //cache.clear();

        //clear SD cache
        File[] files = getCacheDir().listFiles();
        for (File f : files) {
            size = size + f.length();
            // f.delete();
        }

        Toast.makeText(this, size + " Bytes", Toast.LENGTH_SHORT).show();
    }



}
