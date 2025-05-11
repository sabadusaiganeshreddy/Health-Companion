package com.example.healthcompanion;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class DiseaseDetailDialog extends Dialog {

    private String diseaseName;
    private String language;
    private TextView titleView;
    private TextView descriptionView;
    private ProgressBar progressBar;
    private Button closeButton;

    public DiseaseDetailDialog(@NonNull Context context, String diseaseName, String language) {
        super(context);
        this.diseaseName = diseaseName;
        this.language = language;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove default title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Inflate layout
        View view = LayoutInflater.from(getContext()).inflate(R.layout.disease_detail_dialog, null);
        setContentView(view);

        // Initialize views
        titleView = findViewById(R.id.tv_disease_title);
        descriptionView = findViewById(R.id.tv_disease_description);
        progressBar = findViewById(R.id.progress_description);
        closeButton = findViewById(R.id.btn_close);

        // Set disease name
        titleView.setText(diseaseName);

        // Set close button listener
        closeButton.setOnClickListener(v -> dismiss());

        // Load disease description
        loadDiseaseDescription();
    }

    private void loadDiseaseDescription() {
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        descriptionView.setVisibility(View.GONE);

        // Call Gemini API to get disease description
        GeminiService.getDiseaseDescription(diseaseName, language, new GeminiService.DiseaseDescriptionCallback() {
            @Override
            public void onSuccess(String description) {
                // Update UI on main thread
                titleView.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    descriptionView.setVisibility(View.VISIBLE);
                    descriptionView.setText(description);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Show error message
                titleView.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    descriptionView.setVisibility(View.VISIBLE);
                    descriptionView.setText("Error loading description: " + errorMessage);
                    Toast.makeText(getContext(), "Failed to load disease information", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}