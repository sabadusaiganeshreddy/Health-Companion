package com.example.healthcompanion.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.healthcompanion.model.Food;

import java.util.List;

@Dao
public interface FoodDao {
    // Search food by name (supports partial matches)
    @Query("SELECT * FROM food_table WHERE food_name LIKE '%' || :searchQuery || '%' LIMIT 1")
    Food findFood(String searchQuery);


    // Get all foods (LiveData for real-time updates)
    @Query("SELECT * FROM food_table")
    LiveData<List<Food>> getAllFoods();

    // Insert multiple foods (replaces existing ones to avoid conflicts)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Food... foods);
}
