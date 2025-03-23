package com.example.healthcompanion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable {
    private String name;
    private String mainIngredient;
    private int cookingTimeMinutes;
    private String difficulty;
    private int servings;
    private List<String> ingredients;
    private List<String> instructions;
    private String nutritionInfo;
    private String cookingTips;
    private boolean isFavorite;

    public Recipe() {
        ingredients = new ArrayList<>();
        instructions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainIngredient() {
        return mainIngredient;
    }

    public void setMainIngredient(String mainIngredient) {
        this.mainIngredient = mainIngredient;
    }

    public int getCookingTimeMinutes() {
        return cookingTimeMinutes;
    }

        public void setCookingTime(int cookingTimeMinutes) {
        this.cookingTimeMinutes = cookingTimeMinutes;
    }


    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public String getNutritionInfo() {
        return nutritionInfo;
    }

    public void setNutritionInfo(String nutritionInfo) {
        this.nutritionInfo = nutritionInfo;
    }

    public String getCookingTips() {
        return cookingTips;
    }

    public void setCookingTips(String cookingTips) {
        this.cookingTips = cookingTips;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getFormattedTime() {
        int hours = cookingTimeMinutes / 60;
        int minutes = cookingTimeMinutes % 60;

        if (hours > 0) {
            return hours + " hr " + (minutes > 0 ? minutes + " min" : "");
        } else {
            return minutes + " min";
        }
    }
}