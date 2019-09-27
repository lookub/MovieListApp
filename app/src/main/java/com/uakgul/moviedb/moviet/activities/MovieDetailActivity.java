package com.uakgul.moviedb.moviet.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.uakgul.moviedb.moviet.R;
import com.uakgul.moviedb.moviet.contract.IGetCreditsCallback;
import com.uakgul.moviedb.moviet.contract.IGetGenresCallback;
import com.uakgul.moviedb.moviet.contract.IGetMovieCallback;
import com.uakgul.moviedb.moviet.contract.IGetReviewsCallback;
import com.uakgul.moviedb.moviet.contract.IGetTrailersCallback;
import com.uakgul.moviedb.moviet.model.Cast;
import com.uakgul.moviedb.moviet.model.Crew;
import com.uakgul.moviedb.moviet.model.Genre;
import com.uakgul.moviedb.moviet.model.Movie;
import com.uakgul.moviedb.moviet.model.Review;
import com.uakgul.moviedb.moviet.model.Trailer;
import com.uakgul.moviedb.moviet.repository.MoviesRepository;
import com.uakgul.moviedb.moviet.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {


    public static String MOVIE_ID = "movie_id";

    private ImageView movieBackdrop;

    private RatingBar movieRating;


    private TextView movieTitle;
    private TextView movieRatingText;
    private TextView movieVoteCount;
    private TextView movieGenres;
    private TextView movieCasts;
    private TextView movieOverview;
    private TextView movieReleaseDate;



    private LinearLayout layoutMovieTrailers;
    private LinearLayout layoutMoviePictures;
    private LinearLayout layoutMovieReviews;


    private MoviesRepository moviesRepository;
    private int movieId;

    private Movie currentMovie = new Movie();


    CircularProgressDrawable progressDrawable;

    private FloatingActionButton fab = null;

    private DBHelper sqliteDB ;
    private boolean movieFavState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieId = getIntent().getIntExtra( MOVIE_ID, movieId );

        initUI();

        getMovie();

        setupToolbar();


        sqliteDB.numberOfRows();
        sqliteDB.getAllMovies();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( !movieFavState ){
                    boolean insertStatusFav = sqliteDB.insertMovie( currentMovie );
                    Log.e("Detail", "FAB.onClick() : getMovieFromID. " + movieId + "' movieFav state = FALSE so go to INSERT...");
                    if( insertStatusFav ){
                        fab.setImageResource(R.drawable.star_true);
                        showWarning( currentMovie.getTitle() + " Favorilere Eklendi." );
                        movieFavState = true;
                    }else{
                        fab.setImageResource(R.drawable.star_false);
                        showError( "Bir Hata Oluştu ! " + currentMovie.getTitle() + " Favorilere Eklenemedi!" );
                    }

                }else{

                    Log.e("Detail", "FAB.onClick() : getMovieFromID. " + movieId + "' movieFav state = TRUE so go to DELETE...");
                    int deleteMovieFav = sqliteDB.deletetMovieFromID( movieId );

                    if( deleteMovieFav == 1 ){
                        fab.setImageResource(R.drawable.star_false);
                        showWarning( currentMovie.getTitle() + " Favorilerden Silindi." );
                        movieFavState = false;
                    }else{
                        fab.setImageResource(R.drawable.star_true);
                        showError( "Bir Hata Oluştu ! " + currentMovie.getTitle() + " Favorilerden Silinemedi!" );
                    }
                }

            }
        });

    }//end of onCreate



    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setTextToolbar(String title) {
        CollapsingToolbarLayout ctl = findViewById(R.id.collapsingToolbar);
        ctl.setTitle(title);
    }


    private void initUI() {

        // important
        moviesRepository = MoviesRepository.getInstance();

        movieBackdrop       = findViewById(R.id.ivMovieDetailsBackdrop);
        movieRating         = findViewById(R.id.rbMovieDetailsRating);
        movieTitle          = findViewById(R.id.tvMovieDetailsTitle);
        movieRatingText     = findViewById(R.id.tvMovieDetailsRaiting);
        movieVoteCount      = findViewById(R.id.tvMovieDetailsVoteCount);
        movieGenres         = findViewById(R.id.tvMovieDetailsGenres);
        movieCasts          = findViewById(R.id.tvMovieDetailsCasts);
        movieOverview       = findViewById(R.id.tvMovieDetailsOverview);
        movieReleaseDate    = findViewById(R.id.tvMovieDetailsReleaseDate);
        layoutMovieTrailers = findViewById(R.id.linLMovieTrailers);
        layoutMoviePictures = findViewById(R.id.linLMoviePictures);
        layoutMovieReviews  = findViewById(R.id.lyMovieDetailsReviews);


        progressDrawable = new CircularProgressDrawable(MovieDetailActivity.this  );
        progressDrawable.setStrokeWidth( 5f );
        progressDrawable.setCenterRadius( 30f );
        progressDrawable.start();

        fab = findViewById(R.id.fabFav);
        sqliteDB = new DBHelper( MovieDetailActivity.this );

        if( sqliteDB.getMovieFromID( movieId ) != null ){
            movieFavState = true;
        }
        if( movieFavState ) fab.setImageResource(R.drawable.star_true);

    }//end of initUI


    private void getMovie(){

        moviesRepository.getMovie( movieId, new IGetMovieCallback() {
            @Override
            public void onSuccess(final Movie movie) {

                currentMovie = movie;

                movieTitle.setText( movie.getTitle() );

                movieOverview.setText( movie.getOverview() );

                movieRating.setRating( movie.getRating() / 2 );

                movieRatingText.setText( String.valueOf( movie.getRating() ) );

                String voteCount = "( " + String.valueOf( movie.getVote_count() ) + " Votes )";
                movieVoteCount.setText( voteCount );

                setColorRatingLevel( movie.getRating() );

                movieReleaseDate.setText( movie.getReleaseDate() );

                getGenres( movie ); // movieGenres setText in there

                getCredits( movie ); // movieCredits setText in there

                getTrailers( movie ); // getTrailers place in there

                getReviews( movie ); // getReviews setText in there

                showImages();

                final String backDropUrl = MoviesRepository.IMAGE_BASE_URL + movie.getBackdrop();

                if (!isFinishing()) {

                    Glide.with( MovieDetailActivity.this )
                            .load( backDropUrl )
                            .fitCenter()
                            .transition( DrawableTransitionOptions.withCrossFade() )
                            .apply( RequestOptions.placeholderOf( progressDrawable ) )
//                            .apply( RequestOptions.placeholderOf(R.color.colorPrimary) )
                            .into( movieBackdrop );

                }

                movieBackdrop.requestLayout();
                movieBackdrop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPosterOnUrl( backDropUrl );
                    }
                });


//                Log.d("Detail", "getMovie() : getPosterPath : " + MoviesRepository.IMAGE_BASE_URL + movie.getPosterPath() );
//                Log.d("Detail", "getMovie() : getBackdrop   : " + MoviesRepository.IMAGE_BASE_URL + movie.getBackdrop() );

//                setTextToolbar( movie.getTitle() );

            }

            @Override
            public void onError(String errorMsg) {
//                showError("Getting some errors when getMovie()!! Check your network connection and API method might help." );
                showError("Getting some errors when getMovie()! : " + errorMsg );
                finish();
            }
        });
    }//end of getMovie


    private void getGenres(final Movie movie ){

        moviesRepository.getGenres(new IGetGenresCallback() {
            @Override
            public void onSuccess(List<Genre> genres) {

                if( movie.getGenres() != null ){

                    List<String> currentGenres = new ArrayList<>();

                    for (Genre genre : movie.getGenres()) {
                        currentGenres.add( genre.getName() );
                    }

                    movieGenres.setText( TextUtils.join(", ", currentGenres ) );

                }else{
                    showError( movie.getTitle() + " movie has not any Genres! " );
                }
            }

            @Override
            public void onError(String errorMsg) {
//                showError("Getting some errors when getMovie()!! Check your network connection and API method might help." );
                showError("Getting some errors when getGenres()! : " + errorMsg );
            }
        });
    }//end of Genres


    private void getCredits(final Movie movie ){

        moviesRepository.getCredits( movie.getId(), new IGetCreditsCallback() {
            @Override
            public void onSuccess(List<Cast> casts, List<Crew> crews) {

                    if( casts != null ){

                        List<String> currentCasts = new ArrayList<>();

                        for( Cast mCast : casts ){
                            currentCasts.add( mCast.getName() );
                        }

                        movieCasts.setText( TextUtils.join(", ", currentCasts ) );

                    }else{
                        showError( movie.getTitle() + " movie has not Casts! " );
                    }

            }

            @Override
            public void onError() {
                showError("Getting some errors when getCredits()!! Check your network connection and API method might help." );
            }
        });
    }//end of getCredits



    private void getTrailers(final Movie movie ){

        moviesRepository.getTrailers( movie.getId(), new IGetTrailersCallback() {
            @Override
            public void onSuccess(List<Trailer> trailers) {

                layoutMovieTrailers.removeAllViews();

                for( final Trailer trailer : trailers ){

                    View parent = getLayoutInflater().inflate(R.layout.item_movie_thumbnail_trailer, layoutMovieTrailers, false);

                    ImageView ivThumbnail = parent.findViewById(R.id.iv_thumbnail);

                    ivThumbnail.requestLayout();

                    ivThumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTrailerOnYoutube( String.format( MoviesRepository.YOUTUBE_VIDEO_URL, trailer.getKey() ) );
                        }
                    });
                    Glide.with( MovieDetailActivity.this)
                            .load( String.format( MoviesRepository.YOUTUBE_THUMBNAIL_URL, trailer.getKey() ) )
                            .centerInside()
                            .transition( DrawableTransitionOptions.withCrossFade() )
                            .apply( RequestOptions.placeholderOf(R.color.colorPrimary).centerCrop() )
//                            .apply( RequestOptions.placeholderOf( progressDrawable ) )
                            .into( ivThumbnail );

                    layoutMovieTrailers.addView( parent );
                }
            }

            @Override
            public void onError() {
                showError("Getting some errors when getTrailers()!! Check your network connection and API method might help." );
            }
        });

    }//end of getTrailers

    // null exception
    private void showImages(){

        layoutMoviePictures.removeAllViews();

        if( currentMovie.getPosterPath() != null ){

            // this is not work here String.format( MoviesRepository.IMAGE_BASE_URL, currentMovie.getPosterPath() ) )
            final String posterUrl = MoviesRepository.IMAGE_BASE_URL + currentMovie.getPosterPath();

            View parent = getLayoutInflater().inflate(R.layout.item_movie_image, layoutMoviePictures, false);
            ImageView ivImage= parent.findViewById(R.id.iv_image);

            ivImage.requestLayout();
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPosterOnUrl( posterUrl );
                }
            });

            Glide.with( MovieDetailActivity.this)
                    .load( posterUrl )
                    .fitCenter()
                    .transition( DrawableTransitionOptions.withCrossFade() )
                    .apply( RequestOptions.placeholderOf(R.color.colorPrimary).centerCrop() )
