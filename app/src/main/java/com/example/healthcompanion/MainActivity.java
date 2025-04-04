package com.example.healthcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.card_symptom_predictor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SymptomPredictor.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.card_bmi_calculator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BmiCalculator.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.card_food_library).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FoodLibrary.class);
                startActivity(intent);
            }
        });


        // ✅ Recipe Generator Button Integration
        findViewById(R.id.card_recipe_generator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipeGeneratorActivity.class);
                startActivity(intent);
            }
        });
    }
}

