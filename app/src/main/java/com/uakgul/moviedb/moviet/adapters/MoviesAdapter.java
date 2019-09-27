package com.uakgul.moviedb.moviet.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.uakgul.moviedb.moviet.R;
import com.uakgul.moviedb.moviet.contract.IOnClickMoviesCallback;
import com.uakgul.moviedb.moviet.model.Credits;
import com.uakgul.moviedb.moviet.model.Genre;
import com.uakgul.moviedb.moviet.model.Movie;
import com.uakgul.moviedb.moviet.repository.MoviesRepository;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private Context context;

    private List<Movie> movies;

    private List<Genre> allGenres;

    private List<Credits> allCredits;

    private IOnClickMoviesCallback callback;

    // TODO new change
    private Dialog dialog;
    final Handler handler = new Handler();
    float prevX, prevY;

    public MoviesAdapter(Context context, List<Movie> movies, List<Genre> allGenres, IOnClickMoviesCallback callback) {
        this.context = context;
        this.movies = movies;
        this.allGenres = allGenres;
        this.callback = callback;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.item_movie, parent, false);
        return new MovieViewHolder( view );
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind( movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    public void appendMovies(List<Movie> moviesToAppend) {
        this.movies.addAll( moviesToAppend );
        notifyDataSetChanged();
    }

    public void clearMovies() {
        movies.clear();
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView moviePoster;
        TextView releaseDate;
        TextView title;
        TextView rating;
        TextView genres;
        TextView popularity;

        Movie movie;

        public MovieViewHolder(View itemView) {
            super(itemView);

            moviePoster = itemView.findViewById(R.id.item_movie_poster);
            releaseDate = itemView.findViewById(R.id.item_movie_release_date);
            title = itemView.findViewById(R.id.item_movie_title);
            rating = itemView.findViewById(R.id.item_movie_rating);
            genres = itemView.findViewById(R.id.item_movie_genre);
            popularity = itemView.findViewById(R.id.item_movie_popularity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // call IOnClickMoviesCallback.onClick() with selected movie
                    callback.onClick( movie );
                }
            });


                // i do not prefer it
//            moviePoster.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    showQuickView();
//                    return true;
//                }
//            });

            // TODO new change
            moviePoster.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        //Remove handler callbacks if finger is lifted up
//                        handler.removeCallbacks(mLongPressed);

                        Log.d("Adptr", "MotionEvent.ACTION_UP so go hideQuickView..." );
                        hideQuickView();
                        return false;

                    } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        //start the handler if finger is pressed for 50 milliseconds
//                        handler.postDelayed( mLongPressed, 50 );
                        Log.d("Adptr", "MotionEvent.ACTION_DOWN so handler.postDelayed..." );
                        showQuickView();
                        return true;
                    }
                    //Remove handler callbacks for all other motion events
