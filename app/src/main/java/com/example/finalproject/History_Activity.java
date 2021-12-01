package com.example.finalproject;

import static com.example.finalproject.MainActivity.LETTER_COL;
import static com.example.finalproject.MainActivity.TABLE_NAME;
import static com.example.finalproject.MainActivity.TIME_COL;
import static com.example.finalproject.MainActivity.db;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class History_Activity extends AppCompatActivity {

//    List<Integer[]> A_array = new int[100];
//    List<Integer[]> B_array = new int[100];
//    List<Integer[]> C_array = new int[100];
//    List<Integer[]> D_array = new int[100];
//    List<Integer[]> E_array = new int[100];
//    List<Integer[]> F_array = new int[100];
//    List<Integer[]> G_array = new int[100];
//    List<Integer[]> H_array = new int[100];
//    List<Integer[]> I_array = new int[100];
//    List<Integer[]> J_array = new int[100];
//    List<Integer[]> K_array = new int[100];
//    List<Integer[]> L_array = new int[100];
//    List<Integer[]> M_array = new int[100];
//    List<Integer[]> N_array = new int[100];
//    List<Integer[]> O_array = new int[100];
//    List<Integer[]> P_array = new int[100];
//    List<Integer[]> Q_array = new int[100];
//    List<Integer[]> R_array = new int[100];
//    List<Integer[]> S_array = new int[100];
//    List<Integer[]> T_array = new int[100];
//    List<Integer[]> U_array = new int[100];
//    List<Integer[]> V_array = new int[100];
//    List<Integer[]> W_array = new int[100];
//    List<Integer[]> X_array = new int[100];
//    List<Integer[]> Y_array = new int[100];
//    List<Integer[]> Z_array = new int[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        queryDb();
    }

    private void queryDb() {
//        Cursor cursor = db.rawQuery("SELECT * from "+ TABLE_NAME, null);
        Cursor cursor = db.rawQuery("SELECT " + LETTER_COL + ", avg(time) from "+ TABLE_NAME + " GROUP BY " + LETTER_COL, null);
        cursor.moveToFirst();
        for (int i=0;i< cursor.getCount();i++) {
            String letter = cursor.getString(0);
            int avg = cursor.getInt(1);
//            int time = cursor.getInt(2);
//            long seconds = cursor.getLong(3);
//            Date date = new Date(seconds * 1000);
//            SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH);
//            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//            String formattedDate = sdf.format(date);
//            System.out.println(formattedDate);
//            System.out.println(formattedDate);
            Log.v("DB_TAG", "letter = " + letter);
            Log.v("DB_TAG", "avg = " + avg);
//            Log.v("DB_TAG", "letter = " + time);
//            Log.v("DB_TAG", "letter = " + date);
            cursor.moveToNext();
        }
    }

    private int averageSecs(int[] secs) {
        int sum = 0;
        for (int i=0; i<secs.length; i++) {
            sum += secs[i];
        }
        return sum / secs.length;
    }
}