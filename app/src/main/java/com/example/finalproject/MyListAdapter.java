package com.example.finalproject;

import static com.example.finalproject.Game_Board_Activity.ALPHABET;
import static com.example.finalproject.Game_Board_Activity.TOTAL_COLUMNS;
import static com.example.finalproject.Game_Board_Activity.TOTAL_ROWS;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    String desiredLetter;
    ArrayList<TextView> textViewList = new ArrayList<>(TOTAL_ROWS);

    public MyListAdapter(Context game_board_context, String desiredLetter) {
//        Log.v("take2", "desiredLetter at initiaion: " + desiredLetter);
        this.context = game_board_context;
        this.desiredLetter = desiredLetter;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return TOTAL_ROWS;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.v("take2", "desiredLetter at getView: " + desiredLetter);
        if(convertView == null) {
            if (this.context == null) {
                Log.v("mytag", "is maincontext null?");

            }
            convertView = this.inflater.inflate(R.layout.cell, null, false);
        }

        TextView letterText0 = convertView.findViewById(R.id.letter_text_0);
        letterText0.setText(String.valueOf(ALPHABET[position * TOTAL_COLUMNS + 0]));
        String tag0 = String.valueOf(ALPHABET[position * TOTAL_COLUMNS + 0]);
        if (tag0.equals(desiredLetter)) {
            animate(letterText0);
        }
        letterText0.setTag(tag0);

        TextView letterText1 = convertView.findViewById(R.id.letter_text_1);
        letterText1.setText(String.valueOf(ALPHABET[position * TOTAL_COLUMNS + 1]));
        String tag1 = String.valueOf(ALPHABET[position * TOTAL_COLUMNS + 1]);
        letterText1.setTag(tag1);
        if (tag1.equals(desiredLetter)) {
            animate(letterText1);
        }

            TextView letterText2 = convertView.findViewById(R.id.letter_text_2);
            if (position < TOTAL_ROWS - 1) {
                letterText2.setText(String.valueOf(ALPHABET[position * TOTAL_COLUMNS + 2]));
                String tag2 = String.valueOf(ALPHABET[position * TOTAL_COLUMNS + 2]);
                letterText2.setTag(tag2);
                if (tag2.equals(desiredLetter)) {
                    animate(letterText2);
                }
            } else {
                letterText2.setText("!!");
            }


        return convertView;
    }

    private void animate(TextView letter) {
    }

    public TextView getTextView(int i) {

    }
}
