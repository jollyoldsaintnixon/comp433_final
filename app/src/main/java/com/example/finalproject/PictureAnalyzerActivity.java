package com.example.finalproject;

import static com.example.finalproject.Game_Board_Activity.REQUEST_IMAGE_CAPTURE;
import static com.example.finalproject.Game_Board_Activity.QUERIED_LABEL_DESCRIPTIONS;

import androidx.activity.result.ActivityResultCaller;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class PictureAnalyzerActivity extends AppCompatActivity implements ActivityResultCaller {


    ImageView photo;
    char selectedLetter;
    LinearLayout backboard;
    TextView matchText;
    boolean matched = false;
    byte[] imageByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_analyzer);

        Bundle intentExtras = getIntent().getExtras();
        backboard = findViewById(R.id.photo_anaylyzer_backboard);
        selectedLetter = intentExtras.getChar("selectedLetter");
        matchText = findViewById(R.id.match_text);
        photo = findViewById(R.id.big_photo);

        snapPic();

//        photo = savedInstanceState.getBundle("photo").get;
    }

    private void snapPic() {
        Intent snapPicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(snapPicIntent, REQUEST_IMAGE_CAPTURE);
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

            photo.setImageBitmap(imageBitmap);
//
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bout);

            imageByteArray = bout.toByteArray();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        myVisionTester(imageBitmap);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            matchText.setText("we have a match. Description: " + description);
                            matched = true;
                            makeToast("good job!");
                        }
                    });
                } else {
                    Log.v("HEYO", "no match.  description: " + description);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            matchText.setText("no match.  description: " + description);
                            makeToast("better luck next time!");
                        }
                    });
                }
            }
        }
    }

    private void makeToast(CharSequence text) {
        Snackbar snackbar = Snackbar.make(backboard, text, BaseTransientBottomBar.LENGTH_INDEFINITE);
        Intent backToGameActivity = new Intent(this, Game_Board_Activity.class);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                if (matched) {
                    backToGameActivity.putExtra("imageByteArray", imageByteArray);
                }
                startActivity(backToGameActivity);
            }
        });
        snackbar.show();
    }

}
