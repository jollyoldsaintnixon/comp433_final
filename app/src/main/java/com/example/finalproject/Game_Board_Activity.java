package com.example.finalproject;

import androidx.activity.result.ActivityResultCaller;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
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

public class Game_Board_Activity extends AppCompatActivity implements ActivityResultCaller {

    public static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public static int TOTAL_COLUMNS = 3;
    public static int TOTAL_ROWS = 9;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int QUERIED_LABEL_DESCRIPTIONS = 3;
    public static String REMAINING_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final String SAVED_INSTANCE_STATE = "savedInstanceState";
    static final String SELECTED_LETTER = "SELECTED_LETTER";
    static final String IMAGE_BYTE_ARRAY = "IMAGE_BYTE_ARRAY";
    static final String MATCHED = "MATCHED";

    protected String remaining_alphabet;
    protected LinearLayout backboard;
    protected ScrollView scrollView;
    ListView listView0;
    protected Chronometer timer;
    protected MediaPlayer mediaPlayer;
    protected Bitmap current_bitmap;
    protected ImageView currentThumbnail;

    protected String receivedLetter;
    protected String selectedLetter;
    protected String desiredLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
//        try {
//            state = extras.getBundle(SAVED_INSTANCE_STATE);
//        } catch (Exception exception) {
//            state = new Bundle();
//            state.putString(REMAINING_ALPHABET, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
//            Log.v("ABC", "blank state");
//        }
//        if (state.getBoolean(MATCHED)) {
//            A_byteArray = state.getByteArray("A");
//            B_byteArray = state.getByteArray("B");
//            C_byteArray = state.getByteArray("C");
//            D_byteArray = state.getByteArray("D");
//            E_byteArray = state.getByteArray("E");
//            F_byteArray = state.getByteArray("F");
//            G_byteArray = state.getByteArray("G");
//            H_byteArray = state.getByteArray("H");
//            I_byteArray = state.getByteArray("I");
//            J_byteArray = state.getByteArray("J");
//            K_byteArray = state.getByteArray("K");
//            L_byteArray = state.getByteArray("L");
//            M_byteArray = state.getByteArray("M");
//            N_byteArray = state.getByteArray("N");
//            O_byteArray = state.getByteArray("O");
//            P_byteArray = state.getByteArray("P");
//            Q_byteArray = state.getByteArray("Q");
//            R_byteArray = state.getByteArray("R");
//            S_byteArray = state.getByteArray("S");
//            T_byteArray = state.getByteArray("T");
//            U_byteArray = state.getByteArray("U");
//            V_byteArray = state.getByteArray("V");
//            W_byteArray = state.getByteArray("W");
//            X_byteArray = state.getByteArray("X");
//            Y_byteArray = state.getByteArray("Y");
//            Z_byteArray = state.getByteArray("Z");
//        }
        setContentView(R.layout.activity_game_board);
        remaining_alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.song);
        playSong();

        backboard = findViewById(R.id.game_backboard);
//        imageViewList = new ArrayList<>(26);
//        imageByteArrays = new byte[26][];

        timer = findViewById(R.id.timer);
        timer.setFormat("Elapsed Time: %s");
        timer.setTextSize(40.0F);
        timer.start();

        TextView letterTv = findViewById(R.id.desired_letter_text);

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

//        scrollView = new ScrollView(this);
//
//        gridLayout = new GridLayout(this);
//        gridLayout.setRowCount(TOTAL_ROWS);
//        gridLayout.setColumnCount(TOTAL_COLUMNS);
//        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
//        scrollView.addView(gridLayout);
        desiredLetter = pickDesiredLetter();
        letterTv.setText("OK, let's do " + desiredLetter + "!");

        Log.v("take2", "desiredLetter: " + desiredLetter );
        listView0 = findViewById(R.id.list_view0);
        listView0.setAdapter(new MyListAdapter(this, desiredLetter));
        listView0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("mytag", "hey i created an anonymous onitemclicklistener.  the item that was clicked was #" + position);
            }
        });
        animate(desiredLetter);
