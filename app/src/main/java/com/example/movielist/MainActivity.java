package com.example.movielist;


import android.content.Intent;
import android.content.SharedPreferences;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;


class Movie
{
    String name;
    String id ;
    int Rating;
    Integer budget;
    Integer Runtime;
    float Rating2;
    Calendar dateAdded;
    String releaseDate;
    String Poster;
    String vKey;
    String vote_average;
    String overview;

    String tagline;
    String moreInfo;
    StringBuilder genres;
    String original_language;
    String imdb_id;
    boolean isAdded = false;

    // Constructor
    public Movie(){
        this.name = "";
        this.id ="";
        this.Rating = 0;
        this.tagline="";
        this.moreInfo = "";
        this.vote_average="";
        this.Runtime = 0;
        this.original_language="en";
        this.budget = 0;
        this.overview ="";
        this.releaseDate ="";
        this.vKey = "";
        this.Poster = "";
        this.imdb_id="";
        this.Rating2=0;
        this.genres=new StringBuilder();



    }
    public Movie(String name, int r)
    {
        this.name = name;
        this.Rating = r;
        this.Rating2 = r;
        this.dateAdded = Calendar.getInstance();
        this.Poster = "";

    }

    public Movie(String name, String relDate ,String image,String id,String vote)
    {
        this.name = name;
        this.releaseDate = relDate;
        this.dateAdded = Calendar.getInstance();
        this.Poster = image;
        this.id = id;
        this.vote_average = vote;

    }

    // Used to print movie details in main()
    public String toString()
    {
        return this.name + " " + this.Rating;
    }


}

class SortbyRating implements Comparator<Movie>
{
    // Used for sorting in descending order of
    // roll number
    public int compare(Movie a, Movie b)
    {
        return Float.compare(b.Rating2,a.Rating2);
    }
}





