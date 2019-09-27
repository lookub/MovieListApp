package com.uakgul.moviedb.moviet.contract;

import com.uakgul.moviedb.moviet.model.Cast;
import com.uakgul.moviedb.moviet.model.Crew;

import java.util.List;

public interface IGetCreditsCallback {

    void onSuccess(List<Cast> casts, List<Crew> crews);

    void onError();

}