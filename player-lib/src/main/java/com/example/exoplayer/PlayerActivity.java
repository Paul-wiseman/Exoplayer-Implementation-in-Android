/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
* limitations under the License.
 */
package com.example.exoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.PlaybackStatsListener;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;


/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {
  private SimpleExoPlayer player;
  private PlaybackStateListener playbackStateListener;
  private static final String TAG = PlayerActivity.class.getName();


  private PlayerView playerView;
  private Boolean playWhenReady = true;
  private int currentWindow = 0;
  private long playbackPosition = 0;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);

    playerView = findViewById(R.id.video_view);

    playbackStateListener = new PlaybackStateListener();
  }

  @Override
  protected void onStart() {
    super.onStart();

    if(Util.SDK_INT >= 24){
      initializePlayer();
    };
  }

  @Override
  protected void onStop() {
    super.onStop();
    if(Util.SDK_INT >= 24){
      releasePlayer();
    };
  }

  @Override
  protected void onPause() {
    super.onPause();
    if(Util.SDK_INT < 24){
      releasePlayer();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    hideSystemUi();
            if(Util.SDK_INT < 24 || player == null){
              initializePlayer();
            }
  }

  private void initializePlayer(){
    if(player == null){
      DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
      trackSelector.setParameters(
              trackSelector.buildUponParameters().setMaxVideoSizeSd()
      );
      player = new SimpleExoPlayer.Builder(this)
              .setTrackSelector(trackSelector)
              .build();
      player.addListener(playbackStateListener);
      player.prepare();
    }
//    player = new SimpleExoPlayer.Builder(this).build();

    playerView.setPlayer(player);
//    MediaItem mediaItem = new MediaItem.Builder()
//            .setUri(getString(R.string.media_url_dash))
//            .setMimeType(MimeTypes.APPLICATION_MPD)
//            .build();

    MediaItem mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4));
    MediaItem secondMediaItem = MediaItem.fromUri(getString(R.string.media_url_mp3));
    player.setMediaItem(mediaItem);
    player.addMediaItem(secondMediaItem);
    player.setPlayWhenReady(playWhenReady);
    player.seekTo(currentWindow, playbackPosition);
    player.prepare();
   }

  @SuppressLint("InlinedApi")
  private void hideSystemUi(){
    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }

  private void releasePlayer(){
    if(player != null){
      playWhenReady = player.getPlayWhenReady();
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      player.removeListener(playbackStateListener);
      player.release();
      player = null;
    }
  }

  private class PlaybackStateListener implements Player.EventListener {

    @Override
    public void onPlaybackStateChanged(int playbackState) {
      String stateString;
      switch (playbackState) {
        case ExoPlayer.STATE_IDLE:
          stateString = "ExoPlayer.STATE_IDLE      -";
          break;
        case ExoPlayer.STATE_BUFFERING:
          stateString = "ExoPlayer.STATE_BUFFERING -";
          break;
        case ExoPlayer.STATE_READY:
          stateString = "ExoPlayer.STATE_READY     -";
          break;
        case ExoPlayer.STATE_ENDED:
          stateString = "ExoPlayer.STATE_ENDED     -";
          break;
        default:
          stateString = "UNKNOWN_STATE             -";
          break;
      }
      Log.d(TAG, "changed state to " + stateString);
    }
  }
/**
 * the below code is an example of how you can make a fragment go full screen
 * But if it is an activity you want to make fullscreen, then you don't need to pass in an activity into the function
 * this code is commented out because i want to store how to make a fragment full screen
 */
//  @Suppress("DEPRECATION")
//  @SuppressLint("ObsoleteSdkInt")
//  private fun setFullscreen(activity: Activity?) {
//    activity?.let{
//      when {
//        /*I added this case below. And the view looks very weird. Why??*/
//        Build.VERSION.SDK_INT >= 30 -> {
//          it.window.setDecorFitsSystemWindows(false)
////                ViewCompat.setOnApplyWindowInsetsListener(){view, windowInsets ->
////                    val sysWindow = windowInsets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.ime())
////                }
//          val controller: WindowInsetsController? = it.window.insetsController
//          controller?.let { insetsController ->
//                  insetsController.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
//            insetsController.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
////                    insetsController.systemBarsBehavior = BEHAVIOR_SHOW_BARS_BY_SWIPE
//          }
//        }
//        /*The code below worked for years. Why they change working stuff?*/
//        Build.VERSION.SDK_INT > 10 -> {
//          // Enables regular immersive mode.
//          // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
//          // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//          it.window.decorView.systemUiVisibility = (
//                  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//          // Set the content to appear under the system bars so that the
//          // content doesn't resize when the system bars hide and show.
//          or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//          or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//          or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//          // Hide the nav bar and status bar
//          or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//          or View.SYSTEM_UI_FLAG_FULLSCREEN
//          or View.SYSTEM_UI_FLAG_LOW_PROFILE
//                        )
//          (it as? AppCompatActivity)?.supportActionBar?.hide()
//        }
//            else -> {
//          it.window
//                  .setFlags(
//                          WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                          WindowManager.LayoutParams.FLAG_FULLSCREEN
//                  )
//        }
//      }
//    }
//  }

}
