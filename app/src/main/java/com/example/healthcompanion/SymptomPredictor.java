package com.example.healthcompanion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SymptomPredictor extends AppCompatActivity {

    private EditText etSymptoms;
    private TextView tvPrediction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_predictor);

        etSymptoms = findViewById(R.id.et_symptoms);
        tvPrediction = findViewById(R.id.tv_prediction);
        Button btnPredict = findViewById(R.id.btn_predict);

        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predictDisease();
            }
        });
    }

    private void predictDisease() {
        String symptoms = etSymptoms.getText().toString();
        // Add your disease prediction logic here
        String prediction = "Predicted Disease based on symptoms: " + symptoms;
        tvPrediction.setText(prediction);
    }
}