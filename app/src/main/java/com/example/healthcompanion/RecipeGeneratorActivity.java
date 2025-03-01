package com.example.healthcompanion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.Markwon;

public class RecipeGeneratorActivity extends AppCompatActivity {

    private EditText foodSearchInput;
    private Button getRecipeButton;
    private Button toggleOptionsButton;
    private LinearLayout optionsLayout;
    private Spinner cuisineSpinner;
    private Spinner dietaryPreferencesSpinner;
    private Spinner difficultySpinner;
    private EditText maxCookingTimeInput;
    private EditText excludedIngredientsInput;
    private ProgressBar loadingProgressBar;
    private CardView recipeCardView;
    private TextView recipeNameTextView;
    private TextView recipeDetailsTextView;
    private TextView ingredientsHeaderTextView;
    private TextView ingredientsTextView;
    private TextView instructionsHeaderTextView;
    private TextView instructionsTextView;
    private TextView nutritionHeaderTextView;
    private TextView nutritionTextView;
    private TextView tipsHeaderTextView;
    private TextView tipsTextView;
    private Button saveButton;
    private Button shareButton;
    private ChipGroup recentSearchesChipGroup;

    private List<String> recentSearches = new ArrayList<>();
    private static final int MAX_RECENT_SEARCHES = 5;

