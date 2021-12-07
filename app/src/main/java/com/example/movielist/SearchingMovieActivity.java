package com.example.movielist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.facebook.shimmer.ShimmerFrameLayout;


/*import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;*/


public class SearchingMovieActivity extends AppCompatActivity implements RecyclerViewAdapter.OnNoteListener{
    //public static final String EXTRA_TEXT = "com.example.movielist.EXTRA_TEXT";
    //public static final String EXTRA_NUMBER = "com.example.movielist.EXTRA_NUMBER";
    private static final String TAG = "SearchingMovieActivity";
    public  ArrayList<Movie>  searchList=new ArrayList<Movie>();
    //private ShimmerFrameLayout mShimmerViewContainer;
    public static String API_data;
    ProgressDialog dialog;




    EditText Movie_Name;


    static Boolean allowAdd = false;
    public static String urlString = "";
    static Boolean cancel = false;
    getPosterAsyncTask asyncTask;

    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ArrayList<Movie> placeholder = new ArrayList<>();

        for(int i=0;i<20;i++)
        {
            placeholder.add(new Movie("0",4));
        }

        if(!CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {

            Toast.makeText(this,"No Internet Connection. Please Connect to the internet",Toast.LENGTH_LONG).show();
        }



        setContentView(R.layout.activity_searching_movie);

        //mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        Movie_Name = (EditText) findViewById(R.id.editText);




        Movie_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {
                urlString = s.toString();
                searchList.clear();
                //asyncTask.cancel(false);
                initRecyclerView(placeholder);
                getFromDB();




            }
        });

        Movie_Name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {

                    closeKeyboard();

                    initRecyclerView(placeholder);
                    cancel = true;
                    asyncTask.cancel(true);
                    //searchList.clear();
                    getFromDB();

                    return true;
                }

                return false;
            }


        });


    }



    public void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public void initRecyclerView(ArrayList arr){
        Log.d(TAG, "initRecyclerView: ");
        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        adapter = new RecyclerViewAdapter(this,arr,this );
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL,16));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private void getFromDB(){
            searchList.clear();
            //getPosterAsyncTask asyncTask = new getPosterAsyncTask();
            asyncTask = new getPosterAsyncTask();
            asyncTask.execute();
        }

    private void getFromDB2(int pos){
        //searchList.clear();
        getTrailerAsyncTask asyncTask = new getTrailerAsyncTask(pos);
        asyncTask.execute();
    }

    @Override
    public void onNoteClick(int position) {
        Log.d(TAG, "onNoteClick: ");
        //ViewMovieInfo.VIDEO_ID =
        dialog = new ProgressDialog(this);
        dialog.setMessage("loading info");
        dialog.show();
        getFromDB2(position);

        //Intent intent = new Intent(this, ViewMovieInfo.class);
        //startActivity(intent);
    }



    private class getPosterAsyncTask extends AsyncTask<Void, Void, Void> {
            String data;
            String results;



            @Override
            protected Void doInBackground(Void... voids) {


                InputStream inputStream = null;
                try {

                    URL url = new URL("https://api.themoviedb.org/3/search/movie?api_key=3640d546d2b2c1cdb84c465f601e109a&language=en-US&query="+urlString+"&page=1&include_adult=false");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    //bufferedReader.skip(20);
                    String line ="";
                    data = line;
                    results = "";




                    while(line!=null) {
                        if (isCancelled()) {
                            break;
                        } else {
                            line = bufferedReader.readLine();
                            data = data + line;
                        }

                    }

                    JSONObject JO = new JSONObject(data);
                    results = JO.get("results").toString();
                    JSONArray JA = new JSONArray(results);

                    for(int i=0; i<JA.length();i++) {
                        if (isCancelled()) {
                            break;
                        } else {
                            JSONObject JO2 = (JSONObject) JA.get(i);
                            Movie m = new Movie(JO2.get("original_title").toString(), JO2.get("release_date").toString(), JO2.get("poster_path").toString(), JO2.get("id").toString(), JO2.get("vote_average").toString());
                            searchList.add(m);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(SearchingMovieActivity.this,"FUCK",Toast.LENGTH_LONG).show();
                }finally {
                    try { if (inputStream != null) inputStream.close(); } catch(IOException e) {}


                }




                return null;
            }





            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                initRecyclerView(searchList);
                //adapter.notifyDataSetChanged();
                // stop animating Shimmer and hide the layout
               // mShimmerViewContainer.stopShimmer();
                //mShimmerViewContainer.setVisibility(View.GONE);
            }


        }

    @Override
    protected void onResume() {
        super.onResume();
        //mShimmerViewContainer.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mShimmerViewContainer.stopShimmer();
    }

    private class getTrailerAsyncTask extends AsyncTask<Void, Void, Void> {
        String data;
        String results;
        int pos;

        public getTrailerAsyncTask(int pos) {
            this.pos = pos;
            /*ViewMovieInfo.displayed_movie.id = searchList.get(pos).id;
            */
        }

        @Override
        protected Void doInBackground(Void... Voids) {

            String query = "https://api.themoviedb.org/3/movie/"+searchList.get(pos).id+"/videos?api_key=3640d546d2b2c1cdb84c465f601e109a&language=en-US";



                    InputStream inputStream = null;
            try {
                URL url = new URL(query);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line ="";
                data = line;
                while(line!=null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                JSONObject JO = new JSONObject(data);
                results = JO.get("results").toString();
                JSONArray JA = new JSONArray(results);
                JSONObject JO2= (JSONObject) JA.get(0);
                ViewMovieInfo.displayed_movie.vKey= (JO2.get("key").toString());



            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(SearchingMovieActivity.this,"FUCK",Toast.LENGTH_LONG).show();
            }finally {
                try { if (inputStream != null) inputStream.close(); } catch(IOException e) {}


            }

            query = "https://api.themoviedb.org/3/movie/"+(searchList.get(pos).id)+"?api_key=3640d546d2b2c1cdb84c465f601e109a&language=en-US";

            try {
                URL url = new URL(query);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line ="";
                data = line;
                while(line!=null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                JSONObject JO = new JSONObject(data);

                ViewMovieInfo.displayed_movie.Poster =JO.get("poster_path").toString();
                ViewMovieInfo.displayed_movie.name = JO.get("original_title").toString();
                ViewMovieInfo.displayed_movie.imdb_id = JO.get("imdb_id").toString();
                ViewMovieInfo.displayed_movie.releaseDate = (JO.get("release_date").toString());
                ViewMovieInfo.displayed_movie.budget = (JO.getInt("budget"));
                ViewMovieInfo.displayed_movie.overview = (JO.get("overview").toString());
                ViewMovieInfo.displayed_movie.original_language = (JO.get("original_language").toString());
                ViewMovieInfo.displayed_movie.Runtime =(JO.getInt("runtime"));
                ViewMovieInfo.displayed_movie.tagline = (JO.get("tagline").toString());
                ViewMovieInfo.displayed_movie.vote_average =JO.get("vote_average").toString();
                ViewMovieInfo.displayed_movie.moreInfo =(JO.get("homepage").toString());
                ViewMovieInfo.displayed_movie.id =JO.get("id").toString();

                JSONArray genres = JO.getJSONArray("genres");
                for(int i=0; i<genres.length();i++)
                {
                    ViewMovieInfo.displayed_movie.genres.append(genres.getJSONObject(i).get("name").toString()+", ");
                }







            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }


            return null;
        }







        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            Intent intent = new Intent(SearchingMovieActivity.this, ViewMovieInfo.class);
            startActivity(intent);

        }

    }


    }


