package com.example.sd.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sd.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by SD on 11.09.2016.
 */
public class DetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_activity_frame_view, new DetailFragment())
                    .commit();
        }
    }

    public static class DetailFragment extends Fragment {
        private static final String LOG_TAG = DetailActivity.class.getSimpleName();
        private MovieDb movieDb;


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if (intent != null && intent.hasExtra("movie")) {
                Movie movie = (Movie) intent.getSerializableExtra("movie");
                movieDb = movie.getMovieDb();

                ImageView imageView = ((ImageView) rootView.findViewById(R.id.poster_detail_activity));
                TextView title = (TextView)rootView.findViewById(R.id.title_detail_activity);
                TextView stats = (TextView)rootView.findViewById(R.id.stats_detail_activity);
                TextView description = (TextView)rootView.findViewById(R.id.description_detail_activity);

                String url = "http://image.tmdb.org/t/p/w185/" + movie.getMovieDb().getPosterPath();
                String titleStr = movieDb.getTitle();
                String releaseDate = movieDb.getReleaseDate();
                Float averageVote = movieDb.getVoteAverage();
                String overview = movieDb.getOverview();

                Picasso.with(getContext()).load(url).into(imageView);
                title.setText(titleStr);
                title.setTextSize(25);
                stats.setText("Release Date : " + releaseDate + "\n"
                        + "Rating : " + averageVote);
                description.setText(overview);
            }
            return rootView;
        }
    }
}

