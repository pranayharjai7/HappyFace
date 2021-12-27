package com.pranay7.happyface;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.pranay7.happyface.databinding.ActivityCameraMenuBinding;

public class CameraMenu extends AppCompatActivity implements SensorEventListener, NavigationView.OnNavigationItemSelectedListener {

    public static final int CAMERA_PERMISSION_CODE = 1;
    public static final String THUMBNAIL_FROM_CAMERA = "thumbnail";
    private static final float SHAKE_THRESHOLD = 6.25f; // m/s^2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECONDS = 1000;
    private static long mLastShakeTime;

    private ActivityCameraMenuBinding binding;
    private Bitmap thumbnail;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.cameraMenuToolbar);
        binding.navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.cameraMenuToolbar, R.string.navigation_bar_open, R.string.navigation_bar_close);

        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if(savedInstanceState == null){
            binding.navView.setCheckedItem(R.id.nav_home);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECONDS) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                Log.d("Accelerometer", "Acc: " + acceleration + " m/s^2");
                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;
                    Log.d("Accelerometer", "SHAKING BAKING!");
                    openCamera();
                }
            }
        }
    }

    ActivityResultLauncher cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    thumbnail = (Bitmap) result.getData().getExtras().get("data");
                    Intent intent = new Intent(this, PictureViewAndProcess.class);
                    intent.putExtra(THUMBNAIL_FROM_CAMERA, thumbnail);
                    startActivity(intent);
                    finish();
                }
            }
    );

    public void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraActivityResultLauncher.launch(intent);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    public boolean homeNavigationButtonClicked(@NonNull MenuItem item){
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean historyNavigationButtonClicked(@NonNull MenuItem item){
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        sensorManager.unregisterListener(this);
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if ((grantResults.length != 0) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraActivityResultLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Oops!! You didn't allow permission for the camera!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            };

            new Handler(Looper.getMainLooper()).postDelayed(runnable,2000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}