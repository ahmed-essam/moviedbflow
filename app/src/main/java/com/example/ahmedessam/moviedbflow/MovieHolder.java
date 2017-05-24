package com.example.ahmedessam.moviedbflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView imageView;
    private TextView textView;
    private Context context;

    public MovieHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        imageView = (ImageView) itemView.findViewById(R.id.image);
        textView = (TextView) itemView.findViewById(R.id.text);
        itemView.setOnClickListener(this);
    }

    public void bindView(Movie movie) {
        textView.setText(movie.getTitle());
        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w185/" + movie.getPhotoUrl())
                .into(imageView);

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(context, "item clicked", Toast.LENGTH_SHORT).show();

    }
}
