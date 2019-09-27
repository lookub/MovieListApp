package com.uakgul.moviedb.moviet.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.uakgul.moviedb.moviet.R;

public class SplashScreen extends AppCompatActivity {
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread time = new Thread(){
            public void run(){
                try{
                    sleep( SPLASH_DISPLAY_LENGTH );
                    Intent my_intent = new Intent( SplashScreen.this , MainActivity.class );
                    startActivity( my_intent );
                }catch(InterruptedException exception){
                    Toast.makeText(getApplicationContext(), "Error!" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }finally{
                    finish();
                }
            }
        };
        time.start();

    }
}
