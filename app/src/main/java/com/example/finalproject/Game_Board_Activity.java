package com.example.finalproject;

import androidx.activity.result.ActivityResultCaller;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    public static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public static int total_columns = 3;
    public static int total_rows = 9;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int QUERIED_LABEL_DESCRIPTIONS = 3;

    protected LinearLayout backboard;
    protected ScrollView scrollView;
    protected GridLayout gridLayout;
    protected Chronometer timer;
    protected MediaPlayer mediaPlayer;
    protected Bitmap current_bitmap;
    protected ImageView currentThumbnail;
    protected char selectedLetter;
    protected char desiredLetter;
//    protected ArrayList<byte[]> imageViewList;
    protected byte[] A_byteArray;
    protected byte[] B_byteArray;
    protected byte[] C_byteArray;
    protected byte[] D_byteArray;
    protected byte[] E_byteArray;
    protected byte[] F_byteArray;
    protected byte[] G_byteArray;
    protected byte[] H_byteArray;
    protected byte[] I_byteArray;
    protected byte[] J_byteArray;
    protected byte[] K_byteArray;
    protected byte[] L_byteArray;
    protected byte[] M_byteArray;
    protected byte[] N_byteArray;
    protected byte[] O_byteArray;
    protected byte[] P_byteArray;
    protected byte[] Q_byteArray;
    protected byte[] R_byteArray;
    protected byte[] S_byteArray;
    protected byte[] T_byteArray;
    protected byte[] U_byteArray;
    protected byte[] V_byteArray;
    protected byte[] W_byteArray;
    protected byte[] X_byteArray;
    protected byte[] Y_byteArray;
    protected byte[] Z_byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        grabPhoto();
        if (savedInstanceState != null) {
            A_byteArray = savedInstanceState.getByteArray("A");
            B_byteArray = savedInstanceState.getByteArray("B");
            C_byteArray = savedInstanceState.getByteArray("C");
            D_byteArray = savedInstanceState.getByteArray("D");
            E_byteArray = savedInstanceState.getByteArray("E");
            F_byteArray = savedInstanceState.getByteArray("F");
            G_byteArray = savedInstanceState.getByteArray("G");
            H_byteArray = savedInstanceState.getByteArray("H");
            I_byteArray = savedInstanceState.getByteArray("I");
            J_byteArray = savedInstanceState.getByteArray("J");
            K_byteArray = savedInstanceState.getByteArray("K");
            L_byteArray = savedInstanceState.getByteArray("L");
            M_byteArray = savedInstanceState.getByteArray("M");
            N_byteArray = savedInstanceState.getByteArray("N");
            O_byteArray = savedInstanceState.getByteArray("O");
            P_byteArray = savedInstanceState.getByteArray("P");
            Q_byteArray = savedInstanceState.getByteArray("Q");
            R_byteArray = savedInstanceState.getByteArray("R");
            S_byteArray = savedInstanceState.getByteArray("S");
            T_byteArray = savedInstanceState.getByteArray("T");
            U_byteArray = savedInstanceState.getByteArray("U");
            V_byteArray = savedInstanceState.getByteArray("V");
            W_byteArray = savedInstanceState.getByteArray("W");
            X_byteArray = savedInstanceState.getByteArray("X");
            Y_byteArray = savedInstanceState.getByteArray("Y");
            Z_byteArray = savedInstanceState.getByteArray("Z");
        }
        setContentView(R.layout.activity_game_board);

        mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.song);
        playSong();

        backboard = findViewById(R.id.game_backboard);
