package com.uakgul.moviedb.moviet.presenter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.uakgul.moviedb.moviet.model.Review;

import java.util.List;

public class ReviewResponse {

    @SerializedName("results")
    @Expose
    private List<Review> reviews;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "ReviewResponse{" +
                "reviews=" + reviews +
                '}';
    }
}
