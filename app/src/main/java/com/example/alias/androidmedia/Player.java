package com.example.alias.androidmedia;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by alias on 25.04.2015.
 */
public class Player extends PlayerControls {

    static VideoX video;
    FrameLayout frame;
    ProgressBar waiterBar;
    Context context;
    AsyncTask<Void,Integer,Void> progressTask;
    static boolean isPlaying;
    LinearLayout layout;


    public Player(final VideoX video, FrameLayout main, LinearLayout lay) {
        this.video = video;
        this.frame = main;
        this.layout = lay;
        mp = new MediaPlayer();
        context = main.getContext();
        isPlaying=false;

        createWaiter();
        waitingMode(false);

        createThreads();
        progressTask.execute();

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d("log", "prepared");
                waitingMode(false);
                video.seekTo(0);
                video.refreshDrawableState();
                setMediaPlayer(mediaPlayer);
                videoAnimate();
            }
        });


    }

    private void createThreads(){
        progressTask = new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                while (true)
                {
                    int x,y;
                    try {
                        x = video.getCurrentPosition();
                        if (x > 0)
                            x += 133;//временный оффсет из-за непонятности несоответствия тек. позиции с максимальной

                        y = video.getDuration();
                        int p = x * maxProgress / y;
                        setProgress(p);
                        if (p == maxProgress) {

                            publishProgress(p);
                            video.seekTo(0);
                        }
                    }
                    catch (Exception e){}

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            protected void onProgressUpdate(Integer... values) {
                playEvent();
            }
        };
    }

    public boolean waiting = false;
    private void createWaiter(){
        waiterBar = new ProgressBar(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
        params.gravity = Gravity.CENTER;
        waiterBar.setLayoutParams(params);
        waiterBar.setVisibility(View.INVISIBLE);
        frame.addView(waiterBar);
    }

    public void waitingMode(boolean switcher){
        if(switcher) {
            waiterBar.setVisibility(View.VISIBLE);
            if(isPlaying) playEvent();
        }
        else waiterBar.setVisibility(View.INVISIBLE);
        waiting = switcher;

    }

    public void setSource(String url) {

        waitingMode(true);
        video.setVideoURI(Uri.parse(url));
    }

    @Override
    public void playEvent() {

        isActive = true;
        isPlaying = !isPlaying;
        if(isPlaying)
            play();
        else pause();
        super.playEvent();

    }

    public void pause(){
        video.pause();
    }

    public void play(){
        video.start();
    }

    private void videoAnimate(){
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_video);
        video.startAnimation(anim);
    }

}
