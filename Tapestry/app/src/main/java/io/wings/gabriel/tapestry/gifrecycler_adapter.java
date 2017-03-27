package io.wings.gabriel.tapestry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import io.wings.gabriel.tapestry.picasa.Wallpaper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhis on 2/18/2016.
 */
public class gifrecycler_adapter extends RecyclerView.Adapter<gifrecycler_adapter.MygifHolder> {
    Context context;
    private List<Wallpaper> wallpapersList = new ArrayList<Wallpaper>();

    private int imageWidth;
    private int imageheight;
    public gifrecycler_adapter(Context gif_show_activity, List<Wallpaper> photosList, int width, int height) {
        context = gif_show_activity;
        wallpapersList = photosList;
        imageheight = width;
        imageheight = height;

    }



    @Override
    public MygifHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.gif_recycler,parent,false);
        MygifHolder holder = new MygifHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MygifHolder holder, int position) {


        Wallpaper p = wallpapersList.get(position);
        ImageView img = holder.imageView;
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setLayoutParams(new RelativeLayout.LayoutParams(imageheight,imageheight));


        Picasso.with(context).load(p.getUrl()).into(img);




    }

    @Override
    public int getItemCount() {
        return wallpapersList.size();
    }

    public class MygifHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

       ImageView imageView ;



        public MygifHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.thumb_gif);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
