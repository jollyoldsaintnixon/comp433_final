package com.example.finalproject;

import static com.example.finalproject.MainActivity.DATE_COL;
import static com.example.finalproject.MainActivity.IMAGE_COL;
import static com.example.finalproject.MainActivity.LETTER_COL;
import static com.example.finalproject.MainActivity.TABLE_NAME;
import static com.example.finalproject.MainActivity.TIME_COL;
import static com.example.finalproject.MainActivity.db;

import androidx.activity.result.ActivityResultCaller;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Game_Board_Activity extends AppCompatActivity implements ActivityResultCaller {

    public static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
//    public static char[] ALPHABET = "MHZ".toCharArray();
//    public static int TOTAL_COLUMNS = 3;
//    public static int TOTAL_ROWS = 9;
    public static int TOTAL_COLUMNS = 3;
    public static int TOTAL_ROWS = 1;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int QUERIED_LABEL_DESCRIPTIONS = 3;
    static final String SAVED_INSTANCE_STATE = "savedInstanceState";
    static final String SELECTED_LETTER = "SELECTED_LETTER";
    static final String IMAGE_BYTE_ARRAY = "IMAGE_BYTE_ARRAY";
    static final String MATCHED = "MATCHED";

    protected LinearLayout backboard;
    protected ScrollView scrollView;
    protected GridLayout gridLayout;
//    ListView listView0;
    protected TextView letterTv;
    protected Chronometer timer;
    protected MediaPlayer mediaPlayer;
    protected Bitmap current_bitmap;
    protected ImageView currentThumbnail;
    protected ImageView bigPhoto;
    protected TextView matchQuestion;
    protected TextView matchAnswer;

    protected String remaining_alphabet;
    protected String receivedLetter;
    protected String selectedLetter;
    protected String desiredLetter;
    protected boolean matched = false;
    protected long elapsedMillis = 0;
    protected long currentMillis = 0;
    public int correctCount = 0;
//    public ArrayList<TextView> textViewList = new ArrayList<>(ALPHABET.length);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        remaining_alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        remaining_alphabet = "MHZ";

        mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.song);
        playSong();

        backboard = findViewById(R.id.game_backboard);

        bigPhoto = findViewById(R.id.big_photo);
        bigPhoto.setVisibility(View.GONE);
        matchQuestion = findViewById(R.id.match_question);
        matchQuestion.setVisibility(View.GONE);
        matchAnswer = findViewById(R.id.match_text);
        matchAnswer.setVisibility(View.GONE);
