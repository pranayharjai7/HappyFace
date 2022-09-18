package com.pranay7.happyface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pranay7.happyface.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = new Intent(this,CameraMenu.class);
        Runnable launchTask = new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        };
        binding.smileyImageView.postDelayed(launchTask,2000);

    }

    public void screenClicked(View view) {
        Intent intent = new Intent(this,CameraMenu.class);
        startActivity(intent);
        finish();
    }
}