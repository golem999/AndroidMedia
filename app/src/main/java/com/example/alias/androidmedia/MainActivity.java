package com.example.alias.androidmedia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {


    LinearLayout layout, layout_click;
    FrameLayout mainLay, panel;
    ProgressBar pb;
    TextView label;
    Player player;
    VideoX video;
    PopupWindow popup;
    MovieModelContainer films; //список фильмов
    final int popupDelay = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout) findViewById(R.id.videoContainer);
        mainLay = (FrameLayout) findViewById(R.id.mainFrame);

        video = new VideoX(this);
        player = new Player(video, mainLay, layout);
        video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                Log.d("log", "VIDEO ERROR: i:"+i+ " i2:"+i2);
                if(mediaPlayer.MEDIA_ERROR_UNKNOWN==i) Log.d("log", "error unknown");
                return false;
            }
        });


        layout.addView(video);

        setControls();
        createTimer();


        changeFilmList();
        popupBtn = (ImageButton) findViewById(R.id.media_menu);
        configurePopup(popupBtn);
    }

    ImageButton popupBtn;
    void createTimer() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                closePanel();
                Log.d("log", "close this pls");

            }
        };

        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    for (int i=0;i<5;i++){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("log", "i:"+i);
                        if(player.isActive){
                            player.isActive = false;
                            Log.d("log", "is active");

                            break;
                        }
                        if(i==4){
                            if(panelAlive) handler.sendEmptyMessage(0);

                        }

                    }
                }
            }
        });
        thr.start();
    }
    static boolean panelAlive = true, isAnimating=false;

    private void closePanel(){
        if(isAnimating) return; // block new animations while other animating
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_up_label);
        final Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.slide_down_panel);
        isAnimating = true;
        label.startAnimation(anim);
        label.setVisibility(View.INVISIBLE);

        panel.startAnimation(anim2);
        panel.setVisibility(View.INVISIBLE);

        pb.startAnimation(anim2);
        pb.setVisibility(View.INVISIBLE);

        panelAlive = false;

        //открыть доступ к анимации спустя ~2сек
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isAnimating = false;
            }
        }).start();

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                closePanel();
            }
        };

    }
    private void openPanel(){
        if(isAnimating) return;
        isAnimating = true;
        Log.d("log","start");
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_down_label);
        final Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.slide_up_panel);
        label.startAnimation(anim);
        label.setVisibility(View.VISIBLE);

        panel.startAnimation(anim2);
        panel.setVisibility(View.VISIBLE);

        pb.startAnimation(anim2);
        pb.setVisibility(View.VISIBLE);

        panelAlive = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isAnimating = false;
            }
        }).start();
    }

    private void changeFilmList() {
        films = new MovieModelContainer();
        films.setCurrent(1);
        films.addItem("Небо", "http://testapi.qix.sx/video/sky.mp4");
        films.addItem("Юмор", "http://testapi.qix.sx/video/mamahohotala.mp4");
        //films.addItem("test", "http://www.ebookfrenzy.com/android_book/movie.mp4");

    }

    ImageButton longBack, longForward;

    private void setControls() {
        player.setPlayButton((ImageButton) findViewById(R.id.media_play), getResources().getDrawable(R.drawable.panel_play), getResources().getDrawable(R.drawable.panel_stop));
        player.setProgressBar((ProgressBar) findViewById(R.id.media_progress));
        player.setVolumeButton((ImageButton) findViewById(R.id.media_volume), getResources().getDrawable(R.drawable.panel_volume), getResources().getDrawable(R.drawable.panel_volume_off));

        longBack = (ImageButton) findViewById(R.id.media_long_previous);
        longForward = (ImageButton) findViewById(R.id.media_long_next);

        label = (TextView) findViewById(R.id.video_label);
        panel = (FrameLayout) findViewById(R.id.panel_frame);
        pb = (ProgressBar) findViewById(R.id.media_progress);

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.waiting) return;
                String s = (String) view.getTag();
                video.stopPlayback();
                video.pause();

                if(s.equals("long_back")) films.getPrevious();
                if(s.equals("long_forward")) films.getNext();

                onMovieChanged();
                player.isActive = true;
            }
        };
        longBack.setOnClickListener(listener);
        longForward.setOnClickListener(listener);

        layout_click = (LinearLayout) findViewById(R.id.video_click_lay);
        layout_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(panelAlive) closePanel();
                else openPanel();
            }
        });
    }


    private void showPopup() {
        //get location
        //DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        //int x = 78;
       // int y = displaymetrics.heightPixels - popup.getHeight() - 105;

       // popup.showAtLocation(layout, Gravity.NO_GRAVITY, 0, 0);
        popup.showAsDropDown(popupBtn, -60, 0);

        player.isActive = true;
    }

    private void configurePopup(ImageButton btn) {
        Activity context = MainActivity.this;
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup, viewGroup);

        // Creating the PopupWindow
        popup = new PopupWindow(this);
        popup.setContentView(layout);
        popup.setWidth(Integer.parseInt(context.getResources().getString(R.string.popup_width)));
        popup.setHeight(Integer.parseInt(context.getResources().getString(R.string.popup_height)));
        popup.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popup.setFocusable(true);

        //creating ListView
        ListView lv = (ListView) layout.findViewById(R.id.menu_listView);
        String[] from = {"name"};
        int[] to = {R.id.popup_listView_item_text};
        SimpleAdapter adapter = new SimpleAdapter(context, films.getArrayList(), R.layout.popup_list_item, from, to);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view.findViewById(R.id.popup_listView_item_text);
                films.setCurrent(films.getId(tv.getText()+""));
                onMovieChanged();
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();

                //auto closing
                final Handler limitHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        popup.dismiss();
                    }
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(popupDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        limitHandler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });


    }

    private void onMovieChanged(){
        label.setText(films.getName());
        player.setSource(films.getUrl());

        if(films.isFirst()) longBack.setVisibility(View.INVISIBLE);
        else longBack.setVisibility(View.VISIBLE);
        if(films.isLast()) longForward.setVisibility(View.INVISIBLE);
        else longForward.setVisibility(View.VISIBLE);
    }


}


