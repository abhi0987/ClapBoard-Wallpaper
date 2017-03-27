package io.wings.gabriel.tapestry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ABHISHEK on 2/11/2016.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {
    Context context;
    List<navDraweItems> data = Collections.emptyList();
    MainActivity main;
    int position;

    OnitemClickListener mItemClickListener;

    public RecycleAdapter(Context baseContext, ArrayList<navDraweItems> navDrawerItems) {
         this.context = baseContext;
          data = navDrawerItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_each, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        navDraweItems current = data.get(position);
       // holder.title.setText(current.getAlbumTitle());
        if(position==0)
        {
            holder.title.setText("Recently Added");
            holder.photocount.setText("");
        }else {
            holder.title.setText(current.getAlbumTitle());
            holder.photocount.setText( "("+current.getAlbumPhotCount()+")" );
        }
                setPosition(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition(){
        return position;
    }


    public interface OnitemClickListener{

        void onItemClick(View view, int position);
    }

    public void setOnitemClickListener(final OnitemClickListener onitemClickListener){

        mItemClickListener = (OnitemClickListener) onitemClickListener;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView photocount;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titletext);
            photocount = (TextView) itemView.findViewById(R.id.photocountText);
        }

        @Override
        public void onClick(View v) {
           /* if(mItemClickListener!=null){
                mItemClickListener.onItemClick(v,getPosition());
            }*/



        }
    }
}
