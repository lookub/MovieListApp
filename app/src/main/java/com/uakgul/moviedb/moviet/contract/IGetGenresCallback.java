package com.uakgul.moviedb.moviet.contract;

import com.uakgul.moviedb.moviet.model.Genre;

import java.util.List;

public interface IGetGenresCallback {

    void onSuccess(List<Genre> genres);

    void onError(String errorMsg);

}