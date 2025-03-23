package com.example.healthcompanion;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.text.DecimalFormat;

public class BmiCalculator extends AppCompatActivity {
    private EditText etWeight, etHeight;
    private TextView tvResult, tvCategory;
    private ProgressBar bmiProgress;
    private View bmiPointer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);

        etWeight = findViewById(R.id.et_weight);
        etHeight = findViewById(R.id.et_height);
        tvResult = findViewById(R.id.tv_result);
        tvCategory = findViewById(R.id.tv_category);
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

            // Add input validation
            if (weight <= 0 || height <= 0) {
                Toast.makeText(this, "Weight and height must be positive values", Toast.LENGTH_SHORT).show();
                return;
            }
            if (height < 0.5 || height > 2.5) { // Height between 50 cm and 250 cm
                Toast.makeText(this, "Height must be between 50 cm and 250 cm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (weight < 20 || weight > 300) { // Weight between 20 kg and 300 kg
                Toast.makeText(this, "Weight must be between 20 kg and 300 kg", Toast.LENGTH_SHORT).show();
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
                weightAdvice = "You need to gain at least " + df.format(neededGain) + " kg to reach a normal weight.";
            } else if (bmi > 24.9) {
                float neededLoss = weight - maxWeight;
                weightAdvice = "You need to lose at least " + df.format(neededLoss) + " kg to reach a normal weight.";
            } else {
                weightAdvice = "You are in the normal weight range!";
            }

            tvResult.setText("Your BMI: " + df.format(bmi));
            tvCategory.setText(bmiCategory + "\n" + weightAdvice);
            updateProgressBarAndPointer(bmi);
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

    private void updateProgressBarAndPointer(float bmi) {
        // Set progress (cap at 40 since max is 40 in XML)
        int progress = Math.min((int) bmi, 40);

        // Use custom drawable for sharp color transitions
        BmiProgressDrawable progressDrawable = new BmiProgressDrawable();
        progressDrawable.setMax(40);
        progressDrawable.setProgress(progress);
        bmiProgress.setProgressDrawable(progressDrawable);
        bmiProgress.setProgress(progress);

        // Use translation to move the pointer
        float bias = Math.min(bmi, 40.0f) / 40.0f; // Cap BMI at 40 before calculating bias
        bmiProgress.post(() -> {
            float progressBarWidth = bmiProgress.getWidth();
            float pointerPosition = progressBarWidth * bias - (bmiPointer.getWidth() / 2.0f);
            // Clamp the position to ensure the pointer stays within bounds
            float minPosition = 0.0f;
            float maxPosition = progressBarWidth - bmiPointer.getWidth();
            pointerPosition = Math.max(minPosition, Math.min(maxPosition, pointerPosition));
            bmiPointer.setTranslationX(pointerPosition);
        });

        // Debug log to verify bias
        android.util.Log.d("BMI", "BMI: " + bmi + ", Bias: " + bias);

        // Adjust pointer color based on category (using tint to preserve shape)
        int pointerColor;
        if (bmi < 18.5) {
            pointerColor = getResources().getColor(R.color.underweight);
        } else if (bmi < 25) {
            pointerColor = getResources().getColor(R.color.normal);
        } else if (bmi < 30) {
            pointerColor = getResources().getColor(R.color.overweight);
        } else {
            pointerColor = getResources().getColor(R.color.obese);
        }
        bmiPointer.setBackgroundTintList(ColorStateList.valueOf(pointerColor));
    }
}