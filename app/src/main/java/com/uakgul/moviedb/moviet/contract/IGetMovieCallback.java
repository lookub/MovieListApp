package com.uakgul.moviedb.moviet.contract;

import com.uakgul.moviedb.moviet.model.Movie;

public interface IGetMovieCallback {

    void onSuccess(Movie movie);

    void onError(String errorMsg);
}
