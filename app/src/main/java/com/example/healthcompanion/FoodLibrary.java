package com.example.healthcompanion;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthcompanion.database.FoodDatabase;
import com.example.healthcompanion.model.Food;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodLibrary extends AppCompatActivity {
    private EditText etSearch;
    private TextView tvResult;
    private FoodDatabase db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_library);

        etSearch = findViewById(R.id.et_search);
        tvResult = findViewById(R.id.tv_result);
        Button btnSearch = findViewById(R.id.btn_search);

        db = FoodDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor(); // Use Executor instead of new Thread()

        btnSearch.setOnClickListener(v -> searchFood());
    }

    private void searchFood() {
        String searchQuery = etSearch.getText().toString().toLowerCase().trim();

        if (searchQuery.isEmpty()) {
            tvResult.setText(getString(R.string.error_empty_search)); // Use strings.xml
            return;
        }

        executorService.execute(() -> {
            if (db != null) {
                Food food = db.foodDao().findFood(searchQuery);

                runOnUiThread(() -> {
                    if (!isFinishing()) { // Prevent crashes if activity is destroyed
                        if (food != null) {
                            String nutritionalInfo = getString(R.string.food_nutrition_info,
                                    food.getCalories(), food.getProtein(), food.getCarbs(), food.getFats());
                            tvResult.setText(nutritionalInfo);
                        } else {
                            tvResult.setText(getString(R.string.error_food_not_found, searchQuery));
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shut down ExecutorService to avoid memory leaks
    }
}