//        imageViewList = new ArrayList<>(26);
//        imageByteArrays = new byte[26][];

        timer = findViewById(R.id.timer);
        timer.setFormat("Elapsed Time: %s");
        timer.setTextSize(40.0F);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        letterTv = findViewById(R.id.desired_letter_text);

        Button backButton = findViewById(R.id.exit);
        Context context = this.getApplicationContext();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Intent backIntent = new Intent(context, MainActivity.class);
                startActivity(backIntent);
            }
        });

        scrollView = new ScrollView(this);

        gridLayout = new GridLayout(this);
        gridLayout.setRowCount(TOTAL_ROWS);
        gridLayout.setColumnCount(TOTAL_COLUMNS);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        scrollView.addView(gridLayout);

        backboard.addView(scrollView);
        desiredLetter = chooseLetter();
        makeCells();
    }

    private void setBackground(byte[] byteArray) {
        for (int i=0; i<gridLayout.getChildCount(); i++) {
            View cell = gridLayout.getChildAt(i);
            ImageView imageView = cell.findViewById(R.id.photo_0);
            if (imageView.getTag().equals(receivedLetter)) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                imageView.setImageBitmap(bitmap);
            }
        }
    }


    private String chooseLetter() {
        int idx = (int) Math.floor(Math.random() * this.remaining_alphabet.length());
        return String.valueOf(remaining_alphabet.charAt(idx));
    }

    private void animate() {
        int idx = (int) Math.floor(Math.random() * this.remaining_alphabet.length());
        desiredLetter = String.valueOf(remaining_alphabet.charAt(idx));
        letterTv.setText("OK, let's do " + desiredLetter + "!");
        Log.v("take2", "desiredLetter: " + desiredLetter );

        int textViewIdx;
        String alphaString = String.valueOf(ALPHABET);
        textViewIdx = alphaString.indexOf(desiredLetter);
//        TextView spinningTextView = textViewList.get(textViewIdx);
//
//        Animation rotateer = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotator);
//        rotateer.setRepeatCount(Animation.INFINITE);
//        spinningTextView.setAnimation(rotateer);
    }

    private void flashLetter(TextView letter) {
        Animation rotateer = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotator);
        rotateer.setRepeatCount(Animation.INFINITE);
        letter.setAnimation(rotateer);
    }

    private void playSong() {
        new Thread() {
            public void run() {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }.start();
    }

    private void makeCells() {
//        new Thread() {
//            public void run() {
//        if (state.getBoolean(MATCHED)) {
//            StringBuilder sb = new StringBuilder(state.getString(REMAINING_ALPHABET));
//            sb.deleteCharAt(sb.indexOf(String.valueOf(state.getChar(SELECTED_LETTER))));
//            this.remaining_alphabet = sb.toString();
//            Log.v("ABC", state.getString(REMAINING_ALPHABET));
//            state.putString(REMAINING_ALPHABET, this.remaining_alphabet);
//        }
        int desiredLetterInt = String.valueOf(ALPHABET).indexOf(desiredLetter);

        for (int i = 0, col = 0, row = 0; i< ALPHABET.length; i++, col++) {
            if (col == TOTAL_COLUMNS) {
                col = 0;
                row++;
            }
            View cell = getLayoutInflater().inflate(R.layout.cell2, null);
//            cell.setTag(new Integer(tag));
            TextView letter = (TextView) cell.findViewById(R.id.letter_text_0);
            letter.setText(String.valueOf(ALPHABET[i]));
            if ((TOTAL_COLUMNS * row) + col == desiredLetterInt) { // it's our guy
                flashLetter(letter);
            }

            ImageView photo = (ImageView) cell.findViewById(R.id.photo_0);
            int tagIdx = (TOTAL_COLUMNS * row) + col;
            char strTag = String.valueOf(ALPHABET).charAt(tagIdx);
//            photo.setTag(new Integer(tag));
            photo.setTag(String.valueOf(strTag));

//            photo.setOnClickListener(new Snapper(photo, photo.getContext()));
            /// OLD START
            Context gameActivityContext = this.getApplicationContext();
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedLetter = (String) photo.getTag();
                    if (selectedLetter.equals(desiredLetter)) {
                        currentMillis = SystemClock.elapsedRealtime() - timer.getBase();
                        elapsedMillis += currentMillis;
                        timer.stop();
                        currentThumbnail = photo;
                        // start a whole new intent?
//                        Intent photoAnaylzerIntent = new Intent(gameActivityContext, PictureAnalyzerActivity.class);
//                        photoAnaylzerIntent.putExtra(SELECTED_LETTER, selectedLetter);
//                        photoAnaylzerIntent.putExtra(SAVED_INSTANCE_STATE, state);
//                        startActivity(photoAnaylzerIntent);
                        Intent snapPic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (snapPic.resolveActivity(getPackageManager()) != null) {
                            try {
                                Log.v("HEYO", "attempting the startActivityForResult");
                                startActivityForResult(snapPic, REQUEST_IMAGE_CAPTURE);
                            } catch (ActivityNotFoundException e) {
                                Log.v("HEYO -- ERROR", "failed starting camera activity intent: " + e.getMessage());
                            }
                            Log.v("HEYO", "passed the startActivityForResult");
                        } else {
                            Log.v("HEYO", "package manager did not resolve");
                        }
                    } else {
                        makeToast("No silly, we want to find " + desiredLetter + "!");
                    }
                }
            }); //// OLD END

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setGravity(Gravity.CENTER);
            params.columnSpec = GridLayout.spec(col);
            params.rowSpec = GridLayout.spec(row);
            cell.setLayoutParams(params);
            Log.v("asdf", String.valueOf(ALPHABET[i]));
            gridLayout.addView(cell);

        }

    }

    private void victory() {
        for (int i = 0; i< ALPHABET.length; i++) {
            View cell = gridLayout.getChildAt(i);
            TextView textView = cell.findViewById(R.id.letter_text_0);
            if (textView.getText().equals(desiredLetter)) {
                textView.clearAnimation();
                break;
            }
        }
        Snackbar snackbar = Snackbar.make(backboard, "All Done!  You got " + correctCount + " out of " + ALPHABET.length + " correct.\n" + "Total Time: " + TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) + " minutes and " + (TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedMillis))) + " seconds.", BaseTransientBottomBar.LENGTH_INDEFINITE);
        Intent backToHome = new Intent(this, MainActivity.class);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                startActivity(backToHome);
            }
        });
        snackbar.show();
    }


    private void swapVis() {
        if (gridLayout.getVisibility() == View.GONE) {
            gridLayout.setVisibility(View.VISIBLE);
            bigPhoto.setVisibility(View.GONE);
            matchAnswer.setVisibility(View.GONE);
            matchQuestion.setVisibility(View.GONE);
        } else {
            gridLayout.setVisibility(View.GONE);
            bigPhoto.setVisibility(View.VISIBLE);
            matchAnswer.setVisibility(View.VISIBLE);
            matchQuestion.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("HEYO", "beginning of result");
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("HEYO", "passed the call to super");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            gridLayout.setVisibility(View.GONE);
            bigPhoto.setVisibility(View.VISIBLE);
            matchAnswer.setVisibility(View.VISIBLE);
            matchQuestion.setVisibility(View.VISIBLE);

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            bigPhoto.setImageBitmap(imageBitmap);
//            currentThumbnail.setImageBitmap(imageBitmap);
            current_bitmap = imageBitmap;
//

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        myVisionTester(current_bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            //            Image myimage = new Image();
//            myimage.encodeContent(bout.toByteArray());
        }
        Log.v("HEYO", "passed the call to super");
    }

    void myVisionTester(Bitmap bitmap) throws IOException {
        //1. ENCODE image.
        Log.v("HEYO", "in myVisionTester endoce");
//        Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.b1)).getBitmap();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bout);
        Image myimage = new Image();
        myimage.encodeContent(bout.toByteArray());

        //2. PREPARE AnnotateImageRequest
        Log.v("HEYO", "in myVisionTester Prepare");
        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
        annotateImageRequest.setImage(myimage);
        Feature f = new Feature();
        f.setType("LABEL_DETECTION");
        f.setMaxResults(5);
        List<Feature> lf = new ArrayList<Feature>();
        lf.add(f);
        annotateImageRequest.setFeatures(lf);
//        Log.v("HEYO", "in myVisionTester");

        //3.BUILD the Vision
        Log.v("HEYO", "in myVisionTester build");
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(new VisionRequestInitializer("AIzaSyDP9S1JotQFKKtC12n5zewBZejjcWihp6o"));
        Vision vision = builder.build();

        //4. CALL Vision.Images.Annotate
        Log.v("HEYO", "in myVisionTester call");
        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
        List<AnnotateImageRequest> list = new ArrayList<AnnotateImageRequest>();
        list.add(annotateImageRequest);
        batchAnnotateImagesRequest.setRequests(list);
        Vision.Images.Annotate task = vision.images().annotate(batchAnnotateImagesRequest);
        BatchAnnotateImagesResponse response = task.execute();
        parseVisionResponse(response);
//        Log.v("HEYO", response.toPrettyString());
    }

    private void parseVisionResponse(BatchAnnotateImagesResponse response) {
        List<AnnotateImageResponse> chosenResponse = response.getResponses();
        for (int i = 0; i<chosenResponse.size(); i++) {
            AnnotateImageResponse annotateImageResponse = chosenResponse.get(i);
            for (int j = 0; j<QUERIED_LABEL_DESCRIPTIONS; j++) {
                String description = annotateImageResponse.getLabelAnnotations().get(j).getDescription();
                if (description != null && String.valueOf(Character.toUpperCase(description.charAt(0))).equals(selectedLetter) ) {
                    Log.v("HEYO", "we have a match.  descrpition: " + description);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            matchAnswer.setText("we have a match. Description: " + description);
                            matched = true;
                            correctCount++;
                            currentThumbnail.setImageBitmap(current_bitmap);
                            remaining_alphabet = remaining_alphabet.replace(desiredLetter, "");

                            ByteArrayOutputStream bout = new ByteArrayOutputStream();
                            current_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bout);
                            byte[] currentbyteArray = bout.toByteArray();
                            addEntry(currentbyteArray);

                            makeSnack("good job!");
                        }
                    });
                    return;
                }
            }
