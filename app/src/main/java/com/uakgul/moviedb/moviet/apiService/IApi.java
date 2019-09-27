package com.uakgul.moviedb.moviet.apiService;

import android.support.annotation.Nullable;

import com.uakgul.moviedb.moviet.model.Movie;
import com.uakgul.moviedb.moviet.presenter.CreditsResponse;
import com.uakgul.moviedb.moviet.presenter.GenresResponse;
import com.uakgul.moviedb.moviet.presenter.MoviesResponse;
import com.uakgul.moviedb.moviet.presenter.ReviewResponse;
import com.uakgul.moviedb.moviet.presenter.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IApi {

    /** ---------------------------------------------------------------------------------------- **/
    /** Movie Lists API **/
    /** ---------------------------------------------------------------------------------------- **/

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("movie/latest")
    Call<MoviesResponse> getLatestMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("movie/upcoming")
    Call<MoviesResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("movie/now_playing")
    Call<MoviesResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );


    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page,
            @Query("region") String region  // "" - US - TR
    );


    /** ---------------------------------------------------------------------------------------- **/
    /** Movie search API **/
    /** ---------------------------------------------------------------------------------------- **/

    @GET("search/movie")
    Call<MoviesResponse> getSearchedMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page,
            @Query("query") String query
    );


    /** ---------------------------------------------------------------------------------------- **/
    /** Movie filter API **/
    /** ---------------------------------------------------------------------------------------- **/

    @GET("discover/movie")
    Call<MoviesResponse> getFilteredMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page,
            @Query("sort_by") String sort_by,
            @Query("with_genres") int with_genres
    );

    @GET("/trending/{media_type}/{time_window}")
    Call<MoviesResponse> getTrendingMovies(
            @Query("api_key") String apiKey,
            @Query("media_type") String media_type,
            @Query("time_window") String time_window
    );

    /** ---------------------------------------------------------------------------------------- **/
    /** Movie sort API **/
    /** ---------------------------------------------------------------------------------------- **/

    @GET("discover/movie")
    Call<MoviesResponse> getSortedMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page,
            @Query("sort_by") String sort_by
    );

    /**
     *  sort filters
     *
         original_title.asc
         original_title.desc

         release_date.asc
         release_date.desc

         popularity.asc
         popularity.desc

         vote_average.asc
         vote_average.desc

         vote_count.asc
         vote_count.desc

         revenue.asc
         revenue.desc
     */


    /** ---------------------------------------------------------------------------------------- **/
    /** Movie genres API **/
    /** ---------------------------------------------------------------------------------------- **/

    @GET("genre/movie/list")
    Call<GenresResponse> getGenres(
            @Query("api_key") String apiKey,
            @Query("language") String language
    );


    /** ---------------------------------------------------------------------------------------- **/
    /** Movie Details APIs **/
    /** ---------------------------------------------------------------------------------------- **/

    @GET("movie/{movie_id}")
    Call<Movie> getMovie(
            @Path("movie_id") int id,
            @Query("api_key") String apiKEy,
            @Query("language") String language
    );

    @GET("movie/{movie_id}/credits")
    Call<CreditsResponse> getCredits(
            @Path("movie_id") int movie_id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getTrailers(
            @Path("movie_id") int id,
            @Query("api_key") String apiKEy,
            @Query("language") String language
    );


    @GET("movie/{movie_id}/reviews")
    Call<ReviewResponse> getReviews(
            @Path("movie_id") int id,
            @Query("api_key") String apiKEy,
            @Query("language") String language
    );



}
