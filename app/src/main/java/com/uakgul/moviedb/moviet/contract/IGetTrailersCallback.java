package com.uakgul.moviedb.moviet.contract;

import com.uakgul.moviedb.moviet.model.Trailer;

import java.util.List;

public interface IGetTrailersCallback {

    void onSuccess(List<Trailer> trailers);

    void onError();
}