//            Log.v("HEYO", "no match.  description: " + description);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    matched = false;
                    matchAnswer.setText("no match :/");
//                    Drawable frowny = getResources().getDrawable(R.drawable.frowny);
//                    Bitmap bitmap = ((BitmapDrawable)frowny).getBitmap();
//                    currentThumbnail.setImageBitmap(bitmap);
                    makeSnack("better luck next time!");
                }
            });
        }
    }

    private void makeToast(CharSequence text) {
        Snackbar snackbar = Snackbar.make(backboard, text, BaseTransientBottomBar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void makeSnack(CharSequence text) {
        Snackbar snackbar = Snackbar.make(backboard, text, BaseTransientBottomBar.LENGTH_INDEFINITE);
        Intent backToGameActivity = new Intent(this, Game_Board_Activity.class);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                if (matched) {
                    timer.setBase(SystemClock.elapsedRealtime());
                    newLetterDance();
                } else {
                    timer.setBase(SystemClock.elapsedRealtime() - currentMillis);
                }
                timer.start();
                swapVis();
                matchAnswer.setText("Thinking...");
            }
        });
        snackbar.show();
    }

    private void newLetterDance() {
        if (remaining_alphabet.length() == 0) {
            victory();
        } else {
            String newLetter = chooseLetter();
            for (int i = 0; i< ALPHABET.length; i++) {
                View cell = gridLayout.getChildAt(i);
                TextView textView = cell.findViewById(R.id.letter_text_0);
                if (textView.getText().equals(desiredLetter)) {
                    textView.clearAnimation();
                }
                if (textView.getText().equals(newLetter)) {
                    flashLetter(textView);
                }
            }
            desiredLetter = newLetter;
        }
    }

    public void addEntry(byte[] image) throws SQLiteException {
        long seconds = System.currentTimeMillis() / 1000l;
        ContentValues cv = new  ContentValues();
        cv.put(LETTER_COL,    selectedLetter);
        cv.put(IMAGE_COL,   image);
        cv.put(TIME_COL,   currentMillis);
        cv.put(DATE_COL,   seconds);
        db.insert( TABLE_NAME, null, cv );
    }
}
