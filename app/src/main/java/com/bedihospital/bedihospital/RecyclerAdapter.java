package com.bedihospital.bedihospital;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Vasu on 28-Oct-17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    Context context;
    LayoutInflater inflater;
    public RecyclerAdapter(Context context) {

        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private int [] images = { R.drawable.pediatric, R.drawable.lap_gynae, R.drawable.new_born};
    String [] text = {"Pediatric: Friendly doctors", "Lap Gynae: Gyno services", "New Born Babies: caring team"};
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_content, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.imageView.setImageResource(images[position]);
        holder.textView.setText(text[position]);

    }

    @Override
    public int getItemCount() {

        return images.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.card_view_image);
            textView = (TextView)itemView.findViewById(R.id.card_view_text);
        }
    }
}
