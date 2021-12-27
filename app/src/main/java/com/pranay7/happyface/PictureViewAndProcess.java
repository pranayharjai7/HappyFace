package com.pranay7.happyface;

import static com.pranay7.happyface.CameraMenu.THUMBNAIL_FROM_CAMERA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.pranay7.happyface.databinding.ActivityPictureViewAndProcessBinding;

import java.util.List;

public class PictureViewAndProcess extends AppCompatActivity {


    public static String MANUAL_TESTING_LOG = "LogData";
    public static final String HEAD_EULER_ANGLE_X = "HeadEulerAngleX";
    public static final String HEAD_EULER_ANGLE_Y = "HeadEulerAngleY";
    public static final String HEAD_EULER_ANGLE_Z = "HeadEulerAngleZ";
    public static final String SMILE_PROB = "SmileProb";
    private ActivityPictureViewAndProcessBinding binding;
    private FaceDetector detector;
    private Handler handler = new Handler();
    private Bitmap thumbnail;
    private float smileProb;
    private float HeadEulerAngleX;
    private float HeadEulerAngleY;
    private float HeadEulerAngleZ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPictureViewAndProcessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        thumbnail = (Bitmap) intent.getExtras().get(THUMBNAIL_FROM_CAMERA);
        binding.myImage.setImageBitmap(thumbnail);
        
        processImage(imageFromBitmap(thumbnail));
    }

    private void processImage(InputImage image) {
        new Thread(() -> {
            detectFaces(image);

            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int finalI = i;
                handler.post(() -> {
                    binding.progressBar.setProgress(finalI);
                    binding.progressTextView.setText(finalI+"%");
                });
            }

            handler.post(() -> {
                binding.progressTextView.setText("Processing Completed!");
            });

            nextActivityToReadExpressions();
        }).start();
    }

    private void nextActivityToReadExpressions() {
        Intent intent = new Intent(this, ExpressionReading.class);
        intent.putExtra(HEAD_EULER_ANGLE_X,HeadEulerAngleX);
        intent.putExtra(HEAD_EULER_ANGLE_Y,HeadEulerAngleY);
        intent.putExtra(HEAD_EULER_ANGLE_Z,HeadEulerAngleZ);
        intent.putExtra(SMILE_PROB,smileProb);
        startActivity(intent);
        finish();
    }

    private InputImage imageFromBitmap(Bitmap bitmap) {
        int rotationDegree = 0;
        InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);
        return image;
    }

    private void detectFaces(InputImage image) {
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setMinFaceSize(0.15f)
                        .enableTracking()
                        .build();
        Log.d(MANUAL_TESTING_LOG,"Face Detector Options: "+ options);

        // Real-time contour detection
        FaceDetectorOptions realTimeOpts =
                new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .build();
        Log.d(MANUAL_TESTING_LOG,"Realtime Options: "+ realTimeOpts);

        detector = FaceDetection.getClient(options);
        Task<List<Face>> result = detectInImage(image);
    }

    private Task<List<Face>> detectInImage(InputImage image) {
        Task<List<Face>> result = detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(@NonNull List<Face> faces) {
                HeadEulerAngleX = 0;
                HeadEulerAngleY = 0;
                HeadEulerAngleZ = 0;
                smileProb = -1;
                for (Face face : faces) {
                    HeadEulerAngleX = face.getHeadEulerAngleX();
                    HeadEulerAngleY = face.getHeadEulerAngleY();
                    HeadEulerAngleZ = face.getHeadEulerAngleZ();
                    smileProb = face.getSmilingProbability();
                    Log.d(MANUAL_TESTING_LOG,"HeadEulerAngleX = " + face.getHeadEulerAngleX());
                    Log.d(MANUAL_TESTING_LOG,"HeadEulerAngleY = " + face.getHeadEulerAngleY());
                    Log.d(MANUAL_TESTING_LOG,"HeadEulerAngleZ = " + face.getHeadEulerAngleZ());
                    Log.d(MANUAL_TESTING_LOG,"smileProb =" + face.getSmilingProbability());
                    //logExtrasForTesting(face);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "Face detection failed " + e);
            }
        });
        return result;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,CameraMenu.class);
        startActivity(intent);
        finish();
    }
}