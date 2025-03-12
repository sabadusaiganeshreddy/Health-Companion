package com.example.healthcompanion;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcompanion.database.FoodDao;
import com.example.healthcompanion.database.FoodDatabase;
import com.example.healthcompanion.model.Food;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodLibrary extends AppCompatActivity {
    private EditText etSearch;
    private FoodDao foodDao;
    private TextView tvResultError, tvResultLabel;

    // Table Layouts
    private TableLayout tableMacronutrients, tableMinerals, tableVitamins;

    // Macronutrients
    private TextView tvCaloriesValue, tvProteinValue, tvCarbsValue, tvFatsValue, tvSugarValue, tvFibreValue, tvCholesterolValue;

    // Minerals
    private TextView tvCalciumValue, tvPhosphorusValue, tvMagnesiumValue, tvSodiumValue, tvPotassiumValue, tvIronValue, tvCopperValue,
            tvSeleniumValue, tvChromiumValue, tvManganeseValue, tvMolybdenumValue, tvZincValue;

    // Vitamins
    private TextView tvVitAValue, tvVitEValue, tvVitD2Value, tvVitD3Value, tvVitK1Value, tvVitK2Value, tvFolateValue,
            tvVitB1Value, tvVitB2Value, tvVitB3Value, tvVitB5Value, tvVitB6Value, tvVitB7Value;

    private FoodDatabase db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_library);

        // Initialize UI elements
        etSearch = findViewById(R.id.et_search);
        tvResultError = findViewById(R.id.tv_result_error);
        tvResultLabel = findViewById(R.id.tv_result_label);

        // Table Layouts
        tableMacronutrients = findViewById(R.id.table_macronutrients);
        tableMinerals = findViewById(R.id.table_minerals);
        tableVitamins = findViewById(R.id.table_vitamins);

        // Macronutrients
        tvCaloriesValue = findViewById(R.id.tv_calories_value);
        tvProteinValue = findViewById(R.id.tv_protein_value);
        tvCarbsValue = findViewById(R.id.tv_carbs_value);
        tvFatsValue = findViewById(R.id.tv_fats_value);
        tvSugarValue = findViewById(R.id.tv_sugar_value);
        tvFibreValue = findViewById(R.id.tv_fibre_value);
        tvCholesterolValue = findViewById(R.id.tv_cholesterol_value);

        // Minerals
        tvCalciumValue = findViewById(R.id.tv_calcium_value);
        tvPhosphorusValue = findViewById(R.id.tv_phosphorus_value);
        tvMagnesiumValue = findViewById(R.id.tv_magnesium_value);
        tvSodiumValue = findViewById(R.id.tv_sodium_value);
        tvPotassiumValue = findViewById(R.id.tv_potassium_value);
        tvIronValue = findViewById(R.id.tv_iron_value);
        tvCopperValue = findViewById(R.id.tv_copper_value);
        tvSeleniumValue = findViewById(R.id.tv_selenium_value);
        tvChromiumValue = findViewById(R.id.tv_chromium_value);
        tvManganeseValue = findViewById(R.id.tv_manganese_value);
        tvMolybdenumValue = findViewById(R.id.tv_molybdenum_value);
        tvZincValue = findViewById(R.id.tv_zinc_value);

        // Vitamins
        tvVitAValue = findViewById(R.id.tv_vitA_value);
        tvVitEValue = findViewById(R.id.tv_vitE_value);
        tvVitD2Value = findViewById(R.id.tv_vitD2_value);
        tvVitD3Value = findViewById(R.id.tv_vitD3_value);
        tvVitK1Value = findViewById(R.id.tv_vitK1_value);
        tvVitK2Value = findViewById(R.id.tv_vitK2_value);
        tvFolateValue = findViewById(R.id.tv_folate_value);
        tvVitB1Value = findViewById(R.id.tv_vitB1_value);
        tvVitB2Value = findViewById(R.id.tv_vitB2_value);
        tvVitB3Value = findViewById(R.id.tv_vitB3_value);
        tvVitB5Value = findViewById(R.id.tv_vitB5_value);
        tvVitB6Value = findViewById(R.id.tv_vitB6_value);
        tvVitB7Value = findViewById(R.id.tv_vitB7_value);

        Button btnSearch = findViewById(R.id.btn_search);

        // Initialize database
        db = FoodDatabase.getDatabase(this);
        foodDao = db.foodDao();
        executorService = Executors.newSingleThreadExecutor();

        btnSearch.setOnClickListener(v -> searchFood());
    }

    private void searchFood() {
        String foodName = etSearch.getText().toString().trim();

        // Hide previous results
        tvResultError.setVisibility(View.GONE);
        tvResultLabel.setVisibility(View.GONE);
        tableMacronutrients.setVisibility(View.GONE);
        tableMinerals.setVisibility(View.GONE);
        tableVitamins.setVisibility(View.GONE);

        if (foodName.isEmpty()) {
            tvResultError.setVisibility(View.VISIBLE);
            tvResultError.setText(getString(R.string.error_empty_search));
            return;
        }

        executorService.execute(() -> {
            Food food = foodDao.findFood(foodName);

            runOnUiThread(() -> {
                if (!isFinishing()) {
                    if (food != null) {
                        // Set values for Macronutrients
                        tvCaloriesValue.setText(food.getCalories() + " kcal");
                        tvProteinValue.setText(food.getProtein() + " g");
                        tvCarbsValue.setText(food.getCarbs() + " g");
                        tvFatsValue.setText(food.getFats() + " g");
                        tvSugarValue.setText(food.getSugar() + " g");
                        tvFibreValue.setText(food.getFibre() + " g");
                        tvCholesterolValue.setText(food.getCholesterol() + " mg");

                        // Set values for Minerals
                        tvCalciumValue.setText(food.getCalcium() + " mg");
                        tvPhosphorusValue.setText(food.getPhosphorus() + " mg");
                        tvMagnesiumValue.setText(food.getMagnesium() + " mg");
                        tvSodiumValue.setText(food.getSodium() + " mg");
                        tvPotassiumValue.setText(food.getPotassiumMg() + " mg");
                        tvIronValue.setText(food.getIronMg() + " mg");
                        tvCopperValue.setText(food.getCopperMg() + " mg");
                        tvSeleniumValue.setText(food.getSeleniumUg() + " µg");
                        tvChromiumValue.setText(food.getChromiumMg() + " mg");
                        tvManganeseValue.setText(food.getManganeseMg() + " mg");
                        tvMolybdenumValue.setText(food.getMolybdenumMg() + " mg");
                        tvZincValue.setText(food.getZincMg() + " mg");

                        // Set values for Vitamins
                        tvVitAValue.setText(food.getVitAUg() + " µg");
                        tvVitEValue.setText(food.getVitEMg() + " mg");
                        tvVitD2Value.setText(food.getVitD2Ug() + " µg");
                        tvVitD3Value.setText(food.getVitD3Ug() + " µg");
                        tvVitK1Value.setText(food.getVitK1Ug() + " µg");
                        tvVitK2Value.setText(food.getVitK2Ug() + " µg");

                        tvFolateValue.setText(food.getFolateUg() + " µg");
                        tvVitB1Value.setText(food.getVitB1Mg() + " µg");
                        tvVitB2Value.setText(food.getVitB2Mg() + " µg");

                        tvVitB3Value.setText(food.getVitB3Mg() + " µg");
                        tvVitB5Value.setText(food.getVitB5Mg() + " µg");
                        tvVitB6Value.setText(food.getVitB6Mg() + " µg");
                        tvVitB7Value.setText(food.getVitB7Ug() + " µg");


                        tvResultLabel.setVisibility(View.VISIBLE);
                        tableMacronutrients.setVisibility(View.VISIBLE);
                        tableMinerals.setVisibility(View.VISIBLE);
                        tableVitamins.setVisibility(View.VISIBLE);
                    } else {
                        tvResultError.setVisibility(View.VISIBLE);
                        tvResultError.setText("Food item not found!");
                    }
                }
            });
        });
    }
}
