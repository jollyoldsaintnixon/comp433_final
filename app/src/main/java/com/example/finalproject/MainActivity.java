package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    protected String[] salty = new String[] { "ocean", "sea"};
    protected String[] fresh = new String[] { "pond", "lake"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG)
            StrictMode.enableDefaults();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        listView = findViewById(R.id.list_view);
//        listView.setAdapter(new MyListAdapter(this));
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.v("mytag", "hey i created an anonymous onitemclicklistener.  the item that was clicked was #" + position);
//            }
//        });
//        ImageView abc_image = findViewById(R.id.abc_image);
    }

    private void addSoundEffects() {
        Button start_button = findViewById(R.id.start_button);
        MediaPlayer startMediaPlayer = MediaPlayer.create(this, R.raw.sound1);

        Button history_button = findViewById(R.id.history_button);

    }

    public void start_game(View view) {
        makeMediaPlayer(R.raw.sound1);
        Intent start_game_intent = new Intent(this, Game_Board_Activity.class);
        startActivity(start_game_intent);
    }

    private void makeMediaPlayer(int sound) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, sound);
        mediaPlayer.start();
    }

    public void start_history(View view) {
        makeMediaPlayer(R.raw.sound2);
        Intent history_intent = new Intent(this, History_Activity.class);
        startActivity(history_intent);
    }
}