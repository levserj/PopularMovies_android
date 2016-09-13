package com.example.sd.popularmovies.model;

import android.media.Image;
import android.widget.ImageView;

import java.io.Serializable;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by SD on 09.09.2016.
 */
public class Movie implements Serializable {
    private MovieDb movieDb;


    public MovieDb getMovieDb() {
        return movieDb;
    }

    public void setMovieDb(MovieDb movieDb) {
        this.movieDb = movieDb;
    }

    public Movie() {
    }

    public Movie(MovieDb movieDb) {
        this.movieDb = movieDb;
    }
}
