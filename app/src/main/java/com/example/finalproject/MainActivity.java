package com.example.finalproject;

import static com.example.finalproject.Game_Board_Activity.ALPHABET;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static String DB_NAME = "DATABASE";
    public static String TABLE_NAME = "photo_times";
    public static String LETTER_COL = "letter";
    public static String IMAGE_COL = "blob";
    public static String TIME_COL = "time";
    public static String DATE_COL = "date";
    public static String LABEL_COL = "label";

    private ListView listView;
    public static SQLiteDatabase db;
//    protected String[] salty = new String[] { "ocean", "sea"};
//    protected String[] fresh = new String[] { "pond", "lake"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG)
            StrictMode.enableDefaults();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeDb();
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

    private void makeDb() {
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
//        if (!doesDatabaseExist(getApplicationContext(), DB_NAME)) {
//            for (char c : ALPHABET) {
//                db.execSQL("DROP TABLE IF EXISTS " + c);
//                db.execSQL("create table " + c + " (" +
//                        "time INTEGER," +
//                        "blob BLOB" +
//                        "date INTEGER" +
//                        ")");
//            }
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("create table if not exists " + TABLE_NAME + " (" +
                    "letter TEXT," +
                    "blob BLOB," +
                    "time INTEGER," +
                    "date INTEGER," +
                    "label TEXT" +
                    ")");
//            db.execSQL("insert into menu_items values ('corn dog', 'grill')");
//            db.execSQL("insert into menu_items values ('burger dog', 'grill')");
//            db.execSQL("insert into menu_items values ('ham dog', 'grill')");
//            db.execSQL("insert into menu_items values ('salad', 'not grill')");
//            db.execSQL("insert into menu_items values ('pizza', 'oven')");

//        }
    }

    public static boolean doesDatabaseExist(Context context, String dbName) { // from https://stackoverflow.com/questions/3386667/query-if-android-database-exists/12025733
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
}