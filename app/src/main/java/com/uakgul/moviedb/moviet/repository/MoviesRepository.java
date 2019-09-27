package com.uakgul.moviedb.moviet.repository;

import android.util.Log;

import com.uakgul.moviedb.moviet.BuildConfig;
import com.uakgul.moviedb.moviet.contract.IGetCreditsCallback;
import com.uakgul.moviedb.moviet.contract.IGetGenresCallback;
import com.uakgul.moviedb.moviet.contract.IGetMovieCallback;
import com.uakgul.moviedb.moviet.contract.IGetMoviesCallback;
import com.uakgul.moviedb.moviet.apiService.IApi;
import com.uakgul.moviedb.moviet.contract.IGetReviewsCallback;
import com.uakgul.moviedb.moviet.contract.IGetTrailersCallback;
import com.uakgul.moviedb.moviet.model.Movie;
import com.uakgul.moviedb.moviet.presenter.CreditsResponse;
import com.uakgul.moviedb.moviet.presenter.GenresResponse;
import com.uakgul.moviedb.moviet.presenter.MoviesResponse;
import com.uakgul.moviedb.moviet.presenter.ReviewResponse;
import com.uakgul.moviedb.moviet.presenter.TrailerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesRepository {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String API_KEY  = BuildConfig.TMDB_API_KEY;

    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";

    public static final String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch?v=%s"; // stringFormat ket for %s
    public static final String YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/%s/0.jpg"; //

    public static final String IMDB_PAGE_URL = "https://www.imdb.com/title/%s";
    public static final String IMDB_CAST_PAGE_URL = "https://www.imdb.com/name/%s";

    private static final String LANGUAGE = "en-US"; // tr-TR



    private static MoviesRepository repository;

    private int currentPage = 1;

    private IApi api;

    private MoviesRepository(IApi api) {
        this.api = api;
    }

    public static MoviesRepository getInstance() {

        if (repository == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl( BASE_URL )
                    .addConverterFactory( GsonConverterFactory.create() )
                    .build();

            repository = new MoviesRepository( retrofit.create( IApi.class ) );
        }

        return repository;
    }//end of getInstance


    public void getMovies(int page, String showBy, final IGetMoviesCallback callback) {

        Callback<MoviesResponse> call = new Callback<MoviesResponse>() {
//        api.getPopularMovies( API_KEY , LANGUAGE, page ).enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                        if ( response.isSuccessful() ) {

                            MoviesResponse moviesResponse = response.body();

                            if (moviesResponse != null && moviesResponse.getMovies() != null) {
                                callback.onSuccess( moviesResponse.getPage() , moviesResponse.getMovies() );
                            } else {
                                callback.onError( "getting some error movies response is NULL" );
                            }

                        } else {
                            callback.onError( "getting some error with getMovies() API : " + response.errorBody() );
                        }

                    }

                    @Override
                    public void onFailure(Call<MoviesResponse> call, Throwable t) {
                        Log.e("MRepo", "...getMovies() : onFailure() : Exception Message : " + t.getMessage() );
                        callback.onError( "getting some error getMovies()  : " + t.getMessage() );
                    }
//                });
        };

        switch ( showBy ) {

            case ServiceConstants.TOP_RATED:
                api.getTopRatedMovies( API_KEY, LANGUAGE, page ).enqueue(call);
                break;

            case ServiceConstants.LATEST:
                api.getLatestMovies( API_KEY, LANGUAGE, page ).enqueue(call);
                break;

            case ServiceConstants.UPCOMING:
                api.getUpcomingMovies( API_KEY, LANGUAGE, page ).enqueue(call);
                break;

            case ServiceConstants.NOW_PLAYING:
                api.getNowPlayingMovies( API_KEY, LANGUAGE, page ).enqueue(call);
                break;

            default:
                api.getPopularMovies( API_KEY, LANGUAGE, page ).enqueue(call);
                break;
        }


    }//end of getMovies


    public void getGenres(final IGetGenresCallback callback) {

        api.getGenres( API_KEY, LANGUAGE ).enqueue(new Callback<GenresResponse>() {
                    @Override
                    public void onResponse(Call<GenresResponse> call, Response<GenresResponse> response) {

                        if ( response.isSuccessful() ) {

                            GenresResponse genresResponse = response.body();

                            if (genresResponse != null && genresResponse.getGenres() != null) {
                                callback.onSuccess( genresResponse.getGenres() );
                            } else {
                                callback.onError( "getting some error genres response is NULL" );
                            }

                        } else {
                            callback.onError( "getting some error with getGenres() API : " + response.errorBody() );
                        }

                    }

                    @Override
                    public void onFailure(Call<GenresResponse> call, Throwable t) {
                        Log.e("MRepo", "...getGenres() : onFailure() : Exception Message : " + t.getMessage() );
                        callback.onError( "getting some error getGenres()  : " + t.getMessage() );
                    }
                });
    }//end of getGenres


    public void getMovie(int movieId, final IGetMovieCallback callback) {

        api.getMovie( movieId, API_KEY, LANGUAGE ).enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {

                        if ( response.isSuccessful() ) {

                            Movie movie = response.body();

                            if (movie != null) {
                                callback.onSuccess( movie );
                            } else {
                                callback.onError( "getting some error movie response is NULL" );
                            }
                        } else {
                            Log.e("MRepo", "...getMovie() : movie is NULL : getting some error with API" );
                            callback.onError( "getting some error with getMovies() API : " + response.errorBody() );
                        }

                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        Log.e("MRepo", "...getMovie() : onFailure() : Exception Message : " + t.getMessage() );
                        callback.onError( "getting some error getMovie()  : " + t.getMessage() );
                    }
                });
    }//end of getMovie



    public void getCredits(int movie_id, final IGetCreditsCallback callback) {

        api.getCredits( movie_id, API_KEY, LANGUAGE ).enqueue(new Callback<CreditsResponse>() {

            @Override
            public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {

                if (response.isSuccessful()) {

                    CreditsResponse creditsResponse = response.body();

                    if (creditsResponse != null && creditsResponse.getCast() != null) {
                        callback.onSuccess( creditsResponse.getCast(), creditsResponse.getCrew() );
                    } else {
                        callback.onError();
                    }

                } else {
                    callback.onError();
                }

            }

            @Override
            public void onFailure(Call<CreditsResponse> call, Throwable t) {
                Log.e("MRepo", "...getCredits() : onFailure() : Exception Message : " + t.getMessage() );
                callback.onError();
            }
        });
    }//end of getCredits



    public void getTrailers(int movie_id, final IGetTrailersCallback callback) {

        api.getTrailers( movie_id, API_KEY, LANGUAGE ).enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response.isSuccessful()) {

                    TrailerResponse trailerResponse = response.body();

                    if( trailerResponse != null && trailerResponse.getTrailers() != null ){

                        callback.onSuccess( trailerResponse.getTrailers() );

                    }else {
                        callback.onError();
                    }

                } else {
                    callback.onError();
                }

            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.e("MRepo", "...getTrailers() : onFailure() : Exception Message : " + t.getMessage() );
            }
        });

    }//end of getTrailers



    public void getReviews( int movieId, final IGetReviewsCallback callback ) {

        api.getReviews( movieId, API_KEY, LANGUAGE ).enqueue(new Callback<ReviewResponse>() {
                    @Override
                    public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {

                        if ( response.isSuccessful() ) {

                            ReviewResponse reviewResponse = response.body();

                            if (reviewResponse != null && reviewResponse.getReviews() != null) {

                                callback.onSuccess( reviewResponse.getReviews() );

                            } else {
                                callback.onError();
                            }
                        } else {
                            callback.onError();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewResponse> call, Throwable t) {
                        Log.e("MRepo", "...getReviews() : onFailure() : Exception Message : " + t.getMessage() );
                        callback.onError();
                    }
                });
    }//end of getReviews





    public void getSearchedMovies(int page, String query, final IGetMoviesCallback callback) {

        api.getSearchedMovies( API_KEY, LANGUAGE, page, query ).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                if ( response.isSuccessful() ) {

                    MoviesResponse moviesResponse = response.body();

                    if (moviesResponse != null && moviesResponse.getMovies() != null) {
                        callback.onSuccess( moviesResponse.getPage() , moviesResponse.getMovies() );
                    } else {
                        callback.onError( "getting some error searched movies response is NULL" );
                    }

                } else {
                    callback.onError( "getting some error with getSearchedMovies() API : " + response.errorBody() );
                }

            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.e("MRepo", "...getSearchedMovies() : onFailure() : Exception Message : " + t.getMessage() );
                callback.onError( "getting some error getSearchedMovies()  : " + t.getMessage() );
            }
        });

    }//end of getSearchedMovies



    public void getSortedMovies(int page, String sort_by, final IGetMoviesCallback callback) {

        api.getSortedMovies( API_KEY, LANGUAGE, page, sort_by ).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                if ( response.isSuccessful() ) {

                    MoviesResponse moviesResponse = response.body();

                    if (moviesResponse != null && moviesResponse.getMovies() != null) {
                        callback.onSuccess( moviesResponse.getPage() , moviesResponse.getMovies() );
                    } else {
                        callback.onError( "getting some error sorted movies response is NULL" );
                    }

                } else {
                    callback.onError( "getting some error with getSortedMovies() API : " + response.errorBody() );
                }

            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.e("MRepo", "...getSortedMovies() : onFailure() : Exception Message : " + t.getMessage() );
                callback.onError( "getting some error getSortedMovies()  : " + t.getMessage() );
            }
        });

    }//end of getSortedMovies





    public void getFilteredMovies(int page, String sort_by, int filterGenreID, final IGetMoviesCallback callback) {

        api.getFilteredMovies( API_KEY, LANGUAGE, page, sort_by, filterGenreID ).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                if ( response.isSuccessful() ) {

                    MoviesResponse moviesResponse = response.body();

                    if (moviesResponse != null && moviesResponse.getMovies() != null) {
                        callback.onSuccess( moviesResponse.getPage() , moviesResponse.getMovies() );
                    } else {
                        callback.onError( "getting some error filtered movies response is NULL" );
                    }

                } else {
                    callback.onError( "getting some error with getFilteredMovies() API : " + response.errorBody() );
                }

            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.e("MRepo", "...getFilteredMovies() : onFailure() : Exception Message : " + t.getMessage() );
                callback.onError( "getting some error getFilteredMovies()  : " + t.getMessage() );
            }
        });

    }//end of getFilteredMovies



}