//        makeBundle();
//        makeCells();
    }

    private void animate(String desiredLetter) {
        for (int i=0; i<listView0.getCount(); i++) {
            TextView textview = listView0.getItemAtPosition(i);
            if
        }
        Animation rotateer = AnimationUtils.loadAnimation(context, R.anim.rotator);
        rotateer.setRepeatCount(Animation.INFINITE);
        letter.setAnimation(rotateer);
    }

    private String pickDesiredLetter() {
        int idx = (int) Math.floor(Math.random() * this.remaining_alphabet.length());
        return String.valueOf(remaining_alphabet.charAt(idx));
    }
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putChar("hey", 'r');
//        outState.putByteArray("A", A_byteArray);
//        outState.putByteArray("B", B_byteArray);
//        outState.putByteArray("C", C_byteArray);
//        outState.putByteArray("D", D_byteArray);
//        outState.putByteArray("E", E_byteArray);
//        outState.putByteArray("F", F_byteArray);
//        outState.putByteArray("G", G_byteArray);
//        outState.putByteArray("H", H_byteArray);
//        outState.putByteArray("I", I_byteArray);
//        outState.putByteArray("J", J_byteArray);
//        outState.putByteArray("K", K_byteArray);
//        outState.putByteArray("L", L_byteArray);
//        outState.putByteArray("M", M_byteArray);
//        outState.putByteArray("N", N_byteArray);
//        outState.putByteArray("O", O_byteArray);
//        outState.putByteArray("P", P_byteArray);
//        outState.putByteArray("Q", Q_byteArray);
//        outState.putByteArray("R", R_byteArray);
//        outState.putByteArray("S", S_byteArray);
//        outState.putByteArray("T", T_byteArray);
//        outState.putByteArray("U", U_byteArray);
//        outState.putByteArray("V", V_byteArray);
//        outState.putByteArray("W", W_byteArray);
//        outState.putByteArray("X", X_byteArray);
//        outState.putByteArray("Y", Y_byteArray);
//        outState.putByteArray("Z", Z_byteArray);
//    }

//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
////        imageViewList = savedInstanceState.getParcelableArrayList("imageViewList");
//        A_byteArray = savedInstanceState.getByteArray("A");
//        B_byteArray = savedInstanceState.getByteArray("B");
//        C_byteArray = savedInstanceState.getByteArray("C");
//        D_byteArray = savedInstanceState.getByteArray("D");
//        E_byteArray = savedInstanceState.getByteArray("E");
//        F_byteArray = savedInstanceState.getByteArray("F");
//        G_byteArray = savedInstanceState.getByteArray("G");
//        H_byteArray = savedInstanceState.getByteArray("H");
//        I_byteArray = savedInstanceState.getByteArray("I");
//        J_byteArray = savedInstanceState.getByteArray("J");
//        K_byteArray = savedInstanceState.getByteArray("K");
//        L_byteArray = savedInstanceState.getByteArray("L");
//        M_byteArray = savedInstanceState.getByteArray("M");
//        N_byteArray = savedInstanceState.getByteArray("N");
//        O_byteArray = savedInstanceState.getByteArray("O");
//        P_byteArray = savedInstanceState.getByteArray("P");
//        Q_byteArray = savedInstanceState.getByteArray("Q");
//        R_byteArray = savedInstanceState.getByteArray("R");
//        S_byteArray = savedInstanceState.getByteArray("S");
//        T_byteArray = savedInstanceState.getByteArray("T");
//        U_byteArray = savedInstanceState.getByteArray("U");
//        V_byteArray = savedInstanceState.getByteArray("V");
//        W_byteArray = savedInstanceState.getByteArray("W");
//        X_byteArray = savedInstanceState.getByteArray("X");
//        Y_byteArray = savedInstanceState.getByteArray("Y");
//        Z_byteArray = savedInstanceState.getByteArray("Z");
//    }

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

//    private void makeCells() {
////        new Thread() {
////            public void run() {
//        if (state.getBoolean(MATCHED)) {
//            StringBuilder sb = new StringBuilder(state.getString(REMAINING_ALPHABET));
//            sb.deleteCharAt(sb.indexOf(String.valueOf(state.getChar(SELECTED_LETTER))));
//            this.remaining_alphabet = sb.toString();
//            Log.v("ABC", state.getString(REMAINING_ALPHABET));
//            state.putString(REMAINING_ALPHABET, this.remaining_alphabet);
//        }
//
//        char desiredChar = chooseLetter();
//        int desiredLetterInt = String.valueOf(ALPHABET).indexOf(desiredChar);
//
//        for (int i = 0, col = 0, row = 0; i< ALPHABET.length; i++, col++) {
//            if (col == TOTAL_COLUMNS) {
//                col = 0;
//                row++;
//            }
//            View cell = getLayoutInflater().inflate(R.layout.cell, null);
////            cell.setTag(new Integer(tag));
//            TextView letter = (TextView) cell.findViewById(R.id.letter);
//            letter.setText(String.valueOf(ALPHABET[i]));
//            if ((TOTAL_COLUMNS * row) + col == desiredLetterInt) { // it's our guy
//                flashLetter(letter);
//            }
//
//            ImageView photo = (ImageView) cell.findViewById(R.id.photo);
//            int tag = (TOTAL_COLUMNS * row) + col;
//            photo.setTag(new Integer(tag));
//            setBackground(photo);
//
////            photo.setOnClickListener(new Snapper(photo, photo.getContext()));
//            /// OLD START
//            Context gameActivityContext = this.getApplicationContext();
//            photo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    selectedLetter = ALPHABET[(Integer) photo.getTag()];
//                    if (selectedLetter == desiredLetter) {
//                        currentThumbnail = photo;
//                        // start a whole new intent?
//                        Intent photoAnaylzerIntent = new Intent(gameActivityContext, PictureAnalyzerActivity.class);
//                        photoAnaylzerIntent.putExtra(SELECTED_LETTER, selectedLetter);
//                        photoAnaylzerIntent.putExtra(SAVED_INSTANCE_STATE, state);
//                        startActivity(photoAnaylzerIntent);
////                        Intent snapPic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
////                        if (snapPic.resolveActivity(getPackageManager()) != null) {
////                            try {
////                                Log.v("HEYO", "attempting the startActivityForResult");
////                                startActivityForResult(snapPic, REQUEST_IMAGE_CAPTURE);
////                            } catch (ActivityNotFoundException e) {
////                                Log.v("HEYO -- ERROR", "failed starting camera activity intent: " + e.getMessage());
////                            }
////                            Log.v("HEYO", "passed the startActivityForResult");
////                        } else {
////                            Log.v("HEYO", "package manager did not resolve");
////                        }
//                    } else {
//                        makeToast("No silly, we want to find " + desiredLetter + "!");
//                    }
//                }
//            }); //// OLD END
//
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
//            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
//            params.setGravity(Gravity.CENTER);
//            params.columnSpec = GridLayout.spec(col);
//            params.rowSpec = GridLayout.spec(row);
//            cell.setLayoutParams(params);
//            Log.v("asdf", String.valueOf(ALPHABET[i]));
//            gridLayout.addView(cell);
//
//            timer.setBase(SystemClock.elapsedRealtime());
//            timer.stop();
//            timer.start();
//            if (state.getString(REMAINING_ALPHABET).length() == 0) {
//                victory();
//            }
//        }
//
//    }

