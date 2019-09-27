package com.uakgul.moviedb.moviet.presenter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.uakgul.moviedb.moviet.model.Genre;

import java.util.List;

public class GenresResponse {

        @SerializedName("genres")
        @Expose
        private List<Genre> genres;

        public List<Genre> getGenres() {
            return genres;
        }

        public void setGenres(List<Genre> genres) {
            this.genres = genres;
        }

    @Override
    public String toString() {
        return "GenresResponse{" +
                "genres=" + genres +
                '}';
    }

}
