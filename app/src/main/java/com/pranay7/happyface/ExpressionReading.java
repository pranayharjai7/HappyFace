package com.pranay7.happyface;

import static com.pranay7.happyface.PictureViewAndProcess.HEAD_EULER_ANGLE_X;
import static com.pranay7.happyface.PictureViewAndProcess.HEAD_EULER_ANGLE_Y;
import static com.pranay7.happyface.PictureViewAndProcess.HEAD_EULER_ANGLE_Z;
import static com.pranay7.happyface.PictureViewAndProcess.MANUAL_TESTING_LOG;
import static com.pranay7.happyface.PictureViewAndProcess.SMILE_PROB;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.pranay7.happyface.databinding.ActivityExpressionReadingBinding;

public class ExpressionReading extends AppCompatActivity {

    private float smileProb;
    private float headEulerAngleX;
    private float headEulerAngleY;
    private float headEulerAngleZ;
    private String expression;
    private ActivityExpressionReadingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpressionReadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        headEulerAngleX = (float) intent.getExtras().get(HEAD_EULER_ANGLE_X);
        headEulerAngleY = (float) intent.getExtras().get(HEAD_EULER_ANGLE_Y);
        headEulerAngleZ = (float) intent.getExtras().get(HEAD_EULER_ANGLE_Z);
        smileProb = (float) intent.getExtras().get(SMILE_PROB);

        Log.d(MANUAL_TESTING_LOG,"HeadEulerAngleX = " + headEulerAngleX);
        Log.d(MANUAL_TESTING_LOG,"HeadEulerAngleY = " + headEulerAngleY);
        Log.d(MANUAL_TESTING_LOG,"HeadEulerAngleZ = " + headEulerAngleZ);
        Log.d(MANUAL_TESTING_LOG,"smileProb =" + smileProb);
        if(smileProb != -1){
            if((headEulerAngleX < 30) && (headEulerAngleX> -30) && (headEulerAngleY < 30) && (headEulerAngleY> -30)){
                if(smileProb<0.01){
                    binding.expressionTextView.append("SAD");
                    expression = "SAD";
                }
                else if(smileProb<0.3){
                    binding.expressionTextView.append("NEUTRAL");
                    expression = "NEUTRAL";
                }
                else{
                    binding.expressionTextView.append("HAPPY");
                    expression = "HAPPY";
                }
                onExpressionReading(expression);
            }
            else{
                binding.expressionTextView.setText("Nice click!\n But can't see you clearly.. :(\n Try looking at the camera and do-over?");
            }
        }
        else{
            binding.expressionTextView.setText("Nice picture!\n But you are missing :(\n Wanna try again?");
        }
    }

    private void onExpressionReading(String expression) {
        
    }
}