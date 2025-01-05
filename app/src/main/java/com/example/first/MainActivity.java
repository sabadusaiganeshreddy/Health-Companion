package com.example.first;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class MainActivity extends AppCompatActivity {

    private EditText etSymptoms;
    private Button btnPredict;
    private TextView tvPrediction;
    private ChipGroup chipGroup;
    private TextView tvLimitMessage;
    private static final int MAX_SYMPTOMS = 10;
    private Button btnGoToBMICalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSymptoms = findViewById(R.id.etSymptoms);
        btnPredict = findViewById(R.id.btnPredict);
        tvPrediction = findViewById(R.id.tvPrediction);
        chipGroup = findViewById(R.id.chipGroup);
        tvLimitMessage = findViewById(R.id.tvLimitMessage);
        btnGoToBMICalculator = findViewById(R.id.btnGoToBMICalculator);

        etSymptoms.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    addChip(etSymptoms.getText().toString());
                    etSymptoms.setText("");
                    return true;
                }
                return false;
            }
        });

        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Your prediction logic here
                tvPrediction.setText("Prediction logic not implemented yet.");
            }
        });

        btnGoToBMICalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BMICalculatorActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addChip(String symptom) {
        if (symptom.trim().isEmpty()) {
            return;
        }

        if (chipGroup.getChildCount() >= MAX_SYMPTOMS) {
            tvLimitMessage.setVisibility(View.VISIBLE);
            return;
        }

        Chip chip = new Chip(this);
        chip.setText(symptom);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(v);
                if (chipGroup.getChildCount() < MAX_SYMPTOMS) {
                    tvLimitMessage.setVisibility(View.GONE);
                }
            }
        });
        chipGroup.addView(chip);
    }
}