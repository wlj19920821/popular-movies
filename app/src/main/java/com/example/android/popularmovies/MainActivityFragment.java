package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        protected Void doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;
            try {
                // Construct the URL for the movie database query
                String baseUrl = "http://api.themoviedb.org/3/discover/movie?";
                String SORT_BY = "sort_by";
                String API_KEY = "api_key";
                Uri movieUrl = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(API_KEY, getString(R.string.Api_key))
                        .build();
                URL uri=new URL(movieUrl.toString());
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
            }