//                    .apply( RequestOptions.placeholderOf( progressDrawable ) )
                    .into( ivImage );

            layoutMoviePictures.addView( parent );

        }

    }//end of showImages



    private void showTrailerOnYoutube(String url) {
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
        startActivity( intent );
    }


    private void showPosterOnUrl(String url) {
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
        startActivity( intent );
    }



    private void getReviews(Movie movie) {

        moviesRepository.getReviews( movie.getId(), new IGetReviewsCallback() {
            @Override
            public void onSuccess( List<Review> reviews) {

                layoutMovieReviews.removeAllViews();

                for ( Review review : reviews ) {

                    View parent = getLayoutInflater().inflate(R.layout.item_movie_review, layoutMovieReviews, false);

                    TextView revAuthor  = parent.findViewById(R.id.reviewAuthor);
                    TextView revContent = parent.findViewById(R.id.reviewContent);

                    revAuthor.setText( review.getAuthor() );
                    revContent.setText( review.getContent() );

                    layoutMovieReviews.addView(parent);
                }
            }

            @Override
            public void onError() {
                showError("Getting some errors when getReviews()!! Check your network connection and API method might help." );
        }
        });
    }






    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




    public void setColorRatingLevel(Float raiting) {

        if( raiting <6 ){
            movieRating.setProgressTintList(ColorStateList.valueOf(MovieDetailActivity.this.getResources().getColor(R.color.red600)));
            movieRatingText.setTextColor( MovieDetailActivity.this.getResources().getColor(R.color.red600) );
        }else if( raiting <6.8 ){
            movieRating.setProgressTintList(ColorStateList.valueOf(MovieDetailActivity.this.getResources().getColor(R.color.pink600)));
            movieRatingText.setTextColor( MovieDetailActivity.this.getResources().getColor(R.color.pink600) );
        }else if( raiting <7.2 ){
            movieRating.setProgressTintList(ColorStateList.valueOf(MovieDetailActivity.this.getResources().getColor(R.color.deeporange600)));
            movieRatingText.setTextColor( MovieDetailActivity.this.getResources().getColor(R.color.deeporange600) );
        }else if( raiting <7.8 ){
            movieRating.setProgressTintList(ColorStateList.valueOf(MovieDetailActivity.this.getResources().getColor(R.color.amber600)));
            movieRatingText.setTextColor( MovieDetailActivity.this.getResources().getColor(R.color.amber600) );
        }else if( raiting <8.2 ){
            movieRating.setProgressTintList(ColorStateList.valueOf(MovieDetailActivity.this.getResources().getColor(R.color.teal600)));
            movieRatingText.setTextColor( MovieDetailActivity.this.getResources().getColor(R.color.teal600) );
        }else{
            movieRating.setProgressTintList(ColorStateList.valueOf(MovieDetailActivity.this.getResources().getColor(R.color.green600)));
            movieRatingText.setTextColor( MovieDetailActivity.this.getResources().getColor(R.color.green600) );
        }

    }



    private void showError(String msg) {
        Toast.makeText( MovieDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
    private void showWarning(String msg) {
        Toast.makeText( MovieDetailActivity.this, msg, Toast.LENGTH_SHORT ).show();
    }

}