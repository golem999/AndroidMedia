package com.example.alias.androidmedia;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by alias on 25.04.2015.
 */
public class PlayerControls {

    ImageButton btnPlay;
    Drawable imgPlayOn, imgPlayOff;
    boolean switcherPlay=false;
    private ProgressBar progressBar;
    static int progress, secondaryProgress, maxProgress = 100;
    ImageButton btnVolume;
    Drawable imgVolumeOn, imgVolumeOff;
    boolean switcherVolume;
    MediaPlayer mp;
    public static boolean isActive = false;

    public PlayerControls() {
        mp = new MediaPlayer();
    }

    ///     playButton`s activities start
    //drawables is not required
    public void setPlayButton(ImageButton play, Drawable play_image, Drawable pause_image){
        btnPlay =  play;
        imgPlayOn = play_image;
        imgPlayOff = pause_image;
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playEvent();
            }
        });

    }

    /**
     * call super to switch image
     */
    public void playEvent(){
        if(switcherPlay){
            if(imgPlayOn!=null){
                btnPlay.setImageDrawable(imgPlayOn);
            }
        }else{
            if(imgPlayOff!=null){
                btnPlay.setImageDrawable(imgPlayOff);
            }
        }
        switcherPlay = !switcherPlay;
    }

    public void setProgressBar(ProgressBar progressBar){
        this.progressBar = progressBar;
        this.maxProgress = progressBar.getMax();
    }

    ///     progress bar`s activities start
    public void setProgress(int value){
        progress = value;
        if(progressBar !=null)
        progressBar.setProgress(value);

    }

    public int getProgress(){
        return progress;
    }

    public void setSecondaryProgress(int value){
        secondaryProgress = value;
        if(progressBar !=null)
            progressBar.setSecondaryProgress(value);
    }

    public int getSecondaryProgress(){
        return secondaryProgress;
    }

    ///     volumeButton`s activities start
    public void setVolumeButton(ImageButton button, Drawable volume_on_mode, Drawable volume_off_mode){
        btnVolume = button;
        imgVolumeOn = volume_on_mode;
        imgVolumeOff = volume_off_mode;

        switcherVolume = true;

        btnVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volumeEvent();
            }
        });
    }

    public void volumeEvent(){
        if(switcherVolume){
            if(imgVolumeOn!=null){
                btnVolume.setImageDrawable(imgVolumeOn);
                mp.setVolume(1f,1f);
            }
        }
        else{
            if(imgVolumeOff!=null){
                btnVolume.setImageDrawable(imgVolumeOff);
                try{
                    mp.setVolume(0f,0f);
                } catch (Exception e){}
            }
        }

        switcherVolume = !switcherVolume;
        isActive = true;

    }

    public void setMediaPlayer(MediaPlayer mediaPlayer){
        mp = mediaPlayer;
    }




}


