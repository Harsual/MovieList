package com.example.movielist;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewMovieInfo2 extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String DEVELOPER_KEY = "AIzaSyApOb3ynYu7dQaXl0oJ7iVmvxfbl55KYj0";
    //public static  String VIDEO_ID = "";



    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;
    private YouTubePlayerView youTubeView;


    TextView Title;
    TextView Tagline;
    TextView genres;
    TextView Overview;
    ImageView poster;
    WebView webview;
    Button clk;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        position = intent.getIntExtra(MainActivity.EXTRA_NUMBER,0);

        setContentView(R.layout.activity_view_movie_info);
        youTubeView = findViewById(R.id.youtube_view);
        Title = findViewById(R.id.Title);
        Tagline = findViewById(R.id.Actors);
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
                "<span class=\"imdbRatingPlugin\" data-user=\"ur58524381\" data-title=\""+MainActivity.arrayList.get(position).imdb_id+"\" data-style=\"p1\"><a href=\"https://www.imdb.com/title/"+MainActivity.arrayList.get(position).imdb_id+"/?ref_=plg_rt_1\"><img src=\"https://ia.media-imdb.com/images/G/01/imdb/plugins/rating/images/imdb_46x22.png\" alt=\" Avengers: Endgame\n" +
                "(2019) on IMDb\" />\n" +
                "</a></span><script>(function(d,s,id){var js,stags=d.getElementsByTagName(s)[0];if(d.getElementById(id)){return;}js=d.createElement(s);js.id=id;js.src=\"https://ia.media-imdb.com/images/G/01/imdb/plugins/rating/js/rating.js\";stags.parentNode.insertBefore(js,stags);})(document,\"script\",\"imdb-rating-api\");</script>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        clk.setText("Already Added");
        clk.setEnabled(false);
        youTubeView.initialize(DEVELOPER_KEY,this);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.loadData(content,"text/html","UTF-8");

        Title.setText(MainActivity.arrayList.get(position).name);
        Tagline.setText(MainActivity.arrayList.get(position).tagline);
        String string = "Genres: "+MainActivity.arrayList.get(position).genres +'\n'+"Release date: " +MainActivity.arrayList.get(position).releaseDate+'\n'+"Runtime: "+MainActivity.arrayList.get(position).Runtime.toString();
        genres.setText(string);
        Overview.setText(MainActivity.arrayList.get(position).overview);


        String pstr = "https://image.tmdb.org/t/p/w500/"+MainActivity.arrayList.get(position).Poster;
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
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            player.cueVideo(MainActivity.arrayList.get(position).vKey);

            // Hiding player controls
            // player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, REQ_START_STANDALONE_PLAYER).show();
        } else {
            //String errorMessage = String.format(
            //  getString(R.string.), errorReason.toString());
            //Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
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
