package com.pranay7.happyface;

import static com.pranay7.happyface.PictureViewAndProcess.HEAD_EULER_ANGLE_X;
import static com.pranay7.happyface.PictureViewAndProcess.HEAD_EULER_ANGLE_Y;
import static com.pranay7.happyface.PictureViewAndProcess.HEAD_EULER_ANGLE_Z;
import static com.pranay7.happyface.PictureViewAndProcess.MANUAL_TESTING_LOG;
import static com.pranay7.happyface.PictureViewAndProcess.SMILE_PROB;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.pranay7.happyface.database.Expression;
import com.pranay7.happyface.database.ExpressionDatabase;
import com.pranay7.happyface.databinding.ActivityExpressionReadingBinding;

import java.time.LocalDateTime;

public class ExpressionReading extends AppCompatActivity {

    private float smileProb;
    private float headEulerAngleX;
    private float headEulerAngleY;
    private float headEulerAngleZ;
    private String expression;
    private ActivityExpressionReadingBinding binding;
    private ExpressionDatabase expressionDatabase;
    @RequiresApi(api = Build.VERSION_CODES.O)
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
                    binding.expressionTextView.append("SAD 😢");
                    expression = "SAD";
                }
                else if(smileProb<0.3){
                    binding.expressionTextView.append("NEUTRAL 😐");
                    expression = "NEUTRAL";
                }
                else{
                    binding.expressionTextView.append("HAPPY 😄\n\nHi Happy face!");
                    expression = "HAPPY";
                }
                saveExpression(expression);
                onExpressionReading(expression);
            }
            else{
                binding.expressionTextView.setText("Nice click!\n But can't see you clearly.. 😓\n Try looking at the camera and do-over?");
            }
        }
        else{
            binding.expressionTextView.setText("Nice picture!\n But you are missing 😓\n Wanna try again?");
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveExpression(String expression) {
        expressionDatabase = Room.databaseBuilder(this,ExpressionDatabase.class,"Expression_db")
                .fallbackToDestructiveMigration()
                .build();

        new Thread(() -> {
            Expression expression1 = new Expression();
            expression1.setExpression(expression);
            expression1.setDateTime(LocalDateTime.now().toString());

            expressionDatabase.expressionDAO().insertNewExpression(expression1);
        }).start();
    }

    private void onExpressionReading(String expression) {
        
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,CameraMenu.class);
        startActivity(intent);
        finish();
    }
}