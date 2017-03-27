package io.wings.gabriel.tapestry;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.wings.gabriel.tapestry.app.AppConst;
import io.wings.gabriel.tapestry.app.AppController;
import io.wings.gabriel.tapestry.picasa.Category;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = SplashActivity.class.getSimpleName();
    Animation textAnim, imgAnim;

    private final String TAG_FEED="feed",TAG_ENTRY="entry",TAG_GPHOTOID = "gphoto$id",TAG_T = "$t"
            ,TAG_ALBUM_TITLE = "title" ,TAG_NO_PHOTOS = "gphoto$numphotos" ;

    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        textAnim = AnimationUtils.loadAnimation(this,R.anim.left_in);
        imgAnim = AnimationUtils.loadAnimation(this,R.anim.right_in);
        textAnim.setDuration(800);
        imgAnim.setDuration(800);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView textView = (TextView) findViewById(R.id.imageView2);




        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_splash);

        String url = AppConst.URL_PICASA_ALBUMS.replace("PICASA_USER_", AppController.getInstance().getPrefManger().getGoogleUserName());
        Log.d(TAG, "Albums request url: " + url);
        JSONObject jsonObject = null;

        imageView.startAnimation(imgAnim);
        textView.startAnimation(textAnim);





        JsonObjectRequest jsonObjreq = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "Albums Response: " + response.toString());
                List<Category> albums = new ArrayList<Category>() ;

                try{
                    JSONArray entry = response.getJSONObject(TAG_FEED).getJSONArray(TAG_ENTRY);
                    for(int i =0 ; i<entry.length();i++){

                        JSONObject albumObj = (JSONObject) entry.get(i);
                        String album_id = albumObj.getJSONObject(TAG_GPHOTOID).getString(TAG_T);
                        String album_titel = albumObj.getJSONObject(TAG_ALBUM_TITLE).getString(TAG_T);
                        String albumNoOFpics = albumObj.getJSONObject(TAG_NO_PHOTOS).getString(TAG_T);

                        Category album = new Category();

                        album.setId(album_id);
                        album.setTitle(album_titel);
                        album.setPhotoNo(albumNoOFpics);

                        albums.add(album);

                        Log.d(TAG, "Album Id: " + album_id + ", Album Title: " + album_titel + " No Of Photos it has " + albumNoOFpics);

                    }

                    AppController.getInstance().getPrefManger().storeCategories(albums);

                   Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();



                }catch (Exception e){

                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(), getString(R.string.msg_unknown_error), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "Volley Error: " + error.getMessage());



                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.splash_error), Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                recreate();
                            }
                        });



                snackbar.setActionTextColor(Color.RED);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

            }

});


        jsonObjreq.setShouldCache(false);

        AppController.getInstance().addToRequestQueue(jsonObjreq);

    }


}