//        imageViewList = new ArrayList<>(26);
//        imageByteArrays = new byte[26][];

        timer = new Chronometer(this);
        timer.setFormat("Elapsed Time: %s");
        timer.setTextSize(40.0F);
        backboard.addView(timer);
        timer.start();

        Button backButton = new Button(this);
        backButton.setText("Back");
        Context context = this.getApplicationContext();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Intent backIntent = new Intent(context, MainActivity.class);
                startActivity(backIntent);
            }
        });
        backboard.addView(backButton);

        scrollView = new ScrollView(this);

        gridLayout = new GridLayout(this);
        gridLayout.setRowCount(total_rows);
        gridLayout.setColumnCount(total_columns);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        scrollView.addView(gridLayout);
        backboard.addView(scrollView);
        Log.v("asdf", "asdf1");
        makeCells();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray("A", A_byteArray);
        outState.putByteArray("B", B_byteArray);
        outState.putByteArray("C", C_byteArray);
        outState.putByteArray("D", D_byteArray);
        outState.putByteArray("E", E_byteArray);
        outState.putByteArray("F", F_byteArray);
        outState.putByteArray("G", G_byteArray);
        outState.putByteArray("H", H_byteArray);
        outState.putByteArray("I", I_byteArray);
        outState.putByteArray("J", J_byteArray);
        outState.putByteArray("K", K_byteArray);
        outState.putByteArray("L", L_byteArray);
        outState.putByteArray("M", M_byteArray);
        outState.putByteArray("N", N_byteArray);
        outState.putByteArray("O", O_byteArray);
        outState.putByteArray("P", P_byteArray);
        outState.putByteArray("Q", Q_byteArray);
        outState.putByteArray("R", R_byteArray);
        outState.putByteArray("S", S_byteArray);
        outState.putByteArray("T", T_byteArray);
        outState.putByteArray("U", U_byteArray);
        outState.putByteArray("V", V_byteArray);
        outState.putByteArray("W", W_byteArray);
        outState.putByteArray("X", X_byteArray);
        outState.putByteArray("Y", Y_byteArray);
        outState.putByteArray("Z", Z_byteArray);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        imageViewList = savedInstanceState.getParcelableArrayList("imageViewList");
        A_byteArray = savedInstanceState.getByteArray("A");
        B_byteArray = savedInstanceState.getByteArray("B");
        C_byteArray = savedInstanceState.getByteArray("C");
        D_byteArray = savedInstanceState.getByteArray("D");
        E_byteArray = savedInstanceState.getByteArray("E");
        F_byteArray = savedInstanceState.getByteArray("F");
        G_byteArray = savedInstanceState.getByteArray("G");
        H_byteArray = savedInstanceState.getByteArray("H");
        I_byteArray = savedInstanceState.getByteArray("I");
        J_byteArray = savedInstanceState.getByteArray("J");
        K_byteArray = savedInstanceState.getByteArray("K");
        L_byteArray = savedInstanceState.getByteArray("L");
        M_byteArray = savedInstanceState.getByteArray("M");
        N_byteArray = savedInstanceState.getByteArray("N");
        O_byteArray = savedInstanceState.getByteArray("O");
        P_byteArray = savedInstanceState.getByteArray("P");
        Q_byteArray = savedInstanceState.getByteArray("Q");
        R_byteArray = savedInstanceState.getByteArray("R");
        S_byteArray = savedInstanceState.getByteArray("S");
        T_byteArray = savedInstanceState.getByteArray("T");
        U_byteArray = savedInstanceState.getByteArray("U");
        V_byteArray = savedInstanceState.getByteArray("V");
        W_byteArray = savedInstanceState.getByteArray("W");
        X_byteArray = savedInstanceState.getByteArray("X");
        Y_byteArray = savedInstanceState.getByteArray("Y");
        Z_byteArray = savedInstanceState.getByteArray("Z");
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
        int desiredLetterInt = (int) Math.floor(Math.random() * alphabet.length);
        desiredLetter = alphabet[desiredLetterInt];

        for (int i = 0, col = 0, row = 0; i<alphabet.length; i++, col++) {
            if (col == total_columns) {
                col = 0;
                row++;
            }
            View cell = getLayoutInflater().inflate(R.layout.cell, null);
//            cell.setTag(new Integer(tag));
            TextView letter = (TextView) cell.findViewById(R.id.letter);
            letter.setText(String.valueOf(alphabet[i]));
            if ((total_columns * row) + col == desiredLetterInt) { // it's our guy
                flashLetter(letter);
            }

            ImageView photo = (ImageView) cell.findViewById(R.id.photo);
            int tag = (total_columns * row) + col;
            photo.setTag(new Integer(tag));

//            photo.setOnClickListener(new Snapper(photo, photo.getContext()));
            /// OLD START
            Context gameActivityContext = this.getApplicationContext();
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedLetter = alphabet[(Integer) photo.getTag()];
                    if (selectedLetter == desiredLetter) {
                        currentThumbnail = photo;
                        // start a whole new intent?
                        Intent photoAnaylzerIntent = new Intent(gameActivityContext, PictureAnalyzerActivity.class);
                        photoAnaylzerIntent.putExtra("selectedLetter", selectedLetter);
                        startActivity(photoAnaylzerIntent);


//                        Intent snapPic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//                        if (snapPic.resolveActivity(getPackageManager()) != null) {
//                            try {
//                                Log.v("HEYO", "attempting the startActivityForResult");
//                                startActivityForResult(snapPic, REQUEST_IMAGE_CAPTURE);
//                            } catch (ActivityNotFoundException e) {
//                                Log.v("HEYO -- ERROR", "failed starting camera activity intent: " + e.getMessage());
//                            }
//                            Log.v("HEYO", "passed the startActivityForResult");
//                        } else {
//                            Log.v("HEYO", "package manager did not resolve");
//                        }
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
            Log.v("asdf", String.valueOf(alphabet[i]));
            gridLayout.addView(cell);
        }

    }
//        }.start();
//    }

    private void grabPhoto() {
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
                if (description != null && Character.toUpperCase(description.charAt(0)) == selectedLetter) {
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
