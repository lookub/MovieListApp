package com.uakgul.moviedb.moviet.contract;

import com.uakgul.moviedb.moviet.model.Movie;

import java.util.List;

public interface IGetMoviesCallback {

    void onSuccess(int page, List<Movie> movies);

    void onError(String errorMsg);

}