    private Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_generator);

        initializeViews();
        setupSpinners();
        loadRecentSearches();
        setupRecentSearchesChips();
        setupClickListeners();
    }

    private void initializeViews() {
        foodSearchInput = findViewById(R.id.foodSearchInput);
        getRecipeButton = findViewById(R.id.getRecipeButton);
        toggleOptionsButton = findViewById(R.id.toggleOptionsButton);
        optionsLayout = findViewById(R.id.optionsLayout);
        cuisineSpinner = findViewById(R.id.cuisineSpinner);
        dietaryPreferencesSpinner = findViewById(R.id.dietaryPreferencesSpinner);
        difficultySpinner = findViewById(R.id.difficultySpinner);
        maxCookingTimeInput = findViewById(R.id.maxCookingTimeInput);
        excludedIngredientsInput = findViewById(R.id.excludedIngredientsInput);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        recipeCardView = findViewById(R.id.recipeCardView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        recipeDetailsTextView = findViewById(R.id.recipeDetailsTextView);
        ingredientsHeaderTextView = findViewById(R.id.ingredientsHeaderTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        instructionsHeaderTextView = findViewById(R.id.instructionsHeaderTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);
        nutritionHeaderTextView = findViewById(R.id.nutritionHeaderTextView);
        nutritionTextView = findViewById(R.id.nutritionTextView);
        tipsHeaderTextView = findViewById(R.id.tipsHeaderTextView);
        tipsTextView = findViewById(R.id.tipsTextView);
        saveButton = findViewById(R.id.saveButton);
        shareButton = findViewById(R.id.shareButton);
        recentSearchesChipGroup = findViewById(R.id.recentSearchesChipGroup);
    }

    private void setupSpinners() {
        // Cuisine types
        ArrayAdapter<CharSequence> cuisineAdapter = ArrayAdapter.createFromResource(this,
                R.array.cuisine_types, android.R.layout.simple_spinner_item);
        cuisineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuisineSpinner.setAdapter(cuisineAdapter);

        // Dietary preferences
        ArrayAdapter<CharSequence> dietaryAdapter = ArrayAdapter.createFromResource(this,
                R.array.dietary_preferences, android.R.layout.simple_spinner_item);
        dietaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dietaryPreferencesSpinner.setAdapter(dietaryAdapter);

        // Difficulty levels
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);
    }

    private void setupClickListeners() {
        toggleOptionsButton.setOnClickListener(v -> {
            if (optionsLayout.getVisibility() == View.VISIBLE) {
                optionsLayout.setVisibility(View.GONE);
                toggleOptionsButton.setText("Show Options");
            } else {
                optionsLayout.setVisibility(View.VISIBLE);
                toggleOptionsButton.setText("Hide Options");
            }
        });
// Add this at the beginning of getRecipeButton.setOnClickListener
        getRecipeButton.setOnClickListener(v -> {
            String foodItem = foodSearchInput.getText().toString().trim();

            if (foodItem.isEmpty()) {
                Toast.makeText(RecipeGeneratorActivity.this, "Please enter a food item", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add to recent searches
            addToRecentSearches(foodItem);

            // Show loading indicator
            loadingProgressBar.setVisibility(View.VISIBLE);
            recipeCardView.setVisibility(View.GONE);

            // Get recipe options
            RecipeOptions options = getRecipeOptions();

            // Disable the button while loading
            getRecipeButton.setEnabled(false);
            getRecipeButton.setText("Generating...");

            GeminiService.generateRecipe(foodItem, options, new GeminiService.RecipeCallback() {
                @Override
                public void onSuccess(Recipe recipe) {
                    runOnUiThread(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        getRecipeButton.setEnabled(true);
                        getRecipeButton.setText("Generate Recipe");
                        displayRecipe(recipe);
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        getRecipeButton.setEnabled(true);
                        getRecipeButton.setText("Generate Recipe");
                        Toast.makeText(RecipeGeneratorActivity.this,
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
        getRecipeButton.setOnClickListener(v -> {
            String foodItem = foodSearchInput.getText().toString().trim();

            if (foodItem.isEmpty()) {
                Toast.makeText(RecipeGeneratorActivity.this, "Please enter a food item", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add to recent searches
            addToRecentSearches(foodItem);

            // Show loading indicator
            loadingProgressBar.setVisibility(View.VISIBLE);
            recipeCardView.setVisibility(View.GONE);

            // Get recipe options
            RecipeOptions options = getRecipeOptions();

            GeminiService.generateRecipe(foodItem, options, new GeminiService.RecipeCallback() {
                @Override
                public void onSuccess(Recipe recipe) {
                    runOnUiThread(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        displayRecipe(recipe);
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(RecipeGeneratorActivity.this,
                                "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        saveButton.setOnClickListener(v -> {
            if (currentRecipe != null) {
                saveRecipe(currentRecipe);
                Toast.makeText(this, "Recipe saved to favorites!", Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(v -> {
            if (currentRecipe != null) {
                shareRecipe(currentRecipe);
            }
        });
    }

    private RecipeOptions getRecipeOptions() {
        RecipeOptions options = new RecipeOptions();

        // Get selected cuisine if not "Any"
        String selectedCuisine = cuisineSpinner.getSelectedItem().toString();
        if (!selectedCuisine.equals("Any")) {
            options.setCuisineType(selectedCuisine);
        }

        // Get dietary preferences if not "None"
        String selectedDietary = dietaryPreferencesSpinner.getSelectedItem().toString();
        if (!selectedDietary.equals("None")) {
            options.setDietaryPreferences(selectedDietary);
        }

        // Get difficulty if not "Any"
        String selectedDifficulty = difficultySpinner.getSelectedItem().toString();
        if (!selectedDifficulty.equals("Any")) {
            options.setDifficulty(selectedDifficulty);
        }

        // Get max cooking time if provided
        String maxTimeStr = maxCookingTimeInput.getText().toString().trim();
        if (!maxTimeStr.isEmpty()) {
            try {
                int maxTime = Integer.parseInt(maxTimeStr);
                options.setMaxCookingTime(maxTime);
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }

        // Get excluded ingredients if provided
        String excludedIngredients = excludedIngredientsInput.getText().toString().trim();
        if (!excludedIngredients.isEmpty()) {
            options.setExcludedIngredients(excludedIngredients);
        }

        return options;
    }

    private void displayRecipe(Recipe recipe) {
        currentRecipe = recipe;
        recipeCardView.setVisibility(View.VISIBLE);
        Markwon markwon = Markwon.create(this);
        markwon.setMarkdown(recipeNameTextView, recipe.getName());

        //recipeNameTextView.setText();

        String details = String.format("🕒 %s  •  🍴 %d servings  •  📊 %s",
                recipe.getFormattedTime(),
                recipe.getServings(),
                recipe.getDifficulty());
        markwon.setMarkdown(recipeDetailsTextView,details);

        //recipeDetailsTextView.setText(details);

        // Ingredients
        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            ingredientsHeaderTextView.setVisibility(View.VISIBLE);
            ingredientsTextView.setVisibility(View.VISIBLE);

            StringBuilder ingredientsBuilder = new StringBuilder();
            for (String ingredient : recipe.getIngredients()) {
                ingredientsBuilder.append("• ").append(ingredient).append("\n");
            }
            markwon.setMarkdown(instructionsTextView, ingredientsBuilder.toString());

            //ingredientsTextView.setText(ingredientsBuilder.toString());
        } else {
            ingredientsHeaderTextView.setVisibility(View.GONE);
            ingredientsTextView.setVisibility(View.GONE);
        }

        // Instructions
        if (recipe.getInstructions() != null && !recipe.getInstructions().isEmpty()) {
            instructionsHeaderTextView.setVisibility(View.VISIBLE);
            instructionsTextView.setVisibility(View.VISIBLE);

            StringBuilder instructionsBuilder = new StringBuilder();
            int stepNumber = 1;
            for (String instruction : recipe.getInstructions()) {
                instructionsBuilder.append(stepNumber++).append(". ").append(instruction).append("\n\n");
            }
            markwon.setMarkdown(instructionsTextView, instructionsBuilder.toString());

            //instructionsTextView.setText(instructionsBuilder.toString());
        } else {
            instructionsHeaderTextView.setVisibility(View.GONE);
            instructionsTextView.setVisibility(View.GONE);
        }

        // Nutrition info
        if (recipe.getNutritionInfo() != null && !recipe.getNutritionInfo().isEmpty()) {
            nutritionHeaderTextView.setVisibility(View.VISIBLE);
            nutritionTextView.setVisibility(View.VISIBLE);
            markwon.setMarkdown(nutritionTextView, recipe.getNutritionInfo());

            //nutritionTextView.setText(recipe.getNutritionInfo());
        } else {
            nutritionHeaderTextView.setVisibility(View.GONE);
            nutritionTextView.setVisibility(View.GONE);
        }

        // Cooking tips
        if (recipe.getCookingTips() != null && !recipe.getCookingTips().isEmpty()) {
            tipsHeaderTextView.setVisibility(View.VISIBLE);
            tipsTextView.setVisibility(View.VISIBLE);
            markwon.setMarkdown(tipsTextView,recipe.getCookingTips());

            //tipsTextView.setText(recipe.getCookingTips());
        } else {
            tipsHeaderTextView.setVisibility(View.GONE);
            tipsTextView.setVisibility(View.GONE);
        }
    }

    private void loadRecentSearches() {
        SharedPreferences prefs = getSharedPreferences("recipe_prefs", MODE_PRIVATE);
        String recentSearchesJson = prefs.getString("recent_searches", null);

        if (recentSearchesJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            recentSearches = gson.fromJson(recentSearchesJson, type);
        }
    }

    private void saveRecentSearches() {
        SharedPreferences prefs = getSharedPreferences("recipe_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recentSearches);
        editor.putString("recent_searches", json);
        editor.apply();
    }

    private void addToRecentSearches(String search) {
        // Remove if already exists (to move it to front)
        recentSearches.remove(search);

        // Add to front
        recentSearches.add(0, search);

        // Trim list if too long
        if (recentSearches.size() > MAX_RECENT_SEARCHES) {
            recentSearches = recentSearches.subList(0, MAX_RECENT_SEARCHES);
        }

        // Save and update UI
        saveRecentSearches();
        setupRecentSearchesChips();
    }

    private void setupRecentSearchesChips() {
        recentSearchesChipGroup.removeAllViews();

        for (String search : recentSearches) {
            Chip chip = new Chip(this);
            chip.setText(search);
            chip.setClickable(true);
            chip.setCheckable(false);

            chip.setOnClickListener(v -> {
                foodSearchInput.setText(search);
            });

            recentSearchesChipGroup.addView(chip);
        }
    }

    private void saveRecipe(Recipe recipe) {
        SharedPreferences prefs = getSharedPreferences("recipe_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Get existing saved recipes
        Gson gson = new Gson();
        String savedRecipesJson = prefs.getString("saved_recipes", null);
        List<Recipe> savedRecipes = new ArrayList<>();

        if (savedRecipesJson != null) {
            Type type = new TypeToken<ArrayList<Recipe>>() {}.getType();
            savedRecipes = gson.fromJson(savedRecipesJson, type);
        }

        // Check if recipe already exists
        boolean recipeExists = false;
        for (int i = 0; i < savedRecipes.size(); i++) {
            Recipe existingRecipe = savedRecipes.get(i);
            if (existingRecipe.getName().equals(recipe.getName())) {
                // Replace existing recipe
                savedRecipes.set(i, recipe);
                recipeExists = true;
                break;
            }
        }

        // Add new recipe if it doesn't exist
        if (!recipeExists) {
            recipe.setFavorite(true);
            savedRecipes.add(recipe);
        }

        // Save updated list
        String json = gson.toJson(savedRecipes);
        editor.putString("saved_recipes", json);
        editor.apply();
    }

    private void shareRecipe(Recipe recipe) {
        StringBuilder shareText = new StringBuilder();

        // Add recipe name and details
        shareText.append(recipe.getName()).append("\n\n");
        shareText.append("Cook Time: ").append(recipe.getFormattedTime()).append("\n");
        shareText.append("Servings: ").append(recipe.getServings()).append("\n");
        shareText.append("Difficulty: ").append(recipe.getDifficulty()).append("\n\n");

        // Add ingredients
        shareText.append("INGREDIENTS:\n");
        for (String ingredient : recipe.getIngredients()) {
            shareText.append("• ").append(ingredient).append("\n");
        }
        shareText.append("\n");

        // Add instructions
        shareText.append("INSTRUCTIONS:\n");
        int stepNumber = 1;
        for (String instruction : recipe.getInstructions()) {
            shareText.append(stepNumber++).append(". ").append(instruction).append("\n");
        }

        // Add nutrition info if available
        if (recipe.getNutritionInfo() != null && !recipe.getNutritionInfo().isEmpty()) {
            shareText.append("\nNUTRITION INFO:\n").append(recipe.getNutritionInfo()).append("\n");
        }

        // Add tips if available
        if (recipe.getCookingTips() != null && !recipe.getCookingTips().isEmpty()) {
            shareText.append("\nCOOKING TIPS:\n").append(recipe.getCookingTips()).append("\n");
        }

        shareText.append("\nGenerated with Health Companion App");

        // Create share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Recipe: " + recipe.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());

        // Start the sharing activity
        startActivity(Intent.createChooser(shareIntent, "Share Recipe"));
    }
}