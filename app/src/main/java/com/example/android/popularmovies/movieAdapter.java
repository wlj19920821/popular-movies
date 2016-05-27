package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class movieAdapter extends ArrayAdapter<Movie> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_main, null);
        }

        ImageView posterImageView = (ImageView) convertView.findViewById(R.id.details_movie_poster);
        Movie movie = getItem(position);
        String moviePoster = "movie_poster";
        String imageBaseUrl="http://image.tmdb.org/t/p/w185";
        Uri imageUri = Uri.parse(imageBaseUrl).buildUpon()
                .appendPath(moviePoster.substring(1))
                .build();

        Picasso.with(getContext()).load(imageUri)
                .into(posterImageView);

        return convertView;
    }
}
