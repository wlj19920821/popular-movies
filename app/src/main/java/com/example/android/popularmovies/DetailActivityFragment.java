package com.example.android.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView detailsTitle = (TextView) rootView.findViewById(R.id.details_movie_title);
        ImageView detailsPoster = (ImageView) rootView.findViewById(R.id.details_movie_poster);
        TextView detailsRating = (TextView) rootView.findViewById(R.id.movie_rating);
        TextView detailsReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        TextView detailsOverview = (TextView) rootView.findViewById(R.id.movie_description);

        String title = getActivity().getIntent().getStringExtra("movie_title");
        String poster = getActivity().getIntent().getStringExtra("movie_poster");
        Integer rating = Integer.parseInt(getActivity().getIntent().getStringExtra("move_rating"));
        String releaseDate = getActivity().getIntent().getStringExtra("release_date");
        String overview = getActivity().getIntent().getStringExtra("movie_overview");

        String imageBaseUrl="http://image.tmdb.org/t/p/w185";
        Uri posterUri = Uri.parse(imageBaseUrl).buildUpon()
                .appendPath(poster.substring(1))
                .build();

        Picasso.with(getActivity()).load(posterUri).into(detailsPoster);
        detailsOverview.setText(overview);
        detailsTitle.setText(title);
        detailsRating.setText(Integer.toString(rating));
        detailsReleaseDate.setText(releaseDate);

        return rootView;
    }
}

