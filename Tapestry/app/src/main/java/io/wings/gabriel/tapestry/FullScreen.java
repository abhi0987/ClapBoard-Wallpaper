package io.wings.gabriel.tapestry;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.wings.gabriel.tapestry.app.AppController;
import io.wings.gabriel.tapestry.picasa.Wallpaper;
import io.wings.gabriel.tapestry.utils.PrefManager;
import io.wings.gabriel.tapestry.utils.Utils;

public class FullScreen extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = FullScreen.class.getSimpleName();
    public static final String TAG_SEL_IMAGE = "selectedImage";
    private Wallpaper selectedPhoto;
    LinearLayout set,down,share;
    private ImageView fullImageView;
    private Utils utils;
    String fullResolutionUrl;
    private CoordinatorLayout coordinatorLayout;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private PrefManager pref;
    CircularProgressBar pbLoader;
    String idList;

    Uri uri;


    private static final String TAG_ENTRY = "entry",
            TAG_MEDIA_GROUP = "media$group",
            TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url",

     TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height",TAG_PHOTO_ID="photoid";


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.right_out);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen);

        set = (LinearLayout) findViewById(R.id.llSetWallpaper);
        down = (LinearLayout) findViewById(R.id.llDownloadWallpaper);
        share = (LinearLayout) findViewById(R.id.llshare);
        fullImageView = (ImageView) findViewById(R.id.imgFullscreen);

        pref =  new PrefManager(getBaseContext());
        ;
        pbLoader = (CircularProgressBar) findViewById(R.id.pbLoader);
        pbLoader.setIndeterminate(true);
        pbLoader.setMax(100);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        utils = new Utils(getApplicationContext());



        set.setOnClickListener(this);
        down.setOnClickListener(this);
        share.setOnClickListener(this);


        set.getBackground().setAlpha(70);
        down.getBackground().setAlpha(70);
        share.getBackground().setAlpha(70);



        Intent i = getIntent();
        selectedPhoto = (Wallpaper) i.getSerializableExtra(TAG_SEL_IMAGE);
        idList = i.getStringExtra("title");

        if (selectedPhoto != null) {

            // fetch photo full resolution image by making another json request
            fetchFullResolutionImage();


        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT).show();
        }


    }


    private void fetchFullResolutionImage() {
        String url = selectedPhoto.getPhotoJson();
        Log.d("URL",url);
        // show loader before making request
        pbLoader.setVisibility(View.VISIBLE);
        set.setVisibility(View.GONE);
        down.setVisibility(View.GONE);
        share.setVisibility(View.GONE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "Image full resolution json: " + response.toString());

                JSONObject entry = null;

                try {
                    entry = response.getJSONObject(TAG_ENTRY);


                    JSONArray mediacontentArry = entry.getJSONObject(
                            TAG_MEDIA_GROUP).getJSONArray(
                            TAG_MEDIA_CONTENT);

                    JSONObject mediaObj = (JSONObject) mediacontentArry.get(0);

                    fullResolutionUrl = mediaObj.getString(TAG_IMG_URL);

                    // image full resolution widht and height
                    final int width = mediaObj.getInt(TAG_IMG_WIDTH);
                    final int height = mediaObj.getInt(TAG_IMG_HEIGHT);


                    Log.d(TAG, "Full resolution image. url: " + fullResolutionUrl + ", w: " + width + ", h: " + height);


                    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


                    progressStatus = 0;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progressStatus < 100) {
                                progressStatus += 1;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbLoader.setProgress(progressStatus);
                                    }
                                });
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();


                    imageLoader.get(fullResolutionUrl, new ImageLoader.ImageListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.msg_wall_fetch_error), Toast.LENGTH_LONG).show();


                        }

                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                            if (response.getBitmap() != null) {
                                // load bitmap into imageview

                                adjustImageAspect(width, height);


                                fullImageView.setImageBitmap(response.getBitmap());

                                // hide loader and show set &
                                // download buttons

                                pbLoader.setVisibility(View.GONE);
                                set.setVisibility(View.VISIBLE);
                                down.setVisibility(View.VISIBLE);
                                share.setVisibility(View.VISIBLE);


                            }
                        }
                    });


                }catch (Exception e){

                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_unknown_error), Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                // unable to fetch wallpaperB
                // either google username is wrong or
                // devices doesn't have internet connection

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.splash_error), Snackbar.LENGTH_LONG);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

                // Toast.makeText(getApplicationContext(), getString(R.string.msg_wall_fetch_error), Toast.LENGTH_LONG).show();
            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);



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

    @Override
    public void onClick(View v) {

        Bitmap bitmap = ((BitmapDrawable) fullImageView.getDrawable()).getBitmap();

        switch (v.getId()) {
            // button Download Wallpaper tapped
            case R.id.llDownloadWallpaper:
                utils.saveImageToSD_CARD(bitmap, coordinatorLayout,idList);
                break;
            // button Set As Wallpaper tapped
            case R.id.llSetWallpaper:
                setWallpaper(bitmap, coordinatorLayout,idList,fullResolutionUrl);
                break;
            case R.id.llshare:
                utils.share_wallPaper(bitmap,coordinatorLayout,idList);
                break;

            default:
                break;
        }

    }



    public void setWallpaper(Bitmap bitmap, CoordinatorLayout coordinatorLayout, String idlist, String list){
        File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),pref.getGalleryName());
        myDir.mkdirs();
      /*  Random geneRators = new Random();
        int n = 10000;
        n = geneRators.nextInt();*/
        //String fname = "Wallpaper-" + n + ".jpg";
        String fname = this.idList;

        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();

        try{
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            uri = getImageContentUri(getBaseContext(),file);

            //  WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseContext());
            Intent intent = null;

            intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Intent chooser_intent = Intent.createChooser(intent,"Set As");
            chooser_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


          /*  Intent viewMediaIntent = new Intent();
            viewMediaIntent.setAction(Intent.ACTION_VIEW);

            viewMediaIntent.setDataAndType(uri, "image*//*");
            viewMediaIntent.putExtra("crop","true");
            viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(viewMediaIntent,1);*/


            try {
                startActivity(chooser_intent);
            } catch (Exception e) {
                //handle error
            }


        }catch (Exception e){
            e.printStackTrace();

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, getBaseContext().getString(R.string.toast_wallpaper_set_failed), Snackbar.LENGTH_SHORT);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }


    public static Uri getImageContentUri(Context context , File imageFile){
        String filePath = imageFile.getAbsolutePath();
        String[] star ={ MediaStore.Images.Media._ID };
        String p = MediaStore.Images.Media.DATA + "=?";
        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, star, p, new String[]{filePath}, null);
        if(cursor!=null && cursor.moveToFirst()){
            int ID = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + ID);
        }else {
            if(imageFile.exists()){
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }else {
                return null;
            }
        }
    }
}
