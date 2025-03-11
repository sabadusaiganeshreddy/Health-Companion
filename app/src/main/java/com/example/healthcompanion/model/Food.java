// Food.java
package com.example.healthcompanion.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

@Entity(tableName = "food_table")
public class Food {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "food_name") // Custom column name
    private final String name;

    @ColumnInfo(name = "calories")
    private final int calories;

    @ColumnInfo(name = "protein")
    private final double protein;

    @ColumnInfo(name = "carbs")
    private final double carbs;

    @ColumnInfo(name = "fats")
    private final double fats;

    public Food(@NonNull String name, int calories, double protein, double carbs, double fats) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }

    @NonNull
    public String getName() { return name; }
    public int getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getCarbs() { return carbs; }
    public double getFats() { return fats; }
}