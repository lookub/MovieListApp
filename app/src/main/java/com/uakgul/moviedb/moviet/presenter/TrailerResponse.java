package com.uakgul.moviedb.moviet.presenter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.uakgul.moviedb.moviet.model.Trailer;

import java.util.List;

public class TrailerResponse {

    @SerializedName("results")
    @Expose
    private List<Trailer> trailers;

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    @Override
    public String toString() {
        return "TrailerResponse{" +
                "trailers=" + trailers +
                '}';
    }
}
