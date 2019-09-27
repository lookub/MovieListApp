package com.uakgul.moviedb.moviet.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.uakgul.moviedb.moviet.model.Genre;
import com.uakgul.moviedb.moviet.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME  = "MoiveFav.db";
    public static final String TABLE_NAME_FAV = "MyFaorites";


    private static final String SCRIPT_SELECT_ALL  =  "SELECT * from " + TABLE_NAME_FAV;
    private static final String SCRIPT_SELECT_FROM_MOVIEID  =  "SELECT * from " + TABLE_NAME_FAV + " WHERE movieID = ";

    private static final String SCRIPT_CREATE_TABLE_FAV =  "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_FAV + " " +
            " ( " +
            " ID INTEGER PRIMARY KEY AUTOINCREMENT, "   +
            " movieID INTEGER, " +
            " title TEXT, " +
            " posterPath TEXT, " +
            " releaseDate TEXT, " +
            " rating REAL, " +
            " voteCount INTEGER, " +
            " popularity REAL, " +
            " genreIds TEXT, " +   // List<Integer> genreList.getId()
            " genreNames TEXT, " + // List<String>  genreList.getName()
            " overview TEXT, " +
            " backdrop TEXT, " +
            " UNIQUE( movieID ) ON CONFLICT REPLACE " +
            " ) " ;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( SCRIPT_CREATE_TABLE_FAV );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate( db );

    }



    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries( db, TABLE_NAME_FAV );
        return numRows;
    }


    public boolean insertMovie(Movie movie) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("movieID",    movie.getId() );
        contentValues.put("title",      movie.getTitle() );
        contentValues.put("posterPath", movie.getPosterPath() );
        contentValues.put("releaseDate",movie.getReleaseDate() );
        contentValues.put("rating",     movie.getRating() );
        contentValues.put("voteCount",  movie.getVote_count() );
        contentValues.put("popularity", movie.getPopularity() );

//        Log.d("DBHelper", "insertMovie() : movie.getGenreIds() : " + movie.getGenreIds() + "..." );

        List<Genre> genresTemp = movie.getGenres();
//        Log.d("DBHelper", "insertMovie() : movie.getGenres() : " + genresTemp.toString() + "..." );

        List<String> genresTempIdsList = getStringListGenreIdFromObjectList( genresTemp );
//        Log.d("DBHelper", "insertMovie() : genresTempIdsList : " + genresTempIdsList.toString() + "..." );
        String genresTempIdsStr = getStringFromListString( genresTempIdsList );

        List<String> genresTempNamesList = getStringListGenreNamesFromObjectList( genresTemp );
//        Log.d("DBHelper", "insertMovie() : genresTempNamesList : " + genresTempNamesList.toString() + "..." );
        String genresTempNamesStr = getStringFromListString( genresTempNamesList );


        contentValues.put("genreIds",   genresTempIdsStr );
        contentValues.put("genreNames", genresTempNamesStr );
        contentValues.put("overview",   movie.getOverview() );
        contentValues.put("backdrop",   movie.getBackdrop() );
