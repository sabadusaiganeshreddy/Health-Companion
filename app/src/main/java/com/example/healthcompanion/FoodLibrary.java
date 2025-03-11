package com.example.healthcompanion;

import android.os.Bundle;
import android.view.View; // Import View
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout; // Import TableLayout
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthcompanion.database.FoodDatabase;
import com.example.healthcompanion.model.Food;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodLibrary extends AppCompatActivity {
    private EditText etSearch;
    private TextView tvResultError; // Renamed to tvResultError for error messages
    private TextView tvResultLabel;  // Label for Nutrition Table
    private TableLayout tableNutrition; // TableLayout for nutrition
    private TextView tvCaloriesValue, tvProteinValue, tvCarbsValue, tvFatsValue; // TextViews for table values
    private FoodDatabase db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_library);

        etSearch = findViewById(R.id.et_search);
        tvResultError = findViewById(R.id.tv_result_error); // Renamed to tvResultError in layout too if you want to be consistent
        tvResultLabel = findViewById(R.id.tv_result_label);
        tableNutrition = findViewById(R.id.table_nutrition);
        tvCaloriesValue = findViewById(R.id.tv_calories_value);
        tvProteinValue = findViewById(R.id.tv_protein_value);
        tvCarbsValue = findViewById(R.id.tv_carbs_value);
        tvFatsValue = findViewById(R.id.tv_fats_value);
        Button btnSearch = findViewById(R.id.btn_search);

        db = FoodDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        btnSearch.setOnClickListener(v -> searchFood());
    }

    private void searchFood() {
        String searchQuery = etSearch.getText().toString().toLowerCase().trim();

        if (searchQuery.isEmpty()) {
            tvResultError.setVisibility(View.VISIBLE); // Show error TextView
            tableNutrition.setVisibility(View.GONE);   // Hide table and label
            tvResultLabel.setVisibility(View.GONE);
            tvResultError.setText(getString(R.string.error_empty_search));
            return;
        }

        executorService.execute(() -> {
            if (db != null) {
                Food food = db.foodDao().findFood(searchQuery);

                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        if (food != null) {
                            tvResultError.setVisibility(View.GONE); // Hide error TextView
                            tvResultLabel.setVisibility(View.VISIBLE); // Show table label
                            tableNutrition.setVisibility(View.VISIBLE);   // Show table
                            tvCaloriesValue.setText(String.valueOf(food.getCalories()));
                            tvProteinValue.setText(String.valueOf(food.getProtein()));
                            tvCarbsValue.setText(String.valueOf(food.getCarbs()));
                            tvFatsValue.setText(String.valueOf(food.getFats()));
                        } else {
                            tvResultError.setVisibility(View.VISIBLE); // Show error TextView
                            tableNutrition.setVisibility(View.GONE);   // Hide table and label
                            tvResultLabel.setVisibility(View.GONE);
                            tvResultError.setText(getString(R.string.error_food_not_found, searchQuery));
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}