//                    handler.removeCallbacks(mLongPressed);
                    return false;
                }
            });




        }//end of viewHolder

        public void bind(Movie movie) {

            this.movie = movie;

            releaseDate.setText( movie.getReleaseDate().split("-")[0] );
            title.setText( movie.getTitle() );
            rating.setText( String.valueOf( movie.getRating() ) );

            if( movie.getRating() <6 ){
                rating.setTextColor( context.getResources().getColor(R.color.red600) );
            }else if( movie.getRating() <6.8 ){
                rating.setTextColor( context.getResources().getColor(R.color.pink600) );
            }else if( movie.getRating() <7.2 ){
                rating.setTextColor( context.getResources().getColor(R.color.deeporange600) );
            }else if( movie.getRating() <7.8 ){
                rating.setTextColor( context.getResources().getColor(R.color.amber600) );
            }else if( movie.getRating() <8.2 ){
                rating.setTextColor( context.getResources().getColor(R.color.teal600) );
            }else{
                rating.setTextColor( context.getResources().getColor(R.color.green600) );
            }

            genres.setText( getGenres( movie.getGenreIds() ) );

            popularity.setText( String.valueOf( "Popularity : " + movie.getPopularity() ) );

            Glide.with( context )   // or itemView
                    .load( MoviesRepository.IMAGE_BASE_URL + movie.getPosterPath() )
                    .centerInside()
                    .transition( DrawableTransitionOptions.withCrossFade() )
//                    .transform( new RoundedCorners(35) )
                    .apply( RequestOptions.bitmapTransform(new RoundedCorners(10 )) ) // TODO new change
                    .apply( RequestOptions.placeholderOf(R.color.transparent) ) // colorPrimary
                    .into( moviePoster );

//            Log.d("Adapter", "imageUrl : " + MoviesRepository.IMAGE_BASE_URL + movie.getPosterPath() );
//            Log.d("Adapter", "\n___movies("+movie.getId()+") : " + movie.toString() + "_____\n" );

        }



        private String getGenres(List<Integer> movieGenreIds) {

            List<String> movieGenres = new ArrayList<>();

            for (Integer mGenreId : movieGenreIds) {

                for (Genre genre : allGenres) {

                    if ( genre.getId() == mGenreId ) {

                        movieGenres.add( genre.getName() );

                        break;
                    }

                }

            }
            return TextUtils.join(", ", movieGenres );
        }




        // TODO new change
        public void showQuickView(){

            Log.d("Adptr", "showQuickView ..." );

            //Get view of dialog layout
            View dialogLayout = LayoutInflater.from( context).inflate(R.layout.dialog_image_peek_pop, null);
            //Initialize Dialog declared inside CustomRecyclerAdapter
            dialog = new Dialog(context);

            ImageView imageView = dialogLayout.findViewById(R.id.img);
            TextView textView   = dialogLayout.findViewById(R.id.text_img_name);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView( dialogLayout );
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);

            String titleView = movies.get( getAdapterPosition() ).getTitle() + "  ( " + movies.get( getAdapterPosition() ).getRating() + " )";
            textView.setText( titleView );
            Glide.with( context )   // or itemView
                    .load( MoviesRepository.IMAGE_BASE_URL + movies.get( getAdapterPosition() ).getPosterPath() )
                    .centerInside()
                    .transition( DrawableTransitionOptions.withCrossFade() )
                    .apply( RequestOptions.bitmapTransform( new RoundedCorners(10 )) ) // TODO new change
                    .apply( RequestOptions.placeholderOf( R.color.transparent ) ) // colorPrimary
                    .into( imageView );

            dialog.show();

        }

        public void hideQuickView(){
            Log.d("Adptr", "hideQuickView  dialog.dismiss() ..." );
            if (dialog != null) dialog.dismiss();
        }




        // TODO new change
//        Runnable mLongPressed = new Runnable(){
//            public void run(){
//
//                Log.d("Adptr", "Runnable create dialog layout ..." );
//                //Get view of dialog layout
//                View dialogLayout = LayoutInflater.from( context).inflate(R.layout.dialog_image_peek_pop, null);
//                //Initialize Dialog declared inside CustomRecyclerAdapter
//                dialog = new Dialog(context);
//
//                ImageView imageView = dialogLayout.findViewById(R.id.img);
//                TextView textView   = dialogLayout.findViewById(R.id.text_img_name);
//
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setContentView( dialogLayout );
//                dialog.setCanceledOnTouchOutside(true);
//                dialog.setCancelable(true);
//
//                Log.d("Adptr", "Runnable set text and image dialog layout ..." );
//
//                //set image and name of animal in dialog
//                String titleView = movies.get( getAdapterPosition() ).getTitle() + " ( " + movies.get( getAdapterPosition() ).getRating() + " )";
//                textView.setText( titleView );
//                Glide.with( context )   // or itemView
//                        .load( MoviesRepository.IMAGE_BASE_URL + movies.get( getAdapterPosition() ).getPosterPath() )
//                        .centerInside()
//                        .transition( DrawableTransitionOptions.withCrossFade() )
//                        .apply( RequestOptions.bitmapTransform( new RoundedCorners(10 )) ) // TODO new change
//                        .apply( RequestOptions.placeholderOf( R.color.transparent ) ) // colorPrimary
//                        .into( imageView );
//
//                dialog.show();
//            }
//        };







    }//end of MovieViewHolder class





}