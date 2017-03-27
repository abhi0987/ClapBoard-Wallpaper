package io.wings.gabriel.tapestry;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.wings.gabriel.tapestry.app.AppConst;
import io.wings.gabriel.tapestry.app.AppController;
import io.wings.gabriel.tapestry.picasa.Category;
import io.wings.gabriel.tapestry.picasa.Wallpaper;
import io.wings.gabriel.tapestry.utils.PrefManager;
import io.wings.gabriel.tapestry.utils.TypefaceSpan;
import io.wings.gabriel.tapestry.utils.Utils;

public class GifActivity extends AppCompatActivity {

    public static final String TAG = GifActivity.class.getSimpleName();
    private List<Category> albums;
    // private ProgressBar pbLoader;
    private int columnWidth;
    JsonObjectRequest jsonObjectRequest;
    private int width,height;
    private List<Wallpaper> photosList;
    gifrecycler_adapter adapter;
    float padding;
    private PrefManager pref;
    Utils utils ;
    RecyclerView gridView;
    boolean status = false;
    ArrayList<String> idList ;
    CircularProgressBar circularProgressBar;
    CoordinatorLayout coordinatorLayout;

    //  live wallpaper = 6258945324786264017
    //gif = 6252119805184846913
    public static final String URL_ALBUM_PHOTOS =
            "https://picasaweb.google.com/data/feed/api/user/116771515069011613362/albumid/6252119805184846913?alt=json";

    private static final String TAG_FEED = "feed", TAG_ENTRY = "entry",
            TAG_MEDIA_GROUP = "media$group",
            TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url",
            TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height", TAG_ID = "id",
            TAG_T = "$t",TAG_TITLE = "title";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SpannableString s = new SpannableString("Gif");
        s.setSpan(new TypefaceSpan(this, "AvenirLTStd-Book.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.container_body_gif);

        idList=new ArrayList<String>();
        photosList = new ArrayList<Wallpaper>();

        gridView = (RecyclerView) findViewById(R.id.recycle_view_gif);
        gridView.setVisibility(View.GONE);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.cpldr_gif);
        circularProgressBar.setVisibility(View.VISIBLE);

        pref = new PrefManager(GifActivity.this);

        String url = URL_ALBUM_PHOTOS;
        Log.d(TAG, "Albums request url: " + url);

        utils = new Utils(GifActivity.this);
        Cache cache  = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry  entry = cache.get(url);


        if(entry!=null){

            try{

                String data = new String(entry.data,"UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }catch (Exception e){

            }
        }else {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.d(TAG, "List of photos json reponse: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error: " + error.getMessage());
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

/*        AppController.getInstance().getRequestQueue().getCache().remove(url);
        jsonObjectRequest.setShouldCache(false);*/
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        }



        InitilizeGridLayout();

        adapter = new gifrecycler_adapter(GifActivity.this,photosList,columnWidth,columnWidth);
        gridView.setAdapter(adapter);
        gridView.setLayoutManager(new GridLayoutManager(getBaseContext(),2));
        gridView.addItemDecoration(new SpacesItemDecoration((int) padding));
        adapter.notifyDataSetChanged();
        gridView.addOnItemTouchListener(new RecyclerTouchListener(getBaseContext(), gridView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent i = new Intent(GifActivity.this,Gif_Image_activity.class);
                Wallpaper photo= photosList.get(position);
                i.putExtra(Gif_Image_activity.TAG_SEL_IMAGE,photo);
                i.putExtra("title",idList.get(position));
                i.putExtra("TAG",TAG);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.activity_close_scale);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }


    private void parseJsonFeed(JSONObject response) {

        try {
            JSONArray entry = response.getJSONObject(TAG_FEED).getJSONArray(TAG_ENTRY);
            for(int i =0 ;i<entry.length();i++){

                JSONObject photoObj = (JSONObject) entry.get(i);
                JSONArray mediacontentArry = photoObj.getJSONObject(TAG_MEDIA_GROUP).getJSONArray(TAG_MEDIA_CONTENT);
                if(mediacontentArry.length()>0){

                    JSONObject mediaObj = (JSONObject) mediacontentArry.get(0);
                    String url = mediaObj.getString(TAG_IMG_URL);

                    String photoJson = photoObj.getJSONObject(TAG_ID).getString(TAG_T) + "&imgmax=d";
                    String id = photoObj.getJSONObject(TAG_TITLE).getString(TAG_T);
                    idList.add(id);

                    int width = mediaObj.getInt(TAG_IMG_WIDTH);
                    int height = mediaObj.getInt(TAG_IMG_HEIGHT);

                    setLayout(width,height);

                    Wallpaper p = new Wallpaper(photoJson,url,width,height);

                    photosList.add(p);
                    Log.d(TAG, "Photo: " + url + ", w: " + width + ", h: " + height);

                }
            }



            circularProgressBar.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(GifActivity.this, getString(R.string.msg_unknown_error), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){


            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }





    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }



    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space-1;
            outRect.right = space-1;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    private void setLayout(int width, int height) {

        this.width = width;
        this.height = height;
    }
    private void InitilizeGridLayout() {
        Resources r = getResources();
        padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConst.GRID_PADDING, r.getDisplayMetrics());

        // Column width
        columnWidth = (int) ((utils.getScreenWidth() - ((pref
                .getNoOfGridColumns() + 1) * padding)) / pref
                .getNoOfGridColumns());


    }


}
