package com.example.sd.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.sd.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private String language;
    private String sorting;

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    public MovieArrayAdapter movieArrayAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(getContext(), R.xml.pref_general, false);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh){
            updateMovies();
        }
        return true;
    }

    private void updateMovies(){
        if (isOnline()){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            language = pref.getString(getString(R.string.pref_language_key),
                    getString(R.string.pref_language_value_ru));
            sorting = pref.getString(getString(R.string.pref_sorting_key),
                    getString(R.string.pref_sorting_value_popular));
            new FetchMovies().execute(language, sorting);
        } else {
            Toast toast  = Toast.makeText(getContext(),
                    R.string.error_no_internet,
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        movieArrayAdapter = new MovieArrayAdapter(getContext(), new ArrayList<Movie>());
        gridView.setAdapter(movieArrayAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie", movieArrayAdapter.getItem(position)));
            }
        });
        return rootView;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public class FetchMovies extends AsyncTask<String, Void, List<Movie>> {
        List<Movie> moviesList = new ArrayList<>();
        List<MovieDb> movieDBList = new ArrayList<>();
        @Override
        protected List<Movie> doInBackground(String... params) {
            TmdbMovies movies = new TmdbApi("91ca123680c7da4ae30a546026abae71").getMovies();

            if (sorting.equals(getString(R.string.pref_sorting_value_popular))) {
                movieDBList = movies.getPopularMovies(language, 1).getResults();
            }
            if (sorting.equals(getString(R.string.pref_sorting_value_rating))) {
                movieDBList = movies.getTopRatedMovies(language, 1).getResults();
            }
            for (MovieDb movieDb : movieDBList){
                moviesList.add(new Movie(movieDb));
            }
            return moviesList;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            if (movieList != null) {
                movieArrayAdapter.clear();
                for (Movie movie : movieList)
                    movieArrayAdapter.add(movie);
            }
        }

    }

}
