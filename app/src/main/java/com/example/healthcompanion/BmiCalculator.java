package com.example.healthcompanion;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.text.DecimalFormat;

public class BmiCalculator extends AppCompatActivity {
    private EditText etWeight, etHeight;
    private TextView tvResult, tvWeightAdvice;
    private ProgressBar bmiProgress;
    private View bmiPointer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);

        etWeight = findViewById(R.id.et_weight);
        etHeight = findViewById(R.id.et_height);
        tvResult = findViewById(R.id.tv_result);
        bmiProgress = findViewById(R.id.bmi_progress);
        bmiPointer = findViewById(R.id.bmi_pointer);
        Button btnCalculate = findViewById(R.id.btn_calculate);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });
    }

    private void calculateBMI() {
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Please enter weight and height", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float weight = Float.parseFloat(weightStr);
            float height = Float.parseFloat(heightStr) / 100; // Convert cm to meters

            if (weight <= 0 || height <= 0) {
                Toast.makeText(this, "Weight and height must be positive values", Toast.LENGTH_SHORT).show();
                return;
            }

            float bmi = weight / (height * height);
            DecimalFormat df = new DecimalFormat("#.#");
            String bmiCategory = getBMICategory(bmi);

            float minWeight = 18.5f * (height * height);
            float maxWeight = 24.9f * (height * height);

            String weightAdvice;
            if (bmi < 18.5) {
                float neededGain = minWeight - weight;
                weightAdvice = "\nYou need to gain at least " + df.format(neededGain) + " kg to reach a normal weight.";
            } else if (bmi > 24.9) {
                float neededLoss = weight - maxWeight;
                weightAdvice = "\nYou need to lose at least " + df.format(neededLoss) + " kg to reach a normal weight.";
            } else {
                weightAdvice = "\nYou are in the normal weight range!";
            }

            tvResult.setText("Your BMI: " + df.format(bmi) + "\n" + bmiCategory + weightAdvice);
            updateProgressBar(bmi);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input. Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private String getBMICategory(float bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal weight";
        else if (bmi < 30) return "Overweight";
        else return "Obese";
    }

    private void updateProgressBar(float bmi) {
        int progress = (int) bmi;
        bmiProgress.setProgress(progress);

        int progressColor;
        if (bmi < 18.5) {
            progressColor = Color.BLUE; // Underweight
        } else if (bmi < 25) {
            progressColor = Color.GREEN; // Normal weight
        } else if (bmi < 30) {
            progressColor = Color.YELLOW; // Overweight
        } else {
            progressColor = Color.RED; // Obese
        }

        // Change the progress bar color dynamically
        Drawable progressDrawable = bmiProgress.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
        bmiProgress.setProgressDrawable(progressDrawable);
    }

}
