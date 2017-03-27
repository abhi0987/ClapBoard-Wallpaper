package io.wings.gabriel.tapestry;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.wings.gabriel.tapestry.app.AppController;
import io.wings.gabriel.tapestry.gif_wall_service.GIFWallpaperService;
import io.wings.gabriel.tapestry.picasa.Wallpaper;
import io.wings.gabriel.tapestry.utils.PrefManager;
import io.wings.gabriel.tapestry.utils.Utils;

public class Gif_Image_activity extends AppCompatActivity {

    private static final String TAG = Gif_Image_activity.class.getSimpleName();
    public static final String TAG_SEL_IMAGE = "selectedImage";
    private Wallpaper selectedPhoto;
    private ImageView fullImageView;
    private LinearLayout llSetWallpaper, llDownloadWallpaper;
    private TextView textView;
    private Utils utils;
    CircularProgressBar pbLoader;
    private CoordinatorLayout coordinatorLayout;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    public static final int progress_bar_type = 0;
    public ProgressDialog pDialog;
    String myUrl;
    PrefManager pref;
    String idList;
    public File file;
    public static int width,height;
    String CHECK = "TAG";
    String c;
    public static String PATH_GIF = null;


    private static final String TAG_ENTRY = "entry",
            TAG_MEDIA_GROUP = "media$group",
            TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url",
            TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height",TAG_IMG_TITLE="title";
    private String url;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.right_out);
        finish();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){

            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading File.....");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;

        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gif__image_activity);

        pref = new PrefManager(this);

        fullImageView = (ImageView) findViewById(R.id.imgFullscreen);
        textView = (TextView) findViewById(R.id.set_down);
        //  llSetWallpaper = (LinearLayout) findViewById(R.id.llSetWallpaper_gif);
        llDownloadWallpaper = (LinearLayout) findViewById(R.id.llDownloadWallpaper_gif);
        pbLoader = (CircularProgressBar) findViewById(R.id.pbLoader);
        pbLoader.setIndeterminate(true);
        pbLoader.setMax(100);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_gif);
        utils = new Utils(getApplicationContext());


        llDownloadWallpaper.getBackground().setAlpha(70);


        Intent i = getIntent();

        c=i.getStringExtra(CHECK);

        if(c.equals(GifActivity.TAG)){

            textView.setText("Download");
        }else {

            textView.setText("Set As Live Wallpaper");
        }



        selectedPhoto = (Wallpaper) i.getSerializableExtra(TAG_SEL_IMAGE);
        idList = i.getStringExtra("title");

        if (selectedPhoto != null) {
            Log.e(TAG, selectedPhoto.getPhotoJson());
            setUrl(selectedPhoto.getUrl());
            setWidthandHeight(selectedPhoto.getWidth(),selectedPhoto.getHeight());
            adjustImageAspect(selectedPhoto.getWidth(),selectedPhoto.getHeight());

            // fetch photo full resolution image by making another json request
            fetchFullResolutionImage();


            llDownloadWallpaper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new downloadwallpaperTask().execute(getUrl());
                }
            });


        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT).show();
        }
    }


    public void setWidthandHeight(int width, int height) {

        this.width=width;
        this.height=height;
    }

    public static int getWidth(){
        return width;

    }

    public static int getHeight(){
        return height;
    }


    private void fetchFullResolutionImage() {
        String url = selectedPhoto.getUrl();
        Log.e(TAG,url);
        pbLoader.setVisibility(View.VISIBLE);
        //  llSetWallpaper.setVisibility(View.GONE);
        llDownloadWallpaper.setVisibility(View.GONE);
        progressStatus = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus<100){
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pbLoader.setProgress(progressStatus);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();



        try{
            Glide.with(getBaseContext()).load(url).asGif().into(fullImageView);


            llDownloadWallpaper.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, getString(R.string.splash_error), Snackbar.LENGTH_LONG);


            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();

        }
    }

    public void setUrl(String url) {
        this.myUrl = url;
    }

    public String getUrl(){

        return myUrl;
    }





    private class downloadwallpaperTask extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... params) {

            File myDir  = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),pref.getGalleryName());
            myDir.mkdirs();
          /*  Random geneRator = new Random();
            int n =10000;
            n = geneRator.nextInt(n);*/
            String fname = idList ;

            file = new File(myDir,fname);
            if(file.exists())
            //  file.delete();
            {
                PATH_GIF = file.getAbsolutePath();

            }else {

                try {
                    URL nUrl = new URL(params[0]);
                    URLConnection conection = nUrl.openConnection();
                    conection.connect();
                    int lenghtOfFile = conection.getContentLength();
                    InputStream input = new BufferedInputStream(nUrl.openStream(), 8192);

                    FileOutputStream out = new FileOutputStream(file);

                    byte data[] = new byte[1024];
                    long total = 0;
                    int count;

                    while ((count = input.read(data)) != -1) {

                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        out.write(data, 0, count);
                    }

                    out.flush();

                    // closing streams
                    out.close();
                    input.close();


                    PATH_GIF = file.getAbsolutePath();
                    Log.e(TAG, PATH_GIF);


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Error in downloading file", Toast.LENGTH_SHORT).show();
                }
            }




            return PATH_GIF;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissDialog(progress_bar_type);

            if(c.equals(GifActivity.TAG)){
                Snackbar snackbar = Snackbar.make(coordinatorLayout,getBaseContext().getString(R.string.toast_saved).replace("#","\"" +
                        pref.getGalleryName() + "\""),Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();

                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
            }else {

                WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                try {
                    myWallpaperManager.setBitmap(BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.icon_clap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent();
                AppController.getInstance().getPrefManger().set_path(PATH_GIF);


                if(Build.VERSION.SDK_INT > 15){
                    i.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);

                    String p = GIFWallpaperService.class.getPackage().getName();
                    String c = GIFWallpaperService.class.getCanonicalName();
                    i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(p, c));
                    i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);


                }
                else{
                    i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                }


                startActivity(i);
            }







        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(Integer.parseInt(values[0]));
        }
    }



    private void adjustImageAspect(int bWidth, int bHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if (bWidth == 0 || bHeight == 0)
            return;

        int sHeight = 0;

        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sHeight = size.y;
        } else {
            Display display = getWindowManager().getDefaultDisplay();
            sHeight = display.getHeight();
        }

        int new_width = (int) Math.floor((double) bWidth * (double) sHeight / (double) bHeight);
        params.width = new_width;
        params.height = sHeight;

        Log.d(TAG, "Fullscreen image new dimensions: w = " + new_width + ", h = " + sHeight);

        fullImageView.setLayoutParams(params);
    }





    }


