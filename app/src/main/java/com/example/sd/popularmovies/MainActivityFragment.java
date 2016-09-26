package com.example.sd.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageButton;
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

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private SharedPreferences pref;

    public MovieArrayAdapter movieArrayAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        PreferenceManager.setDefaultValues(getContext(), R.xml.pref_general, false);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            updateMovies();
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ImageButton previous = (ImageButton) rootView.findViewById(R.id.button_previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previousPage = pref.getInt("page", 1) - 1;
                Log.v(LOG_TAG, String.valueOf(previousPage));
                pref.edit().putInt("page", previousPage).apply();
                updateMovies();
            }
        });
        ImageButton next = (ImageButton) rootView.findViewById(R.id.button_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int nextPage = pref.getInt("page", 1) + 1;
                Log.v(LOG_TAG, String.valueOf(nextPage));
                pref.edit().putInt("page", nextPage).apply();
                updateMovies();
            }
        });

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        movieArrayAdapter = new MovieArrayAdapter(getContext(), new ArrayList<Movie>());
        movieArrayAdapter.notifyDataSetChanged();
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

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        if (isOnline()) {
            new FetchMovies().execute();
        } else {
            Toast toast = Toast.makeText(getContext(),
                    R.string.error_no_internet,
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public void showOrHidePreviousButton(int currentPage){
        ImageButton previous = (ImageButton) getView().findViewById(R.id.button_previous);
        if (currentPage == 1){
            previous.setVisibility(View.GONE);

        } else {
            previous.setVisibility(View.VISIBLE);
        }
    }

    public class FetchMovies extends AsyncTask<String, Void, List<Movie>> {
        List<Movie> moviesList = new ArrayList<>();
        List<MovieDb> movieDBList = new ArrayList<>();
        int page = pref.getInt("page", 1);
        String language = pref.getString(getString(R.string.pref_language_key),
                getString(R.string.pref_language_value_ru));
        String sorting = pref.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_sorting_value_popular));

        @Override
        protected List<Movie> doInBackground(String... params) {
            TmdbMovies movies = new TmdbApi("91ca123680c7da4ae30a546026abae71").getMovies();
            if (sorting.equals(getString(R.string.pref_sorting_value_popular))) {
                movieDBList = movies.getPopularMovies(language, page).getResults();
            }
            if (sorting.equals(getString(R.string.pref_sorting_value_rating))) {
                movieDBList = movies.getTopRatedMovies(language, page).getResults();
            }
            for (MovieDb movieDb : movieDBList) {
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
            showOrHidePreviousButton(page);
        }
    }
}
