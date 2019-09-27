package com.uakgul.moviedb.moviet.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uakgul.moviedb.moviet.R;
import com.uakgul.moviedb.moviet.adapters.MoviesAdapter;
import com.uakgul.moviedb.moviet.contract.IOnClickMoviesCallback;
import com.uakgul.moviedb.moviet.contract.IGetGenresCallback;
import com.uakgul.moviedb.moviet.contract.IGetMoviesCallback;
import com.uakgul.moviedb.moviet.model.Genre;
import com.uakgul.moviedb.moviet.model.Movie;
import com.uakgul.moviedb.moviet.repository.MoviesRepository;
import com.uakgul.moviedb.moviet.repository.ServiceConstants;
import com.uakgul.moviedb.moviet.utils.DBHelper;
import com.uakgul.moviedb.moviet.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public Context context;

    InputMethodManager imm;

    private ProgressBar progressBar;

    private RecyclerView moviesList;

    private MoviesAdapter adapter;

    private MoviesRepository moviesRepository;



    private int currentPage = 1;

    private List<Genre> movieGenres;


    private String showBy  = ServiceConstants.POPULAR;
    private boolean isLoadingMovies;
    private String showMsg = "POPULAR"; //


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int refreshState = 0;


    SearchView searchView;
    private boolean isSearchingMovies;
    private String searchQuery = null;


    AlertDialog.Builder dialogBuilderSort;
    private boolean isSortingMovies;
    private String sortBy = "";
    private String sortMsg = "";


    AlertDialog.Builder dialogBuilderFilter;
    private boolean isFilteringMovies;
    private int filterBy = 0;
    private String filterMsg = "";
    private String [] filterCategories;

    RelativeLayout rlFilter;
    RelativeLayout rlSort;
    RelativeLayout rlPopular;
    RelativeLayout rlTopRated;
    RelativeLayout rlLatest;
    RelativeLayout rlUpComing;
    RelativeLayout rlNowPlaying;

    RelativeLayout rlNetworkError;

    TextView tvFilter;
    TextView tvSort;
    TextView tvPopular;
    TextView tvTopRated;
    TextView tvLatest;
    TextView tvUpComing;
    TextView tvNowPlaying;

    CardView cvFav;
    boolean isCallingFavorites;
    List<Movie> favMovieList;
    RelativeLayout rlFav;
    TextView tvFav;

    private DBHelper sqliteDB ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        moviesRepository = MoviesRepository.getInstance();

        setupOnScrollListener();

        if( haveNetworkConnection() ){
            getGenres();
        }else{
            hideProgress();
        }

        setBoldTextTypeSelected();

        hideKeyboard();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if( refreshState == 0 )  refreshState = 1;

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if( mSwipeRefreshLayout.isRefreshing() ) {

                            mSwipeRefreshLayout.setRefreshing(false);

                            adapter.clearMovies();

                            if( haveNetworkConnection() ){

                                if( isSearchingMovies ){

                                    getSearchedMovies( currentPage, searchQuery );

                                }else if( isSortingMovies ) {

                                    getSortedMovies(  currentPage, sortBy );

                                }else if( isFilteringMovies ) {

                                    getFilteredMovies( currentPage + 1, filterBy );

                                }else if( isCallingFavorites ) {

                                    getFavoritedMovies();

                                }else{
                                    getMovies( currentPage );
                                }

                            }

                        }

                    }
                }, 1000);

            }
        });

        mSwipeRefreshLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if( refreshState == 1 ){
                    mSwipeRefreshLayout.setRefreshing(false);
                    refreshState=0;
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchQuery = query;

                if( haveNetworkConnection() ){

                    if( movieGenres == null ){
//                        Log.d("Main", "setOnQueryTextListener movieGenres is null so getGenresOnly() " );
                        getGenresOnly();
                    }

                    adapter.clearMovies();
                    currentPage = 1;

                    getSearchedMovies( currentPage, searchQuery );

                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {


                //Clear query
                searchView.setQuery(null, false);

                //Collapse the action view
                searchView.onActionViewCollapsed();

                //Collapse the search widget
//                searchView.collapseActionView();

//                hideKeyboard();

                searchView.clearFocus();

                return false;
            }
        });






    }//ed of onCreate



    @Override
    protected void onResume() {
        super.onResume();
        if( checkDbAndGetFavState() ){
            getFavMovieFromDbAndSetList();
        }
    }//ed of onResume




    private void initUI() {

        context = MainActivity.this;

        mSwipeRefreshLayout = findViewById(R.id.swiperefreshlayout);

        progressBar = findViewById(R.id.progressBar);
        showProgress();

        imm   = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        moviesList = findViewById(R.id.movies_list);
        moviesList.setLayoutManager( new LinearLayoutManager(this) );

        rlFilter     = findViewById(R.id.rlFilter);
        rlSort       = findViewById(R.id.rlSort);
        rlPopular    = findViewById(R.id.rlPopular);
        rlTopRated   = findViewById(R.id.rlTopRated);
        rlLatest     = findViewById(R.id.rlLatest);
        rlUpComing   = findViewById(R.id.rlUpComing);
        rlNowPlaying = findViewById(R.id.rlNowPlaying);

        rlNetworkError = findViewById(R.id.rlNetworkError);

        tvFilter     = rlFilter.findViewById(R.id.textViewFilter);
        tvSort       = rlSort.findViewById(R.id.textViewSort);
        tvPopular    = rlPopular.findViewById(R.id.textViewPopular);
        tvTopRated   = rlTopRated.findViewById(R.id.textViewTopRated);
        tvLatest     = rlLatest.findViewById(R.id.textViewLatest);
        tvUpComing   = rlUpComing.findViewById(R.id.textViewUpcoming);
        tvNowPlaying = rlNowPlaying.findViewById(R.id.textViewNowPlaying);

        rlFilter.setOnClickListener(this);
        rlSort.setOnClickListener(this);
        rlPopular.setOnClickListener(this);
        rlTopRated.setOnClickListener(this);
        rlLatest.setOnClickListener(this);
        rlUpComing.setOnClickListener(this);
        rlNowPlaying.setOnClickListener(this);

        searchView = findViewById(R.id.searchview);

        //default select
        tvPopular.setTypeface( Typeface.DEFAULT_BOLD );



        cvFav = findViewById(R.id.cardViewFav);
        rlFav = findViewById(R.id.rlFav);
        tvFav = rlFav.findViewById(R.id.tvFav);

        rlFav.setOnClickListener(this);

        sqliteDB = new DBHelper( MainActivity.this );
        favMovieList = new ArrayList<>();

    }//end of initUI


    public void onClick(View view) {

        currentPage = 1;

        isSearchingMovies = false;

        searchView.setQuery(null, false);
        searchView.clearFocus();


        if( haveNetworkConnection() ){

            if( movieGenres == null ){
//                Log.d("Main", "onClick movieGenres is null so getGenresOnly() " );
                getGenresOnly();
            }

            switch ( view.getId() ){

                case R.id.rlFav :

                    showMsg = "FAV";
                    setBoldTextTypeSelected();

                    getFavoritedMovies();

                    break;

                case R.id.rlSort :

                    showMsg = "SORT";
                    setBoldTextTypeSelected();

                    showSelectSortDialog();

                    break;

                case R.id.rlFilter :

                    showMsg = "FILTER";
                    setBoldTextTypeSelected();

                    showSelectFilterDialog();

                    break;

                case R.id.rlPopular :

                    showBy = ServiceConstants.POPULAR;
                    showMsg = "POPULAR";

                    getMovies(currentPage);

                    setBoldTextTypeSelected();
                    break;

                case R.id.rlTopRated :

                    showBy = ServiceConstants.TOP_RATED;
                    showMsg = "TOP RATED";

                    getMovies(currentPage);

                    setBoldTextTypeSelected();
                    break;

                case R.id.rlLatest :

                    showBy = ServiceConstants.LATEST;
                    showMsg = "LATEST";

                    getMovies(currentPage);

                    setBoldTextTypeSelected();
                    break;

                case R.id.rlUpComing :

                    showBy = ServiceConstants.UPCOMING;
                    showMsg = "UPCOMING";

                    getMovies(currentPage);

                    setBoldTextTypeSelected();
                    break;

                case R.id.rlNowPlaying :

                    showBy = ServiceConstants.NOW_PLAYING;
                    showMsg = "NOW PLAYING";

                    getMovies(currentPage);

                    setBoldTextTypeSelected();
                    break;
            }

        }


    }//end of onClick

    // MovieList RecyclerView onclick listener
    IOnClickMoviesCallback callbackOnclickMovie = new IOnClickMoviesCallback() {
        @Override
        public void onClick(Movie movie) {
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra( MovieDetailActivity.MOVIE_ID, movie.getId());
            startActivity(intent);
        }
    };



    private void setupOnScrollListener() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        moviesList.setLayoutManager(layoutManager);
        moviesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int totalItemCount   = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem  = layoutManager.findLastVisibleItemPosition();

