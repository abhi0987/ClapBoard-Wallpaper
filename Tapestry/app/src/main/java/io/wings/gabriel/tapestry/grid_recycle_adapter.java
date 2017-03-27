package io.wings.gabriel.tapestry;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import io.wings.gabriel.tapestry.app.AppController;
import io.wings.gabriel.tapestry.picasa.Wallpaper;
import io.wings.gabriel.tapestry.utils.FeedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhis on 2/27/2016.
 */
public class grid_recycle_adapter extends RecyclerView.Adapter<grid_recycle_adapter.gridHolder>{
    private Context _activity;
    private LayoutInflater inflater;
    private List<Wallpaper> wallpapersList = new ArrayList<Wallpaper>();
    private int imageWidth;
    ImageLoader imageLoader ;

    public grid_recycle_adapter(FragmentActivity activity, List<Wallpaper> photosList, int columnWidth) {

        this._activity=activity;
        this.wallpapersList = photosList;
        this.imageWidth = columnWidth;
    }


    @Override
    public gridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(_activity).inflate(R.layout.new_grid_recycle,parent,false);
        gridHolder holder = new gridHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(gridHolder holder, int position) {
        Wallpaper p = wallpapersList.get(position);

        imageLoader = AppController.getInstance().getImageLoader();

        holder.thumbNail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.thumbNail.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth, p.getHeight()));
        //holder.thumbNail.setImageUrl(p.getUrl(), imageLoader);
        Picasso.with(_activity)
                .load(p.getUrl())
                .into(holder.thumbNail);





    }

    @Override
    public int getItemCount() {
        return wallpapersList.size();
    }


    public class gridHolder extends RecyclerView.ViewHolder{

        ImageView thumbNail ;


        public gridHolder(View itemView) {
            super(itemView);
            thumbNail = (ImageView) itemView.findViewById(R.id.thumbnail);

        }
    }
}
