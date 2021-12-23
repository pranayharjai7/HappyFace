package com.pranay7.happyface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.pranay7.happyface.database.ExpressionDatabase;
import com.pranay7.happyface.databinding.ActivityHistoryBinding;

public class History extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHistoryBinding binding;
    private ExpressionDatabase expressionDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.historyToolbar);
        binding.navView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.historyToolbar, R.string.navigation_bar_open, R.string.navigation_bar_close);

        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        binding.navView.setCheckedItem(R.id.nav_history);


        expressionDatabase = Room.databaseBuilder(this, ExpressionDatabase.class,"Expression_db")
                .fallbackToDestructiveMigration()
                .build();

        binding.expressionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        expressionDatabase.expressionDAO().getAllExpression().observe(this,
                expressions -> {
                    binding.expressionRecyclerView.setAdapter(new ExpressionViewAdapter(expressions));
                });
    }

    public boolean homeNavigationButtonClicked(@NonNull MenuItem item){
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(this, CameraMenu.class);
        startActivity(intent);
        return true;
    }

    public boolean historyNavigationButtonClicked(@NonNull MenuItem item){
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.clear_history_options_menu, menu);
        return true;
    }

    public void clearHistoryOptionClicked(@NonNull MenuItem item) {
        new Thread(() -> {
            expressionDatabase.expressionDAO().clearData();
        }).start();
        Toast.makeText(this,"The History has been cleared!",Toast.LENGTH_LONG).show();
    }

}