package com.example.movielist;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;

import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;


import static com.example.movielist.MainActivity.arrayList;


public class ViewMovieInfo extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {

    private static final String DEVELOPER_KEY = "AIzaSyApOb3ynYu7dQaXl0oJ7iVmvxfbl55KYj0";
    //public static  String VIDEO_ID = "";
    public static Movie displayed_movie = new Movie();


    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;
    private YouTubePlayerView youTubeView;


    TextView Title;
    TextView Actors;
    TextView genres;
    TextView Overview;
    ImageView poster;
    WebView webview;
    Button clk;
//String video_url="<iframe width=\"100%\" height\"100%\" src=\"https://www.youtube.com/embed/hA6hldpSTF8\" frameborder=\"0\" allowfullscreeen></iframe>";
// String video_url="https://www.youtube.com/embed/hA6hldpSTF8";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie_info);
        youTubeView = findViewById(R.id.youtube_view);
        Title = findViewById(R.id.Title);
        Actors = findViewById(R.id.Actors);
        Overview = findViewById(R.id.Overview);
        genres = findViewById(R.id.details);
        clk = (Button)findViewById(R.id.add_to_watchlist);
        poster = findViewById(R.id.poster);
        webview = findViewById(R.id.webview);
        String content = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                " <meta charset=\"UTF-8\" >\n" +
                "\n" +
                "\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<span class=\"imdbRatingPlugin\" data-user=\"ur58524381\" data-title=\""+displayed_movie.imdb_id+"\" data-style=\"p1\"><a href=\"https://www.imdb.com/title/"+displayed_movie.imdb_id+"/?ref_=plg_rt_1\"><img src=\"https://ia.media-imdb.com/images/G/01/imdb/plugins/rating/images/imdb_46x22.png\" alt=\" Avengers: Endgame\n" +
                "(2019) on IMDb\" />\n" +
                "</a></span><script>(function(d,s,id){var js,stags=d.getElementsByTagName(s)[0];if(d.getElementById(id)){return;}js=d.createElement(s);js.id=id;js.src=\"https://ia.media-imdb.com/images/G/01/imdb/plugins/rating/js/rating.js\";stags.parentNode.insertBefore(js,stags);})(document,\"script\",\"imdb-rating-api\");</script>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>";





        checkforButton();





        youTubeView.initialize(DEVELOPER_KEY,this);


        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.loadData(content,"text/html","UTF-8");
        //webview.loadUrl("file:///android_asset/www/index.html");

        Title.setText(displayed_movie.name);
        Actors.setText(displayed_movie.tagline);
        String budget;
        if (displayed_movie.budget == 0)
        {
            budget = "N/A";
        }
        else if(displayed_movie.budget > 1000000){
            int x = displayed_movie.budget/1000000;
            budget = Integer.toString(x) + " million";
        }

        else {budget = displayed_movie.budget.toString();}



        genres.setText("genres: "+displayed_movie.genres +'\n'+"release date: " +displayed_movie.releaseDate+'\n'+"budget: "+budget);
        Overview.setText(displayed_movie.overview);


        String pstr = "https://image.tmdb.org/t/p/w500/"+displayed_movie.Poster;
        Picasso.get().load(pstr).placeholder(R.drawable.gray_poster)
                .error(R.drawable.gray_poster)
                .into(poster, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        clk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMovieInfo.this, Pop.class);
                startActivity(intent);
            }
        });

    }

    private void checkforButton() {
        clk.setClickable(false);
        boolean isAdded = false;
        for(int i=0;i<arrayList.size();i++) {
            if (arrayList.get(i).id.equals(displayed_movie.id)) {
                isAdded = true;
                break;
            }
        }

        if(isAdded==false){
            clk.setEnabled(true);
            clk.setClickable(true);
            clk.setText("Add to Watchlist");
        }

        else{
            clk.setText("Already Added");
            clk.setEnabled(false);

        }


    }

    @Override
    protected void onStart() {

        super.onStart();
        checkforButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkforButton();

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, REQ_START_STANDALONE_PLAYER).show();
        } else {
            String errorMessage = "Sorry: no video available";
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            if(displayed_movie.vKey != "")
            {
                player.cueVideo(displayed_movie.vKey);
            }
            else{Toast.makeText(this, "Sorry no video available", Toast.LENGTH_LONG).show();}

            // Hiding player controls
            // player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        }
    }

    @Override
    public void onClick(View v) {

        //boolean lightboxMode = lightboxModeCheckBox.isChecked();

        Intent intent = null;
        /*if (v == playVideoButton) {
            intent = YouTubeStandalonePlayer.createVideoIntent(
                    this, DEVELOPER_KEY, VIDEO_ID, 0, false, lightboxMode);
        }*/

        if (intent != null) {
            if (canResolveIntent(intent)) {
                startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            } else {
                // Could not resolve the intent - must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING
                        .getErrorDialog(this, REQ_RESOLVE_SERVICE_MISSING).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        displayed_movie = new Movie();
        super.onBackPressed();
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_START_STANDALONE_PLAYER) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(DEVELOPER_KEY, this);
        }
        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, 0).show();
            } else {
                //String errorMessage =
                        //String.format(getString(R.string.), errorReason.toString());
                //Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }


    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    private int parseInt(String text, int defaultValue) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return defaultValue;
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }











}
