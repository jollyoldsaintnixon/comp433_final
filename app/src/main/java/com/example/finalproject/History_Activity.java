package com.example.finalproject;

import static com.example.finalproject.Game_Board_Activity.ALPHABET;
import static com.example.finalproject.Game_Board_Activity.FOUND_LETTERS;
import static com.example.finalproject.Game_Board_Activity.foundLetters;
import static com.example.finalproject.GraphView.round;
import static com.example.finalproject.MainActivity.DATE_COL;
import static com.example.finalproject.MainActivity.LETTER_COL;
import static com.example.finalproject.MainActivity.TABLE_NAME;
import static com.example.finalproject.MainActivity.TIME_COL;
import static com.example.finalproject.MainActivity.db;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

import java.io.ByteArrayInputStream;
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

        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            foundLetters = intentExtras.getBooleanArray(FOUND_LETTERS);
        }

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
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
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
        queryPhotos(letter);
    }

    private void queryPhotos(CharSequence letter) {
        Cursor cursor = db.rawQuery("SELECT * from "+ TABLE_NAME + " WHERE " + LETTER_COL + " LIKE '" + letter + "' ORDER BY " + DATE_COL + " DESC", null);
        cursor.moveToFirst();
        int end = 3;
        if (cursor.getCount() < end) { end = cursor.getCount(); }
        for (int i=0;i<end;i++) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(cursor.getBlob(1));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            int timeSpent = cursor.getInt(2);
            int dateInt = cursor.getInt(3);
            String dateStr = convertToDate(dateInt);
            String label = cursor.getString(4);
            fillViews(bitmap, timeSpent, dateStr, i, label);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        disappear(end);
        int price;
        int matchPrice;
        int count = 0;
        for (int i=0; i<cursor.getCount() - 1; i++) {
            price = cursor.getInt(1);
            String name = cursor.getString(0);
            if (price >= 751) { // price is ordered ascending so at this point we know that there are no more possible matches
                break;
            }
            int difference = 1500 - price;
            for (int j=i+1; j<cursor.getCount(); j++) {
                matchPrice = cursor.getInt(j);
                if (matchPrice <= difference) {
                    count++;
                    Log.v("MY_RESULT","Option #" + count + ": " + name + " for $" + price + " and " + cursor.getString(0) + " for $" + matchPrice);
                } else {
                    break;
                }
            }
            cursor.moveToPosition(i + 1);
        }
    }

    private void disappear(int end) {
        ImageView imageView0 = findViewById(R.id.photo_0);
        TextView textView0 = findViewById(R.id.tv_00);
        ImageView imageView1 = findViewById(R.id.photo_1);
        TextView textView1 = findViewById(R.id.tv_01);
        ImageView imageView2 = findViewById(R.id.photo_2);
        TextView textView2 = findViewById(R.id.tv_02);
        if (end == 0) {
            imageView0.setVisibility(View.GONE);
            imageView1.setVisibility(View.GONE);
            imageView2.setVisibility(View.GONE);
            textView0.setVisibility(View.GONE);
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
        } else if (end == 1) {
            imageView0.setVisibility(View.VISIBLE);
            imageView1.setVisibility(View.GONE);
            imageView2.setVisibility(View.GONE);
            textView0.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
        } else if (end == 2) {
            imageView0.setVisibility(View.VISIBLE);
            imageView1.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.GONE);
            textView0.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.GONE);
        } else {
            imageView0.setVisibility(View.VISIBLE);
            imageView1.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.VISIBLE);
            textView0.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
        }
    }

    private void fillViews(Bitmap bitmap, int timeSpent, String dateStr, int i, String label) {
        double timeSpentSecs = round(timeSpent/1000.0, 1);
        ImageView imageView;
        TextView textView;
        if (i == 0) {
            imageView = findViewById(R.id.photo_0);
            textView = findViewById(R.id.tv_00);
        } else if (i == 1) {
            imageView = findViewById(R.id.photo_1);
            textView = findViewById(R.id.tv_01);
        } else {
            imageView = findViewById(R.id.photo_2);
            textView = findViewById(R.id.tv_02);
        }
        imageView.setImageBitmap(bitmap);
        textView.setText("Time spent collecting: \n" + timeSpentSecs + "seconds" + "\n\nDate Collected: \n" + dateStr + "\n\nLabel: " + label);
    }

    private String convertToDate(int seconds) {
        Date date = new Date(seconds * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void backToMain(View view) {
        Intent backToMainIntent = new Intent(this, MainActivity.class);
        backToMainIntent.putExtra(FOUND_LETTERS, foundLetters);
        startActivity(backToMainIntent);
    }
}