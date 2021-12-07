package com.example.movielist;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Collections;

import static com.example.movielist.MainActivity.arrayList;

public class Pop extends Activity {
    Button btn,btn2;
    SeekBar skbr;
    TextView Number;
    TextView Average;
    int rating;
    int Num_of_ratings= 0;
    int avg=0;
    int sum=0;
    boolean first= true;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);

        btn = (Button) findViewById(R.id.Add);
        btn2 = (Button) findViewById(R.id.Average_btn);
        skbr = (SeekBar) findViewById(R.id.seekBar);
        Number = findViewById(R.id.rating);
        Number.setText(skbr.getProgress() + "/" + skbr.getMax());
        Average = findViewById(R.id.average);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        skbr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pval = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
                Number.setText(pval + "/" + seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Number.setText(pval + "/" + seekBar.getMax());
            }


        });




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (first) {
                    rating = skbr.getProgress();
                } else {
                    rating = avg;
                }


                ViewMovieInfo.displayed_movie.Rating = rating;
                ViewMovieInfo.displayed_movie.Rating2 = rating;
                Toast.makeText(Pop.this,"Movie added!!",Toast.LENGTH_LONG).show();
                //MainActivity.arrayList.clear();
                ViewMovieInfo.displayed_movie.isAdded = true;
                arrayList.add(ViewMovieInfo.displayed_movie);
                Collections.sort(arrayList, new SortbyRating());
                saveData();
                Pop.this.finish();



            }});

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Num_of_ratings++;
                sum=sum + skbr.getProgress();
                avg=sum/Num_of_ratings;
                Average.setText(avg +"/"+ skbr.getMax());
                first = false;
            }
        });



    }


    public void saveData() {
        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("task list",json);
        editor.apply();
    }

}
