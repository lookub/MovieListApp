package com.uakgul.moviedb.moviet.contract;

import com.uakgul.moviedb.moviet.model.Review;

import java.util.List;

public interface IGetReviewsCallback {

    void onSuccess(List<Review> reviews);

    void onError();
}
