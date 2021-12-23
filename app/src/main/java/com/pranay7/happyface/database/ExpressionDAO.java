package com.pranay7.happyface.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpressionDAO {
    @Insert
    public void insertNewExpression(Expression expression);

    @Query("SELECT * FROM Expression")
    public LiveData<List<Expression>> getAllExpression();

    @Query("DELETE FROM Expression")
    public void clearData();
}
