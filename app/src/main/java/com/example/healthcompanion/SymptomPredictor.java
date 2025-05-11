package com.example.healthcompanion;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class SymptomPredictor extends AppCompatActivity {

    private MultiAutoCompleteTextView symptomsInput;
    private Button predictButton;
    private ProgressBar progressBar;
    private TextView disclaimer;
    private Spinner languageSpinner;
    private RecyclerView resultsRecyclerView;
    private DiseaseAdapter diseaseAdapter;

    // Store disease data from JSON
    private List<DiseaseEntry> diseaseEntries = new ArrayList<>();
    private Set<String> allSymptoms = new HashSet<>();

    private static class DiseaseResult {
        String name;
        double score;

        public DiseaseResult(String name, double score) {
            this.name = name;
            this.score = score;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_predictor);

        // Initialize views
        symptomsInput = findViewById(R.id.et_symptoms);
        predictButton = findViewById(R.id.btn_predict);
        progressBar = findViewById(R.id.progress_bar);
        disclaimer = findViewById(R.id.tv_disclaimer);
        languageSpinner = findViewById(R.id.spinner_language);
        resultsRecyclerView = findViewById(R.id.results_recycler_view);

        // Set disclaimer text
        disclaimer.setText("DISCLAIMER: This app provides predictions based on symptoms and is NOT a substitute for professional medical advice. " +
                "Please consult a healthcare professional for proper diagnosis and treatment.");

        // Load disease data from JSON
        loadDiseaseData();

        // Set up autocomplete for symptoms
        setupSymptomAutocomplete();

        // Populate language spinner
        populateLanguageSpinner();

        // Set up RecyclerView
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<DiseaseResult> initialDiseaseList = new ArrayList<>();
        diseaseAdapter = new DiseaseAdapter(initialDiseaseList, (diseaseName) -> {
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            DiseaseDetailDialog dialog = new DiseaseDetailDialog(this, diseaseName, selectedLanguage);
            dialog.show();
        });
        resultsRecyclerView.setAdapter(diseaseAdapter);

        // Set click listener for predict button
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predictDisease();
            }
        });
    }

    private void populateLanguageSpinner() {
        List<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("Hindi");
        languages.add("Telugu");
        languages.add("Marathi");
        languages.add("Kannada");
        languages.add("Tamil");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languages);
        languageSpinner.setAdapter(adapter);
    }

    private void loadDiseaseData() {
        try {
            // Read JSON file from assets
            String jsonString = loadJSONFromAsset("disease_symptom_data.json");
            JSONArray jsonArray = new JSONArray(jsonString);

            // Parse JSON data
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject diseaseObj = jsonArray.getJSONObject(i);
                String disease = diseaseObj.getString("disease").toLowerCase(Locale.US); // Store in lowercase
                JSONArray symptomsArray = diseaseObj.getJSONArray("symptoms");
                List<String> symptoms = new ArrayList<>();

                for (int j = 0; j < symptomsArray.length(); j++) {
                    String symptom = symptomsArray.getString(j).toLowerCase(Locale.US); // Store in lowercase
                    symptoms.add(symptom);
                    allSymptoms.add(symptom); // Add to all symptoms set
                }

                // Each JSON object becomes a disease entry
                diseaseEntries.add(new DiseaseEntry(disease, symptoms));
            }

            Log.d("DiseasePredictor", "Loaded " + diseaseEntries.size() + " disease entries");
            Log.d("DiseasePredictor", "Found " + allSymptoms.size() + " unique symptoms");

        } catch (JSONException e) {
            Log.e("DiseasePredictor", "Error parsing JSON", e);
            Toast.makeText(this, "Error loading disease data", Toast.LENGTH_SHORT).show();
        }
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e("DiseasePredictor", "Error reading JSON file", ex);
            Toast.makeText(this, "Error reading disease data file", Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    private void setupSymptomAutocomplete() {
        // Convert set to sorted list
        List<String> symptomsList = new ArrayList<>(allSymptoms);
        Collections.sort(symptomsList);

        // Create adapter for autocomplete
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                symptomsList);

        symptomsInput.setAdapter(adapter);
        symptomsInput.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        symptomsInput.setThreshold(1); // Start suggesting after first character
    }

    private void predictDisease() {
        // Get user input
        String input = symptomsInput.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter at least one symptom", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.result_card).setVisibility(View.GONE);
        resultsRecyclerView.setVisibility(View.GONE);

        // Parse entered symptoms
        String[] inputSymptoms = input.split("\\s*,\\s*");
        List<String> userSymptoms = new ArrayList<>();
        for (String symptom : inputSymptoms) {
            if (!symptom.trim().isEmpty()) {
                userSymptoms.add(symptom.trim().toLowerCase(Locale.US)); // Normalize user input
            }
        }

        // Calculate disease matches
        Map<String, Double> diseaseScores = calculateDiseaseScores(userSymptoms);

        // Sort diseases by score (highest first)
        List<Map.Entry<String, Double>> sortedDiseases = new ArrayList<>(diseaseScores.entrySet());
        Collections.sort(sortedDiseases, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Prepare list of top disease results
        List<DiseaseResult> topDiseaseResults = new ArrayList<>();

        if (sortedDiseases.isEmpty() || sortedDiseases.get(0).getValue() < 0.1) {
            Toast.makeText(this, "No significant matches found", Toast.LENGTH_SHORT).show();
        } else {
            int displayCount = 0;
            for (int i = 0; i < Math.min(5, sortedDiseases.size()); i++) {
                Map.Entry<String, Double> entry = sortedDiseases.get(i);
                if (entry.getValue() >= 0.2) {
                    topDiseaseResults.add(new DiseaseResult(capitalizeDiseaseName(entry.getKey()), entry.getValue()));
                    displayCount++;
                }
            }

            if (displayCount == 0) {
                Toast.makeText(this, "No significant matches found", Toast.LENGTH_SHORT).show();
            }
        }

        // Hide progress and show results in RecyclerView
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.result_card).setVisibility(View.VISIBLE);
        resultsRecyclerView.setVisibility(View.VISIBLE);
        diseaseAdapter.setDiseaseList(topDiseaseResults);
        diseaseAdapter.notifyDataSetChanged();
    }

    private Map<String, Double> calculateDiseaseScores(List<String> userSymptoms) {
        Map<String, Double> scores = new HashMap<>();
        Map<String, List<String>> matchedSymptoms = new HashMap<>();

        // Initialize with all diseases at 0
        for (DiseaseEntry entry : diseaseEntries) {
            scores.put(entry.disease, 0.0);
            matchedSymptoms.put(entry.disease, new ArrayList<>());
        }

        // For each disease entry, calculate symptom match
        for (DiseaseEntry entry : diseaseEntries) {
            int matchCount = 0;
            List<String> currentMatchedSymptoms = new ArrayList<>();

            // Check each user symptom against this disease's symptoms
            for (String userSymptom : userSymptoms) {
                for (String entrySymptom : entry.symptoms) {
                    if (entrySymptom.contains(userSymptom) || userSymptom.contains(entrySymptom)) {
                        matchCount++;
                        currentMatchedSymptoms.add(userSymptom + " <-> " + entrySymptom);
                        break; // Move to the next user symptom once a match is found for this entry symptom
                    }
                }
            }

            // Calculate score based on the number of matching symptoms
            if (!entry.symptoms().isEmpty() && !userSymptoms.isEmpty()) {
                double score = (double) matchCount / Math.max(entry.symptoms().size(), userSymptoms.size());
                scores.put(entry.disease(), Math.max(scores.get(entry.disease()), score)); // Keep the highest score if multiple user symptoms match
                if (score > 0) {
                    if (!matchedSymptoms.containsKey(entry.disease())) {
                        matchedSymptoms.put(entry.disease(), new ArrayList<>(currentMatchedSymptoms));
                    } else {
                        matchedSymptoms.get(entry.disease()).addAll(currentMatchedSymptoms);
                    }
                }
            }
        }

        // Log matched symptoms for debugging
        for (Map.Entry<String, List<String>> entry : matchedSymptoms.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                Log.d("SymptomMatches", entry.getKey() + ": " + entry.getValue());
            }
        }

        return scores;
    }

    // Helper method to properly capitalize disease names
    private String capitalizeDiseaseName(String diseaseName) {
        if (diseaseName == null || diseaseName.isEmpty()) {
            return "";
        }

        String[] words = diseaseName.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
            }
        }

        return result.toString().trim();
    }

    // RecyclerView Adapter for displaying clickable disease names with scores
    private class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.ViewHolder> {
        private List<DiseaseResult> diseaseResults;
        private OnDiseaseClickListener clickListener;

        public DiseaseAdapter(List<DiseaseResult> diseaseResults, OnDiseaseClickListener clickListener) {
            this.diseaseResults = diseaseResults;
            this.clickListener = clickListener;
        }

        public void setDiseaseList(List<DiseaseResult> newList) {
            this.diseaseResults = newList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(16, 16, 16, 16);
            textView.setTextSize(16);
            textView.setClickable(true);
            textView.setFocusable(true);
            textView.setBackgroundResource(R.drawable.list_item_background);
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DiseaseResult result = diseaseResults.get(position);
            String diseaseName = result.name;
            double score = result.score;

            String matchLevel;
            if (score >= 0.8) {
                matchLevel = "Very strong";
            } else if (score >= 0.6) {
                matchLevel = "Strong";
            } else if (score >= 0.4) {
                matchLevel = "Moderate";
            } else {
                matchLevel = "Possible";
            }

            holder.diseaseTextView.setText(String.format("%s\n%s match (%.0f%%)", diseaseName, matchLevel, score * 100));
            holder.diseaseTextView.setOnClickListener(v -> clickListener.onDiseaseClick(diseaseName));
        }

        @Override
        public int getItemCount() {
            return diseaseResults.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView diseaseTextView;

            public ViewHolder(@NonNull TextView itemView) {
                super(itemView);
                diseaseTextView = itemView;
            }
        }
    }

    // Interface for handling clicks on disease names
    public interface OnDiseaseClickListener {
        void onDiseaseClick(String diseaseName);
    }

    // Class to hold disease data
    private static class DiseaseEntry {
        String disease;
        List<String> symptoms;

        DiseaseEntry(String disease, List<String> symptoms) {
            this.disease = disease;
            this.symptoms = symptoms;
        }

        public String disease() {
            return disease;
        }

        public List<String> symptoms() {
            return symptoms;
        }
    }
}