package com.example.finalproject;

import static com.example.finalproject.Game_Board_Activity.ALPHABET;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class GraphView extends View {

//    ArrayList<Float> points;
    int divisions = ALPHABET.length - 1;
    int yDivisions = 4;
    int max = 100;
    int min = 0;
    double[] coords = new double[divisions+1];
    int radius = 15;
    Paint bluePaint = new Paint();

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bluePaint.setColor(Color.BLUE);
//        points = new ArrayList<Float>();
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

//    public void clear() {
//        points.clear();
//    }

    public void addPoint(int[] coord) {
        coords[coord[0]] = (double) coord[1];
//        while (points.size() > divisions) {
//            points.remove(0);
//        }
//        points.add(f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        max = findMax();
        super.onDraw(canvas);

        int width = this.getWidth();
        int height = this.getHeight();
        Paint paint = new Paint();
        paint.setTextSize(35);
        paint.setColor(Color.RED);
        int box_left = 130;
        int box_top = 100;
        int box_right = width - 60;
        int box_bottom = height -50;
        int box_width = box_right - box_left;
        int box_height = box_bottom - box_top;
        int box_dx = box_width / divisions;
        int box_dy = box_height / divisions;
        int increment = (max - min) / divisions;
        canvas.drawRect(box_left, box_top, box_right, box_bottom, paint);
        paint.setColor(Color.BLACK);
        for (int i=0; i<divisions+1; i++) {
            canvas.drawText(String.valueOf(ALPHABET[i]), box_left + (i*box_dx), box_bottom + 25, paint); // x axis
            canvas.drawLine(box_left + (i * box_dx), box_top, box_left + (i * box_dx), box_bottom, paint);
//            if (i%2 == 0) {
//                canvas.drawText(String.valueOf(round(((max - (i * increment)) / 1000.0), 1)), box_left-65, box_top + (i*box_dy), paint); // y axis
//                canvas.drawLine(box_right, box_top + (i * box_dy), box_left, box_top + (i * box_dy), paint);
//            }
        }
        int yIncrement = (max - min) / yDivisions;
        int yBox_dx = box_width / yDivisions;
        int yBox_dy = box_height / yDivisions;
        for (int i=0; i<=yDivisions; i++) {
            canvas.drawText(String.valueOf(round(((max - (i * yIncrement)) / 1000.0), 1)), box_left-75, box_top + (i*yBox_dy), paint); // y axis
            canvas.drawLine(box_right, box_top + (i * yBox_dy), box_left, box_top + (i * yBox_dy), paint);
        }
        canvas.drawText("Letters", width/2 -60, box_top - 25, paint);
//        canvas.drawText("Letters", width/2 -5, box_bottom +35, paint);
        plotLines(coords, canvas, box_dx, box_dy, box_left, box_top, box_bottom - box_top, bluePaint, max-min, max);
        paint.setColor(Color.BLACK);
        String values = "Average Times (secs)";
//        paint.getTextBounds(values, 0, values.length(), rect);
        int x = 20;
        int y = (height/3) + 205;
        paintString(canvas, paint, values, -90, x, y);
        x = width/2 - x - 65;
        y = y - box_top ;
//        String numbers = "Random Numbers";
//        paintString(canvas, paint, numbers, 90, x, y);
        invalidate();
//        canvas.translate(-x, -y);
//        canvas.rotate(90);
//        canvas.drawText("Numbers", 5, height/4,paint);
//        canvas.drawText("These are examples of numbers.", width/2, 100 , paint);
//        canvas.drawText("Index", (float) width / 2, (float) (height/2) + 15, paint);
////        canvas.drawCircle(cx, cy, radius, paint);
//        String rotatedtext = "Rotated helloandroid :)";
////Draw bounding rect before rotating text:
//        paint.setColor(Color.GREEN);
//        paint.getTextBounds(rotatedtext, 0, rotatedtext.length(), rect);
//        canvas.translate(x, y);
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawText(rotatedtext , 0, 0, paint);
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawRect(rect, paint);
//        canvas.translate(-x, -y);
//        paint.setColor(Color.RED);
//        canvas.rotate(-45, x + rect.exactCenterX(),y + rect.exactCenterY());
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawText(rotatedtext, x, y, paint);
////        radius -= dx;
////        if (radius < 0 || radius > this.getWidth()/2) {
////            dx = -dx;
////        }
//        paint.setColor(Color.BLUE);
////        canvas.drawText("HENRY!", cx, cy, paint);
////        postInvalidate();
//        invalidate();
    }

    private int findMax() {
        int max = 0;
        for (int i=0; i<coords.length; i++) {
            if ((int) coords[i] > max) {
                max = (int) coords[i];
            }
        }
        return max;
    }

    public void paintString(Canvas canvas, Paint paint, String str, int rotate, int x, int y) {
        Log.v("PARAMS", "X: " + x);
        Log.v("PARAMS", "Y: " + y);
        Rect rect = new Rect();
//        Log.v("PARAMS", "rect then: " + rect.width());
        paint.getTextBounds(str, 0, str.length(), rect);
//        Log.v("PARAMS", "rect now: " + rect.width());
//        x = x + (rect.width() / 2); // center it up
        canvas.translate(x,y);
        canvas.rotate(rotate, rect.left, rect.exactCenterY());
        canvas.drawText(str, 0, 0, paint);
    }

    private void plotLines(double[] coords, Canvas canvas, int dx, float dy, int left,
                           int top, int height, Paint paint, double local_span, double local_max) {
        for (int i=0; i<coords.length; i++) {
            double val = (double) coords[i];
            double x = (i * dx) + left;
            double raw_y = (local_max - val) / local_span;
            double y = (height * raw_y) + top;
//            coords[i] = new double[] {x, y};
            if (val != 0) {
                canvas.drawCircle((float) x, (float) y, radius, paint);
//                canvas.drawline
//                canvas.drawLine((float) coords[i-1][0],(float) coords[i-1][1],
//                        (float)coords[i][0],(float) coords[i][1], paint);
            }
        }
    }
//    plotLines(points, canvas, box_dx, box_dy, box_left, box_top, box_bottom - box_top, bluePaint, max-min, max);
//    public void drawPoint(int[] coords) {
//        double x = (coords[0] * dx + left);
//        double raw_y = (local_max - coords[1]) / local_span;
//        double y = (height * raw_y) + top;
//        canvas.drawCircle((float) x, (float) y, radius, paint);
//    }

    public void paintString(Canvas canvas, Paint paint, String str, int rotate) {
        paintString(canvas, paint, str, 0 ,0, 0);
    }

    public static double round (double value, int precision) { // from https://stackoverflow.com/questions/22186778/using-math-round-to-round-to-one-decimal-place
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
