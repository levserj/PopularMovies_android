package com.example.sd.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.sd.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by SD on 09.09.2016.
 */
public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    public static final String LOG_TAG = MovieArrayAdapter.class.getSimpleName();

    public MovieArrayAdapter(Context context, List<Movie> movieDbList) {
        super(context, 0, movieDbList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        ImageView imageView;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item_layout, parent, false);
            imageView = (ImageView) convertView.findViewById(R.id.grid_item_image);
        } else {
            imageView = (ImageView) convertView;
        }
        String url = "http://image.tmdb.org/t/p/w342/" + movie.getMovieDb().getPosterPath();
        Picasso.with(getContext()).load(url).into(imageView);
        return imageView;
    }
}