//        contentValues.put("genres",     null );
        db.insert( TABLE_NAME_FAV , null, contentValues );
        return true;
    }//end of insertMovie


    public Integer deletetMovieFromID(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete( TABLE_NAME_FAV,  "movieID = ? " , new String[] { Integer.toString(id) });
    }//deletetMovieFromID


    public ArrayList<Movie> getAllMovies() {

        ArrayList<Movie> movieArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( SCRIPT_SELECT_ALL, null );

        if( cursor != null && cursor.moveToFirst() ){

            Movie movie;
            while( !cursor.isAfterLast() ){

                movie = getMovieFromCursor( cursor );

                movieArrayList.add( movie );

                cursor.moveToNext();
            }
            if (!cursor.isClosed())  {
                cursor.close();
            }
        }

        return movieArrayList;
    }//end of getAllMovies


    public Movie getMovieFromID(int id) {

        Movie movie = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( SCRIPT_SELECT_FROM_MOVIEID + id , null );

        if( cursor != null && cursor.moveToFirst() ){

            movie = getMovieFromCursor( cursor );

            if (!cursor.isClosed())  {
                cursor.close();
            }
        }
        
        return movie;
    }//end of getMovieFromID


    public Movie getMovieFromCursor(Cursor cursor) {

        Movie movie = new Movie();
        movie.setId( cursor.getInt(  cursor.getColumnIndex( "movieID"   )  ) );
        movie.setTitle( cursor.getString(  cursor.getColumnIndex( "title"   )  ) );
        movie.setPosterPath( cursor.getString(  cursor.getColumnIndex( "posterPath"   )  ) );
        movie.setReleaseDate( cursor.getString(  cursor.getColumnIndex( "releaseDate"   )  ) );
        movie.setRating( cursor.getFloat(  cursor.getColumnIndex( "rating"   )  ) );
        movie.setVote_count( cursor.getInt( cursor.getColumnIndex( "voteCount"   )  ) );
        movie.setPopularity( cursor.getFloat(  cursor.getColumnIndex( "popularity"   )  ) );


        String genresTempIdsStr = cursor.getString(  cursor.getColumnIndex( "genreIds"   ) );
//        Log.d("DBHelper", "getMovieFromCursor() : genresTempIdsStr : " + genresTempIdsStr + "..." );

        List<String> genresTempIdsStrList = getStringListFromString( genresTempIdsStr );

        String genresTempNamesStr = cursor.getString( cursor.getColumnIndex( "genreNames"   ) );
//        Log.d("DBHelper", "getMovieFromCursor() : genresTempNamesStr : " + genresTempNamesStr + "..." );

        List<String> genresTempNamesList = getStringListFromString( genresTempNamesStr );

        List<Genre> genreListTemp = getGenreObjectFromStringLists( genresTempIdsStrList, genresTempNamesList );

        Log.d("DBHelper", "getMovieFromCursor() : genreListTemp : " + genreListTemp + "..." );

        movie.setGenres( genreListTemp );


        List<Integer> genreIdsTempListInt = getIntegerListFromStringList( genresTempIdsStrList );

        movie.setGenreIds( genreIdsTempListInt );

        movie.setOverview( cursor.getString(  cursor.getColumnIndex( "overview"   )  ) );
        movie.setBackdrop( cursor.getString(  cursor.getColumnIndex( "backdrop"   )  ) );


        return  movie;
    }//end of getMovieFromCursor




    /*** Utils methods ****************************************************************************/


    public List<String> getStringListGenreIdFromObjectList(List<Genre> genreList){
        List<String> stringList = new ArrayList<>( genreList.size() );
        for(int i=0; i<genreList.size(); i++){
            stringList.add( String.valueOf(genreList.get( i ).getId() ) );
        }
        return stringList;
    }//end of getStringListGenreIdFromObjectList


    public List<String> getStringListGenreNamesFromObjectList(List<Genre> genreList){
        /* Specify the size of the list up front to prevent resizing. */
        List<String> stringList = new ArrayList<>( genreList.size() );
        for(int i=0; i<genreList.size(); i++){
            stringList.add( genreList.get( i ).getName() );
        }
        return stringList;
    }//end of getStringListGenreNamesFromObjectList



    public List<Genre> getGenreObjectFromStringLists(List<String> idList, List<String> nameList){

        List<Genre> genreList = new ArrayList<>( idList.size() );

        for(int i=0; i<idList.size(); i++){
            Genre genre = new Genre ( Integer.parseInt( idList.get( i ) ) , nameList.get(i) );
            genreList.add( genre );
        }
        return genreList;
    }//end of getStringListGenreIdFromObjectList



    public List<Integer> getIntegerListFromStringList(List<String> stringList){
        List<Integer> integerlist = new ArrayList<>( stringList.size() );
        for(String string : stringList ) integerlist.add( Integer.valueOf( string ) );

        return integerlist;
    }//end of getStringListFromString




    public List<String> getStringListFromIntegerList(List<Integer> intList){
        /* Specify the size of the list up front to prevent resizing. */
        List<String> stringList = new ArrayList<>( intList.size() );
        for (Integer myInt : intList) {
            stringList.add( String.valueOf( myInt ) );
        }
        return stringList;
    }//end of getStringListFromIntegerList


    public String getStringFromListString(List<String> stringList){
        return TextUtils.join( ", ", stringList );
    }

    public List<String> getStringListFromString(String string){
        return Arrays.asList( string.split("\\s*,\\s*"));
    }//end of getStringListFromString



}
