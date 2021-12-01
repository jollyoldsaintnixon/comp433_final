package com.example.finalproject;

import static com.example.finalproject.Game_Board_Activity.ALPHABET;
import static com.example.finalproject.MainActivity.LETTER_COL;
import static com.example.finalproject.MainActivity.TABLE_NAME;
import static com.example.finalproject.MainActivity.TIME_COL;
import static com.example.finalproject.MainActivity.db;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class History_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    GraphView graph;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        graph = findViewById(R.id.graph);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.alphabet, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        queryDb();
    }

    private void queryDb() {
//        Cursor cursor = db.rawQuery("SELECT * from "+ TABLE_NAME, null);
        Cursor cursor = db.rawQuery("SELECT " + LETTER_COL + ", avg(time) from "+ TABLE_NAME + " GROUP BY " + LETTER_COL, null);
        cursor.moveToFirst();
        for (int i=0;i< cursor.getCount();i++) {
            String letter = cursor.getString(0);
            int avg = cursor.getInt(1);
            Log.v("DB_TAG", "letter = " + letter);
            Log.v("DB_TAG", "avg = " + avg);
            addAvg(letter, avg);
//            Log.v("DB_TAG", "letter = " + time);
//            Log.v("DB_TAG", "letter = " + date);
            cursor.moveToNext();
        }
    }

    private void addAvg(String letter, int avg) {
        int x = String.valueOf(ALPHABET).indexOf(letter);
        int[] coord = {x, avg};
        graph.addPoint(coord);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.v("SPINNER_TAG", "id: " + id);
        CharSequence letter = ((MaterialTextView) view).getText();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}