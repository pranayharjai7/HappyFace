package com.pranay7.happyface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

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
            }
        };
        binding.smileyImageView.postDelayed(launchTask,3000);

    }

}