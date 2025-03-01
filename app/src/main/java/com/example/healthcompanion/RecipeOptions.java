package com.example.healthcompanion;

import java.io.Serializable;

public class RecipeOptions implements Serializable {
    private String cuisineType;
    private String dietaryPreferences;
    private String difficulty;
    private int maxCookingTime;
    private String excludedIngredients;

    public RecipeOptions() {
        // Default constructor
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getDietaryPreferences() {
        return dietaryPreferences;
    }

    public void setDietaryPreferences(String dietaryPreferences) {
        this.dietaryPreferences = dietaryPreferences;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getMaxCookingTime() {
        return maxCookingTime;
    }

    public void setMaxCookingTime(int maxCookingTime) {
        this.maxCookingTime = maxCookingTime;
    }

    public String getExcludedIngredients() {
        return excludedIngredients;
    }

    public void setExcludedIngredients(String excludedIngredients) {
        this.excludedIngredients = excludedIngredients;
    }
}