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
    // Search food by name (case-insensitive search)
    @Query("SELECT * FROM food_table WHERE LOWER(food_name) LIKE '%' || LOWER(:searchQuery) || '%' LIMIT 1")
    Food findFood(String searchQuery);

    // Get all foods (LiveData for real-time updates)
    @Query("SELECT * FROM food_table")
    LiveData<List<Food>> getAllFoods();

    // Bulk insert with REPLACE strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Food> foods);
}
