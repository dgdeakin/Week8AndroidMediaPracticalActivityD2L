package com.example.week8androidmediapracticalactivityd2l;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;


//FOr video:
//https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4

// For audio:
//https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3

//Resources:
//https://developer.android.com/media/platform/mediaplayer

//Explore Other Exoplayer
public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private VideoView videoView;
    private MediaController mediaController; // Added for video controls
    ExoPlayer exoPlayer;
    PlayerView playerView;
    Button playerViewButton;
    private Boolean playWhenReady = true;
    private int currentItem = 0;
    private long playbackPosition = 0L;
    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        EditText audioUrlEditText = findViewById(R.id.audioUrlEditText);
        Button playAudioButton = findViewById(R.id.playAudioButton);
        EditText videoUrlEditText = findViewById(R.id.videoUrlEditText);
        Button playVideoButton = findViewById(R.id.playVideoButton);
        videoView = findViewById(R.id.videoView);

        playerView = findViewById(R.id.playerView);
        playerViewButton = findViewById(R.id.playVideoButtonExoplayer);
        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);

        // Initialize MediaController
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView); // Attach to VideoView
        videoView.setMediaController(mediaController); // Link MediaController to VideoView

        // Audio playback button click listener
        playAudioButton.setOnClickListener(v -> {
            String audioUrl = audioUrlEditText.getText().toString().trim();
            if (audioUrl.isEmpty()) {
                Toast.makeText(this, "Please enter an audio URL", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                Toast.makeText(this, audioUrl, Toast.LENGTH_SHORT).show();

            }

            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
                    return true;
                });
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Video play button click listener
        playVideoButton.setOnClickListener(v -> {
            String videoUrl = videoUrlEditText.getText().toString().trim();
            if (videoUrl.isEmpty()) {
                Toast.makeText(this, "Please enter a video URL", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Log.d("VideoView", "Attempting to play: " + videoUrl);
                if (videoView.isPlaying()) {
                    videoView.stopPlayback();
                }

                videoView.setVideoURI(Uri.parse(videoUrl));
                videoView.setOnPreparedListener(mp -> {
                    videoView.start();
                });
                videoView.setOnErrorListener((mp, what, extra) -> {
                    Toast.makeText(this, "Error playing video: " + what + "," + extra, Toast.LENGTH_SHORT).show();
                    return true;
                });
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        playerViewButton.setOnClickListener(v -> {
            if(playerViewButton.getText().toString() == "Play") {

                MediaItem mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4));

                MediaItem mediaItem1 = MediaItem.fromUri(getString(R.string.media_url_mp3));


                MediaItem mediaItem2 = new MediaItem.Builder()
                        .setUri(getString(R.string.media_url_mp4))
                        .setMimeType(MimeTypes.APPLICATION_MPD)
                        .build();


                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.addMediaItem(mediaItem1);
                exoPlayer.addMediaItem(mediaItem2);

                exoPlayer.setPlayWhenReady(playWhenReady);
                exoPlayer.seekTo(currentItem, playbackPosition);

                exoPlayer.prepare();
                exoPlayer.play();

//                    exoPlayer.prepare();

                playerViewButton.setText("Stop");

            }
            else {
                exoPlayer.stop();

                playbackPosition = exoPlayer.getCurrentPosition();
                currentItem = exoPlayer.getCurrentMediaItemIndex();
                playWhenReady = exoPlayer.getPlayWhenReady();
//                    exoPlayer.release();
                playerViewButton.setText("Play");
            }
        });

        /*
        WebView
         */

        webView = findViewById(R.id.webView);

        String videoId = "Hw0Jeq42FNU";

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:player.playVideo()");
            }
        });


        webView.loadData( "<html>" +
                        "<body>" +
                        "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"
                        + videoId + "?enablejsapi=1\" frameborder=\"0\" allowfullscreen>" +
                        "</iframe>" +
                        "</body>" +
                        "</html>",
                "text/html",
                "utf-8");


    }


}