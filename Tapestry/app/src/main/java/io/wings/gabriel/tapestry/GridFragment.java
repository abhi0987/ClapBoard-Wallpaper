package io.wings.gabriel.tapestry;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import io.wings.gabriel.tapestry.picasa.Wallpaper;
import io.wings.gabriel.tapestry.utils.PrefManager;
import io.wings.gabriel.tapestry.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class GridFragment extends Fragment {

    private static final String TAG = GridFragment.class.getSimpleName();
    private Utils utils;
    private grid_recycle_adapter adapter;
    private RecyclerView gridView;
    private int columnWidth;
    private static final String bundleAlbumId = "albumId";
    private String selectedAlbumId;
    private List<Wallpaper> photosList;
    private PrefManager pref;
    CircularProgressBar circularProgressBar;
    ArrayList<String> idList;
    CoordinatorLayout coordinatorLayout;
    float padding;


    private static final String TAG_FEED = "feed", TAG_ENTRY = "entry",
            TAG_MEDIA_GROUP = "media$group",
            TAG_MEDIA_CONTENT = "media$content", TAG_IMG_URL = "url",
            TAG_IMG_WIDTH = "width", TAG_IMG_HEIGHT = "height", TAG_ID = "id",
            TAG_T = "$t", TAG_TITLE = "title";


    public static GridFragment newInstance(String albumId, CoordinatorLayout coordinatorLayout) {
        GridFragment f = new GridFragment();
        Bundle args = new Bundle();
        args.putString(bundleAlbumId, albumId);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        idList = new ArrayList<String>();
        photosList = new ArrayList<Wallpaper>();
        pref = new PrefManager(getActivity());


        selectedAlbumId = getArguments().getString(bundleAlbumId);
        Log.d(TAG,
                "Selected album id: "
                        + getArguments().getString(bundleAlbumId));
        String url = null;

        {

            url = AppConst.URL_ALBUM_PHOTOS.replace("_PICASA_USER_",
                    pref.getGoogleUserName())
                    .replace("_ALBUM_ID_", selectedAlbumId);
        }

        Log.d(TAG, "Final request url: " + url);

        View view = inflater.inflate(R.layout.fragment_rid, container, false);


        gridView = (RecyclerView) view.findViewById(R.id.grid_view);
        circularProgressBar = (CircularProgressBar) view.findViewById(R.id.cpldr);
        circularProgressBar.setVisibility(View.VISIBLE);
        utils = new Utils(getActivity());


        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);

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
                }
            });

/*        AppController.getInstance().getRequestQueue().getCache().remove(url);
        jsonObjectRequest.setShouldCache(false);*/
            AppController.getInstance().addToRequestQueue(jsonObjectRequest);

        }

            InitilizeGridLayout();

            adapter = new grid_recycle_adapter(getActivity(), photosList, columnWidth);
            gridView.setAdapter(adapter);
            gridView.setLayoutManager(new StaggeredGridLayoutManager( pref.getNoOfGridColumns(),1));

        gridView.addItemDecoration(new SpacesItemDecoration((int) padding));
        gridView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getBaseContext(), gridView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent i = new Intent(getActivity(), FullScreen.class);
                Wallpaper photo = photosList.get(position);
                i.putExtra("title", idList.get(position));
                i.putExtra(FullScreen.TAG_SEL_IMAGE, photo);

                startActivity(i);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.activity_close_scale);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));






        return view;
    }


    private void InitilizeGridLayout() {
        Resources r = getResources();
        padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConst.GRID_PADDING, r.getDisplayMetrics());

        // Column width
        columnWidth = (int) ((utils.getScreenWidth() - ((pref
                .getNoOfGridColumns() + 1) * padding)) / pref
                .getNoOfGridColumns());

        // Setting number of grid columns
     /*   gridView.setNumColumns(pref.getNoOfGridColumns());
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);

        // Setting horizontal and vertical padding
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);*/
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
            outRect.left = space - 1;
            outRect.right = space - 1;
            outRect.bottom = space+1;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    public void parseJsonFeed(JSONObject response) {

        try {
            JSONArray entry = response.getJSONObject(TAG_FEED).getJSONArray(TAG_ENTRY);
            for (int i = 0; i < entry.length(); i++) {

                JSONObject photoObj = (JSONObject) entry.get(i);
                JSONArray mediacontentArry = photoObj.getJSONObject(TAG_MEDIA_GROUP).getJSONArray(TAG_MEDIA_CONTENT);
                if (mediacontentArry.length() > 0) {

                    JSONObject mediaObj = (JSONObject) mediacontentArry.get(0);
                    String url = mediaObj.getString(TAG_IMG_URL);

                    String photoJson = photoObj.getJSONObject(TAG_ID).getString(TAG_T) + "&imgmax=d";

                    String id = photoObj.getJSONObject(TAG_TITLE).getString(TAG_T);
                    idList.add(id);


                    int width = mediaObj.getInt(TAG_IMG_WIDTH);
                    int height = mediaObj.getInt(TAG_IMG_HEIGHT);

                    Wallpaper p = new Wallpaper(photoJson, url, width, height);

                    photosList.add(p);
                    Log.d(TAG, "Photo: " + url + ", w: " + width + ", h: " + height);

                }
            }


            adapter.notifyDataSetChanged();
            //  pbLoader.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            circularProgressBar.setVisibility(View.GONE);
        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(getActivity(), getString(R.string.msg_unknown_error), Toast.LENGTH_LONG).show();

        }

    }

}
