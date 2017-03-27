package io.wings.gabriel.tapestry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.wings.gabriel.tapestry.app.AppController;
import io.wings.gabriel.tapestry.picasa.Category;
import io.wings.gabriel.tapestry.utils.TypefaceSpan;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Category> albumsList;
    private ArrayList<navDraweItems> navDrawerItems;
    DrawerLayout mDrawerLayout;
    RecyclerView recyclerView;
    RecycleAdapter RAdapter;
    ActionBarDrawerToggle mDrawerToggle;
    private boolean stat;
    public static boolean back = true;
    CoordinatorLayout coordinatorLayout;
    int pos;
    Snackbar snackbar;

    private Boolean isFabOpen = false;
   public static FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_main);
        fab= (FloatingActionButton) findViewById(R.id.fab);
        fab1= (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimateFab();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this,GifActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLive = new Intent(MainActivity.this,LIve_wall.class);
                startActivity(intentLive);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });



        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        snackbar = Snackbar
                .make(coordinatorLayout, getString(R.string.msg_wall_fetch_error), Snackbar.LENGTH_INDEFINITE);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navDrawerItems = new ArrayList<navDraweItems>();
        albumsList = AppController.getInstance().getPrefManger().getCategories();

        for (Category a : albumsList) {
            navDrawerItems.add(new navDraweItems(true, a.getId(), a.getTitle(), a.getPhotoNo()));
            // titles a.getTitle()
        }

        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name) {


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                stat = true;
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                stat = false;
                invalidateOptionsMenu();
            }
        };


        Toast.makeText(MainActivity.this, "Slide Right to Show Categories ", Toast.LENGTH_LONG).show();

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();

        RAdapter = new RecycleAdapter(getBaseContext(), navDrawerItems);

        recyclerView.setAdapter(RAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        recyclerView.addItemDecoration(new SpacesItemDecoration(10));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getBaseContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final   int pos = position;
                if(isOnline()){
                    displayView(position);
                    mDrawerLayout.closeDrawers();
                    snackbar.dismiss();

                }else {

                    displayView(position);
                    snackbar = Snackbar
                            .make(coordinatorLayout, getString(R.string.msg_wall_fetch_error), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayView(pos);
                        }
                    });

                    snackbar.setActionTextColor(Color.RED);


                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                    // Toast.makeText(MainActivity.this, "Check for Your Internet Connection !", Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawers();
                }


            }

            @Override
            public void onLongClick(View view, int position) {
                if(isOnline()){
                    displayView(position);
                    mDrawerLayout.closeDrawers();
                    snackbar.dismiss();
                }else {
                    snackbar = Snackbar
                            .make(coordinatorLayout, getString(R.string.msg_wall_fetch_error), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayView(pos);
                        }
                    });

                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                    // Toast.makeText(MainActivity.this, "Check for Your Internet Connection !", Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawers();
                }

            }
        }));


        displayView(0);
    }




    public void AnimateFab(){
  Log.e(TAG,"FAB NAIM");
   if(isFabOpen==true){
          fab.startAnimation(rotate_backward);
          fab1.startAnimation(fab_close);
          fab1.setVisibility(View.INVISIBLE);
          fab2.startAnimation(fab_close);
          fab2.setVisibility(View.INVISIBLE);
          fab1.setClickable(false);
          fab2.setClickable(false);
          isFabOpen = false;
          Log.d("Raj", "close");


    }else {

       fab.startAnimation(rotate_forward);
       fab1.startAnimation(fab_open);
       fab1.setVisibility(View.VISIBLE);
       fab2.startAnimation(fab_open);
       fab2.setVisibility(View.VISIBLE);
       fab1.setClickable(true);
       fab2.setClickable(true);
       isFabOpen = true;
       Log.d("Raj","open");


   }

    }



    private void displayView(int position) {

        Fragment fragment = null;

        setPosition(position);
      /*  switch (position) {
            case 0:
                // Recently added item selected
                // don't pass album id to grid fragment
                Log.e(TAG, "GridFragment is creating");
                fragment = GridFragment.newInstance(null,coordinatorLayout);
                break;

            default:
                // selected wallpaper category
                // send album id to grid fragment to list all the wallpaperB
                String albumId = albumsList.get(position).getId();
                fragment = GridFragment.newInstance(albumId,coordinatorLayout);
                break;
        }*/


        String albumId = albumsList.get(position).getId();
        fragment = GridFragment.newInstance(albumId,coordinatorLayout);

        Log.e(TAG, "GridFragment is creating");



        if (fragment != null) {
            try {


                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();

                //getSupportActionBar().setTitle(albumsList.get(position).getTitle());

           if(position==0){
               SpannableString s = new SpannableString("Recently Added");
               s.setSpan(new TypefaceSpan(this, "AvenirLTStd-Book.otf"), 0, s.length(),
                       Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

               getSupportActionBar().setTitle(s);
           }else {

               SpannableString s = new SpannableString(albumsList.get(position).getTitle());
               s.setSpan(new TypefaceSpan(this, "AvenirLTStd-Book.otf"), 0, s.length(),
                       Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

               getSupportActionBar().setTitle(s);
           }

            } catch (Exception e) {

                e.printStackTrace();
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getString(R.string.msg_wall_fetch_error), Snackbar.LENGTH_LONG);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
                // Toast.makeText(MainActivity.this, "Check for internet connection !", Toast.LENGTH_SHORT).show();


            }
        } else {
            Log.e(TAG, "Error in creating fragment");
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);

                return true;

            case R.id.about_us:
                Intent abtIntent = new Intent(MainActivity.this,About.class);
                startActivity(abtIntent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                return true;
            case R.id.action_rating:
                String ur = "https://play.google.com/store/apps/details?id=io.wings.gabriel.tapestry";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ur));
                startActivity(browserIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }


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
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setPosition(int position) {

        this.pos = position;
    }

    public int getPosition() {


        return pos;
    }



    @Override
    public void onBackPressed() {

        if (stat == true) {
            mDrawerLayout.closeDrawers();
            stat = false;
        } else {

            super.onBackPressed();


        }


    }

}
