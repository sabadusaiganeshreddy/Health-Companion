package com.example.first;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BMICalculatorActivity extends AppCompatActivity {

    private EditText etWeight;
    private EditText etHeight;
    private Button btnCalculateBMI;
    private TextView tvBMIDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmicalculator);

        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        btnCalculateBMI = findViewById(R.id.btnCalculateBMI);
        tvBMIDisplay = findViewById(R.id.tvBMIDisplay);

        btnCalculateBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });
    }

    private void calculateBMI() {
        String weightStr = etWeight.getText().toString();
        String heightStr = etHeight.getText().toString();

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            tvBMIDisplay.setText("Please enter both weight and height.");
            return;
        }

        float weight = Float.parseFloat(weightStr);
        float height = Float.parseFloat(heightStr) / 100; // Convert from cm to meters

        float bmi = weight / (height * height);
        tvBMIDisplay.setText("Your BMI is: " + String.format("%.2f", bmi));
    }
}