//                if ( firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                if( totalItemCount - lastVisibleItem <=5 ){

                    if ( !isLoadingMovies ){
                        //Do pagination.. fetch new data

                        if( isSearchingMovies ){

                            getSearchedMovies( currentPage + 1, searchQuery );

                        }else if( isSortingMovies ) {

                            getSortedMovies(  currentPage + 1, sortBy );

                        }else if( isFilteringMovies ) {

                            getFilteredMovies( currentPage + 1, filterBy );

                        }else if( isCallingFavorites ) {
//                              showWarningToast( "Favori Listesindeki Tüm Filmler ("+favMovieList.size()+")Görüntüleniyor...");
                        }else{
                            getMovies(currentPage + 1 );
                        }

                    }

                }

            }
        });

    }//end of setupOnScrollListener


    /**********************************************************************************************/

    private void getGenres() {

        moviesRepository.getGenres(new IGetGenresCallback() {
            @Override
            public void onSuccess(List<Genre> genres) {

                movieGenres = genres;

                setFilterCategoryWithGenres();

                getMovies( currentPage );

            }

            @Override
            public void onError(String errorMsg) {
                showError("Getting some errors when getGenres()! : " + errorMsg );
            }
        });

    }//end of getGenres

    private void getGenresOnly() {

        moviesRepository.getGenres(new IGetGenresCallback() {
            @Override
            public void onSuccess(List<Genre> genres) {

                movieGenres = genres;

                setFilterCategoryWithGenres();

            }

            @Override
            public void onError(String errorMsg) {
                showError("Getting some errors when getGenresOnly()! : " + errorMsg );
            }
        });

    }//end of getGenres

    private void setFilterCategoryWithGenres(){
        filterCategories = new String[ movieGenres.size() ];
        for(int i=0; i<movieGenres.size(); i++ ){
            filterCategories[ i ] =  movieGenres.get( i ).getName();
        }

    }//end of setFilterCategoryWithGenres


    /**********************************************************************************************/

    private void getMovies(int page ) {

        showProgress();

        isLoadingMovies = true;

        isSortingMovies = false;

        isSearchingMovies = false;

        isFilteringMovies = false;

        isCallingFavorites = false;

        moviesRepository.getMovies( page, showBy, new IGetMoviesCallback() {
            @Override
            public void onSuccess(int page, List<Movie> movies) {

                if (adapter == null) {
                    adapter = new MoviesAdapter( context, movies, movieGenres, callbackOnclickMovie );
                    moviesList.setAdapter( adapter) ;


                }else{

                    if ( page == 1 ) {
                        adapter.clearMovies();
                    }

                    adapter.appendMovies( movies );
                }

                currentPage = page;
                isLoadingMovies = false;

                showWarningToast( movies.size() + " " + showMsg + " Movies loaded. Total movies : " + adapter.getItemCount() );
                hideProgress();
            }


            @Override
            public void onError(String errorMsg) {
                showError("Getting some errors when getMovies()! : " + errorMsg );
            }
        });

    }//end of getMovies


    private void getSearchedMovies(int page, final String query ) {

        showProgress();

        isSearchingMovies = true;

        isLoadingMovies = true;

        isSortingMovies = false;

        isFilteringMovies = false;

        isCallingFavorites = false;

        moviesRepository.getSearchedMovies( page, query, new IGetMoviesCallback() {
            @Override
            public void onSuccess(int page, List<Movie> movies) {

                if (adapter == null) {
                    adapter = new MoviesAdapter( context, movies, movieGenres, callbackOnclickMovie );
                    moviesList.setAdapter( adapter) ;
                }else{

                    if ( page == 1 ) {
                        adapter.clearMovies();
                    }

                    adapter.appendMovies( movies );
                }

                currentPage = page;
                isLoadingMovies = false;

                showWarningToast( movies.size() + " \"" + query.toUpperCase() + "\" searched Movies loaded. Total movies : " + adapter.getItemCount() );
                hideProgress();
            }


            @Override
            public void onError(String errorMsg) {
                showError("Getting some errors when getSearchedMovies()! : " + errorMsg );
            }
        });

    }//end of getSearchedMovies


    private void showSelectSortDialog(){

        final String[] listItemsSort = getResources().getStringArray(R.array.sort_categories);

        dialogBuilderSort = new AlertDialog.Builder( MainActivity.this , R.style.MyAlertDialogStyle );
        dialogBuilderSort.setTitle("Choose Sort Type");
        dialogBuilderSort.setCancelable( true );
        dialogBuilderSort.setSingleChoiceItems( listItemsSort, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                sortMsg = listItemsSort[i];

                switch ( sortMsg ) {

                    case "TITLE_ASC":
                        sortBy = ServiceConstants.TITLE_ASC;
                        break;
                    case "TITLE_DESC":
                        sortBy = ServiceConstants.TITLE_DESC;
                        break;
                    case "POPULARITY_ASC":
                        sortBy = ServiceConstants.POPULARITY_ASC;
                        break;
                    case "POPULARITY_DESC":
                        sortBy = ServiceConstants.POPULARITY_DESC;
                        break;
                    case "VOTE_AVG_ASC":
                        sortBy = ServiceConstants.VOTE_AVG_ASC;
                        break;
                    case "VOTE_AVG_DESC":
                        sortBy = ServiceConstants.VOTE_AVG_DESC;
                        break;
                    case "VOTE_COUNT_ASC":
                        sortBy = ServiceConstants.VOTE_COUNT_ASC;
                        break;
                    case "VOTE_COUNT_DESC":
                        sortBy = ServiceConstants.VOTE_COUNT_DESC;
                        break;
                    case "REVENUE_ASC":
                        sortBy = ServiceConstants.REVENUE_ASC;
                        break;
                    case "REVENUE_DESC":
                        sortBy = ServiceConstants.REVENUE_DESC;
                        break;
                    default:
                         sortBy = ServiceConstants.POPULARITY_ASC;
                         break;

                }

//                Log.d("Main", "showSelectSortDialog() : setSingleChoiceItems() : selected : " + sortMsg  + " sortBy : " + sortBy );

                getSortedMovies(  currentPage, sortBy );

                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = dialogBuilderSort.create();
        mDialog.show();

    }//end of showSelectSortDialog


    private void getSortedMovies(int page, final String sort_by ) {

        showProgress();

        isSortingMovies = true;

        isLoadingMovies = true;

        isSearchingMovies = false;

        isFilteringMovies = false;

        isCallingFavorites = false;

        moviesRepository.getSortedMovies( page, sort_by, new IGetMoviesCallback() {
            @Override
            public void onSuccess(int page, List<Movie> movies) {

                if (adapter == null) {
                    adapter = new MoviesAdapter( context, movies, movieGenres, callbackOnclickMovie );
                    moviesList.setAdapter( adapter) ;
                }else{

                    if ( page == 1 ) {
                        adapter.clearMovies();
                    }

                    adapter.appendMovies( movies );
                }

                currentPage = page;
                isLoadingMovies = false;

                showWarningToast( movies.size() + " " + sortMsg + " sorted Movies loaded. Total movies : " + adapter.getItemCount() );
                hideProgress();
            }


            @Override
            public void onError(String errorMsg) {
                showError("Getting some errors when getSortedMovies()! : " + errorMsg );
            }
        });

    }//end of getSortedMovies


    /**********************************************************************************************/


    private void showSelectFilterDialog(){

        dialogBuilderSort = new AlertDialog.Builder( MainActivity.this , R.style.MyAlertDialogStyle );
        dialogBuilderSort.setTitle("Choose Filter Type");
        dialogBuilderSort.setCancelable( true );
        dialogBuilderSort.setSingleChoiceItems( filterCategories, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                filterMsg = filterCategories[ i ];
                filterBy  = getFilterCategoryID( filterMsg );

//                Log.d("Main", "showSelectSortDialog() : setSingleChoiceItems() : selected filterCategories.get("+i+") : " + filterMsg+ " : ID : " + filterBy );

                getFilteredMovies(  currentPage, filterBy );

                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = dialogBuilderSort.create();
        mDialog.show();

    }//end of showSelectSortDialog


    private int getFilterCategoryID(String filterGenreName){
        int filterID = 28;  // "Action" genre Default
        for(int i=0; i<movieGenres.size(); i++ ){
            if( movieGenres.get( i ).getName().equals( filterGenreName ) ){
                filterID = movieGenres.get( i ).getId();
            }
        }
        return filterID;
    }//end of getFilterCategoryID


    private void getFilteredMovies(int page, int filterID ) {

        showProgress();

        isFilteringMovies = true;

        isLoadingMovies = true;

        isSortingMovies = false;

        isSearchingMovies = false;

        isCallingFavorites = false;

        moviesRepository.getFilteredMovies( page, ServiceConstants.POPULARITY_DESC, filterID, new IGetMoviesCallback() {
            @Override
            public void onSuccess(int page, List<Movie> movies) {

                if (adapter == null) {
                    adapter = new MoviesAdapter( context, movies, movieGenres, callbackOnclickMovie );
                    moviesList.setAdapter( adapter) ;
                }else{

                    if ( page == 1 ) {
                        adapter.clearMovies();
                    }

                    adapter.appendMovies( movies );
                }

                currentPage = page;
                isLoadingMovies = false;

                showWarningToast( movies.size() + " " + filterMsg + " filtered Movies loaded. Total movies : " + adapter.getItemCount() );
                hideProgress();
            }


            @Override
            public void onError(String errorMsg) {
                showError("Getting some errors when getFilteredMovies()! : " + errorMsg );
            }
        });

    }//end of getFilteredMovies


    /**********************************************************************************************/


    public boolean checkDbAndGetFavState() {
        boolean state;
        int favCount = sqliteDB.numberOfRows();
        if( favCount != 0 ){
            state = true;
            cvFav.setVisibility(View.VISIBLE);
        }else{
            state = false;
            cvFav.setVisibility(View.GONE);
        }
        return state;
    }//end of checkDbAndGetFavState

    public void getFavMovieFromDbAndSetList() {

        if( !favMovieList.isEmpty() ) favMovieList.clear();

        favMovieList = sqliteDB.getAllMovies();

    }//end of getFavMovieFromDbToList



    private void getFavoritedMovies() {

        showProgress();

        isLoadingMovies = true;

        isCallingFavorites = true;

        isSortingMovies = false;

        isSearchingMovies = false;

        isFilteringMovies = false;

        if (adapter == null) {
            adapter = new MoviesAdapter( context, favMovieList, movieGenres, callbackOnclickMovie );
            moviesList.setAdapter( adapter) ;
        }else{
            adapter.clearMovies();
            adapter.appendMovies( favMovieList );
        }

        isLoadingMovies = false;
        showWarningToast( favMovieList.size() + " Favorite Movies loaded. Total movies : " + adapter.getItemCount() );
        hideProgress();

    }//end of getFavoritedMovies


    /**********************************************************************************************/

    private void showError(String msg) {
        showWarningToast( "Check your network connection : " + msg );
        hideProgress();
    }


    private void showWarningToast(String msg) {
        Toast.makeText( context, msg, Toast.LENGTH_SHORT ).show();
    }


    public void hideKeyboard(){
        if (imm != null) {
            imm.toggleSoftInput( 0 , 0 );
//            imm.toggleSoftInput( searchView.getApplicationWindowToken() , 0 );
//            // test try // getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
        }
    }//end of hideKeyboard


    public void setBoldTextTypeSelected() {

        switch (showMsg) {

            case "POPULAR" :
                tvFav.setTypeface(Typeface.DEFAULT);
                tvFilter.setTypeface(Typeface.DEFAULT);
                tvSort.setTypeface(Typeface.DEFAULT);
                tvPopular.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                tvTopRated.setTypeface(Typeface.DEFAULT);
                tvLatest.setTypeface(Typeface.DEFAULT);
                tvUpComing.setTypeface(Typeface.DEFAULT);
                tvNowPlaying.setTypeface(Typeface.DEFAULT);
                break;

            case "TOP RATED" :
                tvFav.setTypeface(Typeface.DEFAULT);
                tvFilter.setTypeface(Typeface.DEFAULT);
                tvSort.setTypeface(Typeface.DEFAULT);
                tvPopular.setTypeface(Typeface.DEFAULT);
                tvTopRated.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                tvLatest.setTypeface(Typeface.DEFAULT);
                tvUpComing.setTypeface(Typeface.DEFAULT);
                tvNowPlaying.setTypeface(Typeface.DEFAULT);
                break;

            case  "LATEST" :
                tvFav.setTypeface(Typeface.DEFAULT);
                tvFilter.setTypeface(Typeface.DEFAULT);
                tvSort.setTypeface(Typeface.DEFAULT);
                tvPopular.setTypeface(Typeface.DEFAULT);
                tvTopRated.setTypeface(Typeface.DEFAULT);
                tvLatest.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                tvUpComing.setTypeface(Typeface.DEFAULT);
                tvNowPlaying.setTypeface(Typeface.DEFAULT);
                break;

            case "UPCOMING" :
                tvFav.setTypeface(Typeface.DEFAULT);
                tvFilter.setTypeface(Typeface.DEFAULT);
                tvSort.setTypeface(Typeface.DEFAULT);
                tvPopular.setTypeface(Typeface.DEFAULT);
                tvTopRated.setTypeface(Typeface.DEFAULT);
                tvLatest.setTypeface(Typeface.DEFAULT);
                tvUpComing.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                tvNowPlaying.setTypeface(Typeface.DEFAULT);
                break;

            case "NOW PLAYING" :
                tvFav.setTypeface(Typeface.DEFAULT);
                tvFilter.setTypeface(Typeface.DEFAULT);
                tvSort.setTypeface(Typeface.DEFAULT);
                tvPopular.setTypeface(Typeface.DEFAULT);
                tvTopRated.setTypeface(Typeface.DEFAULT);
                tvLatest.setTypeface(Typeface.DEFAULT);
                tvUpComing.setTypeface(Typeface.DEFAULT);
                tvNowPlaying.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                break;

            case "SORT" :
                tvFav.setTypeface(Typeface.DEFAULT);
                tvFilter.setTypeface(Typeface.DEFAULT);
                tvSort.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                tvPopular.setTypeface(Typeface.DEFAULT);
                tvTopRated.setTypeface(Typeface.DEFAULT);
                tvLatest.setTypeface(Typeface.DEFAULT);
                tvUpComing.setTypeface(Typeface.DEFAULT);
                tvNowPlaying.setTypeface(Typeface.DEFAULT);
                break;

            case "FILTER" :
                tvFav.setTypeface(Typeface.DEFAULT);
                tvFilter.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                tvSort.setTypeface(Typeface.DEFAULT);
                tvPopular.setTypeface(Typeface.DEFAULT);
                tvTopRated.setTypeface(Typeface.DEFAULT);
                tvLatest.setTypeface(Typeface.DEFAULT);
                tvUpComing.setTypeface(Typeface.DEFAULT);
                tvNowPlaying.setTypeface(Typeface.DEFAULT);
                break;

            case "FAV" :
                tvFav.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                tvFilter.setTypeface(Typeface.DEFAULT);
                tvSort.setTypeface(Typeface.DEFAULT);
                tvPopular.setTypeface(Typeface.DEFAULT);
                tvTopRated.setTypeface(Typeface.DEFAULT);
                tvLatest.setTypeface(Typeface.DEFAULT);
                tvUpComing.setTypeface(Typeface.DEFAULT);
                tvNowPlaying.setTypeface(Typeface.DEFAULT);
                break;

            default:
                tvFav.setTypeface(Typeface.DEFAULT);
                tvFilter.setTypeface(Typeface.DEFAULT);
                tvSort.setTypeface(Typeface.DEFAULT);
                tvPopular.setTypeface(Typeface.DEFAULT);
                tvTopRated.setTypeface(Typeface.DEFAULT);
                tvLatest.setTypeface(Typeface.DEFAULT);
                tvUpComing.setTypeface(Typeface.DEFAULT);
                tvNowPlaying.setTypeface(Typeface.DEFAULT);
                break;
        }
    }//end of setBoldTextTypeSelected


    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        if(progressBar!=null && progressBar.isShown()){
            progressBar.setVisibility(View.GONE);
        }
    }


    /**********************************************************************************************/

    public boolean haveNetworkConnection() {

        boolean haveConnected  = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();

        for( NetworkInfo ni : networkInfos ){

            if( ni != null && ni.isConnected() ){
                haveConnected = true;
            }
        }

        if( haveConnected ){

            showListLayout();

            hideNetworkError();

        }else{

            hideListLayout();

            showNetworkError();
        }

        return haveConnected ;
    }// end of haveNetworkConnection


    public void showListLayout() {
        if( mSwipeRefreshLayout.getVisibility() == View.GONE ) mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }
    public void hideListLayout() {
        if( mSwipeRefreshLayout.getVisibility() == View.VISIBLE ) mSwipeRefreshLayout.setVisibility(View.GONE);
    }

    public void showNetworkError() {
        rlNetworkError.setVisibility(View.VISIBLE);
        showWarningToast( "Check Your Internet Connection!");
    }
    public void hideNetworkError() {
        rlNetworkError.setVisibility(View.GONE);
    }

    /**********************************************************************************************/





}