//    private void setBackground(ImageView photo) {
//        int idx = (Integer) photo.getTag();
//        byte[] bytes = state.getByteArray(String.valueOf(ALPHABET[idx]));
//        if (bytes != null) {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            photo.setImageBitmap(bitmap);
////            state.putByteArray(receivedLetter, bytes);
//        }
//    }

    private void victory() {
        makeToast("you won!");
    }

//    private Bundle makeBundle() {
//        if (state == null) {
//            state = new Bundle();
//        }
//        if (!state.getBoolean(MATCHED)) {
//            state.putByteArray("A", A_byteArray);
//            state.putByteArray("B", B_byteArray);
//            state.putByteArray("C", C_byteArray);
//            state.putByteArray("D", D_byteArray);
//            state.putByteArray("E", E_byteArray);
//            state.putByteArray("F", F_byteArray);
//            state.putByteArray("G", G_byteArray);
//            state.putByteArray("H", H_byteArray);
//            state.putByteArray("I", I_byteArray);
//            state.putByteArray("J", J_byteArray);
//            state.putByteArray("K", K_byteArray);
//            state.putByteArray("L", L_byteArray);
//            state.putByteArray("M", M_byteArray);
//            state.putByteArray("N", N_byteArray);
//            state.putByteArray("O", O_byteArray);
//            state.putByteArray("P", P_byteArray);
//            state.putByteArray("Q", Q_byteArray);
//            state.putByteArray("R", R_byteArray);
//            state.putByteArray("S", S_byteArray);
//            state.putByteArray("T", T_byteArray);
//            state.putByteArray("U", U_byteArray);
//            state.putByteArray("V", V_byteArray);
//            state.putByteArray("W", W_byteArray);
//            state.putByteArray("X", X_byteArray);
//            state.putByteArray("Y", Y_byteArray);
//            state.putByteArray("Z", Z_byteArray);
//        }
//        return state;
//    }
//        }.start();
//    }

    private Bundle grabPhoto(Bundle state) {
        if (state == null) {
            state = new Bundle();
        }
        Intent fromPictureAnalyzer = getIntent();
        if (fromPictureAnalyzer != null) {
            Bundle extras = fromPictureAnalyzer.getExtras();
            try {
                byte[] imageByteArray = extras.getByteArray(IMAGE_BYTE_ARRAY);
                receivedLetter = String.valueOf(extras.getChar(SELECTED_LETTER));
                if (imageByteArray != null) {
                    state.putByteArray(String.valueOf(receivedLetter), imageByteArray);
                }
            } catch (Exception e) {
                Log.v("HEYO", "no key imageByteArray was found");
            }
        }
        return state;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("HEYO", "beginning of result");
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("HEYO", "passed the call to super");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            char letter = (char) extras.get("letter");

            currentThumbnail.setImageBitmap(imageBitmap);
            current_bitmap = imageBitmap;
//
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            current_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bout);

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
                if (description != null && String.valueOf(description.charAt(0)).equals(selectedLetter)) {
                    Log.v("HEYO", "we ahve a match.  descrpition: " + description);
                } else {
                    Log.v("HEYO", "no match.  description: " + description);
                }
            }
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
}