public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter2.OnNoteListener {
    public static final String EXTRA_NUMBER = "com.example.movielist.EXTRA_NUMBER";

    Button AddMovie;
    Calendar CurrentDate;
    static boolean listPopulated = false;
    static ArrayList<Movie>  arrayList=new ArrayList<Movie>();
    Calendar checkDate;
    private static final String TAG = "MainActivity";
    RecyclerViewAdapter2 adapter;

    private ColorDrawable swipeBackground= new ColorDrawable(Color.parseColor("#FF0000"));
    private Drawable deleteIcon;


    @Override
    protected void onStart() {
        super.onStart();
        initRecyclerView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();

        CurrentDate = Calendar.getInstance();
        checkDate = Calendar.getInstance();
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart",true);

        deleteIcon= ContextCompat.getDrawable(this,R.drawable.ic_action_name);

        if(firstStart) {
            firstTimeFun();
        }

        checkDate.setTimeInMillis(prefs.getLong("TimeLong",CurrentDate.getTimeInMillis()));





        if(CurrentDate.getTime().after(checkDate.getTime()))
        {
            int i=0;


            Toast.makeText(this,"Current Date:" + CurrentDate.getTime().toString()+"\n" + "Check Date:" +checkDate.getTime().toString(),Toast.LENGTH_SHORT).show();

            while(i<arrayList.size()){

                if(arrayList.get(i).Rating2%Calendar.DAY_OF_YEAR == 0 && arrayList.get(i).Rating2 > 0)
                {
                    arrayList.get(i).Rating2 +=1;
                }

                arrayList.get(i).Rating2 +=0.25;
                i++;
            }

            checkDate.add(Calendar.DAY_OF_YEAR,1);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("TimeLong",checkDate.getTimeInMillis());
            editor.apply();


            //Toast.makeText(this,"checkdate:"+ checkDate.getTime().toString(),Toast.LENGTH_SHORT).show();

        }


        AddMovie=(Button)findViewById(R.id.button2);


        /*if(listPopulated && SearchingMovieActivity.allowAdd) {
            //String movieName = intent.getStringExtra(SearchingMovieActivity.EXTRA_TEXT);
            //int Rating = intent.getIntExtra(SearchingMovieActivity.EXTRA_NUMBER, 0);


            //arrayList.add(new Movie(movieName, Rating));
            Collections.sort(arrayList, new SortbyRating());

            SearchingMovieActivity.allowAdd = false;
        }

        else{checkDate.add(Calendar.DAY_OF_YEAR, 1);}*/







        //arrayList.clear();
        /*Movie test = new Movie();
        test.isAdded = true;
        test.Rating2 = 5;
        test.Rating = 6;
        test.budget = 12312;
        test.releaseDate = "12/12/122";
        test.imdb_id = "123";
        test.Poster = "sdawd";
        test.vKey = "asdwad";
        test.dateAdded = Calendar.getInstance();
        test.name = "cool";

        arrayList.add(test);*/


        initRecyclerView();




        AddMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listPopulated = true;
                saveData();
                openActivity2();
            }
        });
    }

    private void firstTimeFun() {
        checkDate.add(Calendar.DAY_OF_YEAR,1);
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart",false);
        editor.putLong("TimeLong",checkDate.getTimeInMillis());
        editor.apply();



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveData();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView Main: ");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter2(this,arrayList,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            int removedPosition = viewHolder.getAdapterPosition();
            Movie removedItem = arrayList.get(viewHolder.getAdapterPosition());
            arrayList.remove(viewHolder.getAdapterPosition());
            adapter.notifyItemRemoved(removedPosition);

            Snackbar.make(viewHolder.itemView,removedItem.name + " deleted.",Snackbar.LENGTH_LONG).setAction("UNDO", new MyUndoListener(viewHolder,removedItem,removedPosition)).show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            int iconMargin = (viewHolder.itemView.getHeight()- deleteIcon.getIntrinsicHeight())/2;
            swipeBackground.setBounds(viewHolder.itemView.getLeft(),viewHolder.itemView.getTop(),(int)dX,viewHolder.itemView.getBottom());
            deleteIcon.setBounds(viewHolder.itemView.getLeft() + iconMargin,viewHolder.itemView.getTop() + iconMargin,viewHolder.itemView.getLeft()+iconMargin+ deleteIcon.getIntrinsicWidth(),viewHolder.itemView.getBottom() - iconMargin);
            swipeBackground.draw(c);
            c.save();

            c.clipRect(viewHolder.itemView.getLeft(),viewHolder.itemView.getTop(),(int)dX,viewHolder.itemView.getBottom());

            deleteIcon.draw(c);

            c.restore();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void onNoteClick(int position) {
        openActivity(position);


    }

    private void openActivity(int pos) {
        //Toast.makeText(this,"HEYYYY"+pos,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this,ViewMovieInfo2.class);
        intent.putExtra(EXTRA_NUMBER,pos);
        startActivity(intent);
    }


    public class MyUndoListener implements View.OnClickListener {
    RecyclerView.ViewHolder viewHolder;
    Movie e;
    int removedPosition;

        public MyUndoListener(RecyclerView.ViewHolder viewHolder,Movie movie,int i) {
            this.viewHolder=viewHolder;
            this.e=movie;
            this.removedPosition=i;
        }

        @Override
        public void onClick(View v) {
            arrayList.add(removedPosition,e);
            adapter.notifyItemInserted(removedPosition);

        }
    }

   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            // Change things
        } else if (newConfig.orientation ==
                Configuration.ORIENTATION_PORTRAIT){
            // Change other things
        }
    }*/

    protected void onStop(){
        super.onStop();
        //Log.d(TAG, "onStop: IT was saaaaved");
        saveData();


    }

    public void saveData() {
        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("task list",json);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list",null);
        Type type = new TypeToken<ArrayList<Movie>>() {}.getType();
        arrayList = gson.fromJson(json, type);

        if(arrayList == null){
            arrayList = new ArrayList<>();
        }

    }
    public void openActivity2(){
        Intent intent = new Intent(this, SearchingMovieActivity.class);
        startActivity(intent);
    }


}
