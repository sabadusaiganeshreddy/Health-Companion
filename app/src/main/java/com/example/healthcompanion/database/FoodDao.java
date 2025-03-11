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
    // Search food by name (supports partial and case-insensitive matches)
    @Query("SELECT * FROM food_table WHERE food_name LIKE '%' || :searchQuery || '%' LIMIT 1")
    Food findFood(String searchQuery);

    @Insert // <--- ADDED @Insert ANNOTATION HERE
    void insertAll(Food... foods);

    @Query("SELECT * FROM food_table WHERE food_name LIKE '%' || :searchQuery || '%' LIMIT 1")
    Food findSingleFood(String searchQuery);

    // Get all foods (LiveData for real-time updates)
    @Query("SELECT * FROM food_table")
    LiveData<List<Food>> getAllFoods();

    // Bulk insert for efficiency (replaces existing entries on conflict)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Food> foods);
}
