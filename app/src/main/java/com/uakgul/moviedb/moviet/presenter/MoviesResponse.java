package com.uakgul.moviedb.moviet.presenter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.uakgul.moviedb.moviet.model.Movie;

import java.util.List;

public class MoviesResponse {


    @SerializedName("page")
    @Expose
    private int page;

    @SerializedName("total_results")
    @Expose
    private int totalResults;

    @SerializedName("results")
    @Expose
    private List<Movie> movies;

    @SerializedName("total_pages")
    @Expose
    private int totalPages;


    public MoviesResponse() {
    }

    @Override
    public String toString() {
        return "MovieResponse{" +
                "page=" + page +
                ", totalResults=" + totalResults +
                ", movies=" + movies +
                ", totalPages=" + totalPages +
                '}';
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
