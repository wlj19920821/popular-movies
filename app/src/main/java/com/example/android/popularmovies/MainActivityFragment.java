package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String popularity_param = "popularity.desc";
    private static final String rating_param = "vote_average.desc";
    private String preferred_sort_by = popularity_param;
    public movieAdapter mMovieAdapter;
    private GridView mGridView;

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.movie_gridView);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movieTitle = (String) ((TextView) view
                        .findViewById(R.id.details_movie_title)).getText();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movieTitle);
                startActivity(intent);
            }
        });

        return rootView;

    }

    private void updateMovies() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.movie_sort_key),
                getString(R.string.movie_sort_default));
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute(sortOrder);
    }

    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainfragment, menu);
        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);
        if (preferred_sort_by.contentEquals(popularity_param)) {
            if (!action_sort_by_popularity.isChecked())
                action_sort_by_popularity.setChecked(true);
        } else {
            if (!action_sort_by_rating.isChecked())
                action_sort_by_rating.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_refresh) {
            FetchMovieTask task = new FetchMovieTask();
            task.execute();
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onPostExecute(List<Movie> movies) {

        mMovieAdapter.clear();
        for (Movie movie : movies) {
            mMovieAdapter.add(movie);
        }
    }
}

class FetchMovieTask extends AsyncTask<String, Void, String> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private List<Movie> fetchMovieListFromJson(String JsonStr) throws JSONException {
        JSONObject movieJson = new JSONObject(JsonStr);
        JSONArray movieArray = movieJson.getJSONArray("results");
        List<Movie> results = new ArrayList<>();
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);
            Movie movieModel = new Movie(
                    movie.getInt("id"),
                    movie.getString("original_title"),
                    movie.getString("poster_path"),
                    movie.getString("overview"),
                    movie.getInt("vote_average"),
                    movie.getString("release_date")
            );
            results.add(movieModel);
        }
        return results;
    }

    public class Movie {
        private int id;
        private String title; // original_title
        private String image; // poster_path
        private String overview;
        private int rating; // vote_average
        private String date; // release_date

        public Movie(int id, String title, String image, String overview, int rating, String date) {
            this.id = id;
            this.title = title;
            this.image = image;
            this.overview = overview;
            this.rating = rating;
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getImage() {
            return image;
        }

        public String getOverview() {
            return overview;
        }

        public int getRating() {
            return rating;
        }

        public String getDate() {
            return date;
        }
    }

    protected String doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;
        String Api_key = "Your API KEY";
        try {
            // Construct the URL for the movie database query
            final String baseUrl = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_BY = "sort_by";
            final String API_KEY = "api_key";
            Uri movieUrl = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(SORT_BY, params[0])
                    .appendQueryParameter(API_KEY, Api_key)
                    .build();
            URL uri = new URL(movieUrl.toString());
            urlConnection = (HttpURLConnection) uri.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0) {
                Log.d(LOG_TAG, "length of message 0");
                return null;
            }

            movieJsonStr = buffer.toString();

            Log.d(LOG_TAG, movieJsonStr);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error: " + e.getMessage());

                }
            }
        }

        return null;
    }


}







