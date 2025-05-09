package com.example.healthcompanion;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SymptomPredictor extends AppCompatActivity {

    private MultiAutoCompleteTextView symptomsInput;
    private Button predictButton;
    private ProgressBar progressBar;
    private TextView predictionResult;
    private TextView disclaimer;

    // Store disease data from JSON
    private List<DiseaseEntry> diseaseEntries = new ArrayList<>();
    private Set<String> allSymptoms = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_predictor);

        // Initialize views
        symptomsInput = findViewById(R.id.et_symptoms);
        predictButton = findViewById(R.id.btn_predict);
        progressBar = findViewById(R.id.progress_bar);
        predictionResult = findViewById(R.id.tv_prediction);
        disclaimer = findViewById(R.id.tv_disclaimer);

        // Set disclaimer text
        disclaimer.setText("DISCLAIMER: This app provides predictions based on symptoms and is NOT a substitute for professional medical advice. " +
                "Please consult a healthcare professional for proper diagnosis and treatment.");

        // Load disease data from JSON
        loadDiseaseData();

        // Set up autocomplete for symptoms
        setupSymptomAutocomplete();

        // Set click listener for predict button
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predictDisease();
            }
        });
    }

    private void loadDiseaseData() {
        try {
            // Read JSON file from assets
            String jsonString = loadJSONFromAsset("disease_symptom_data.json");
            JSONArray jsonArray = new JSONArray(jsonString);

            // Parse JSON data
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject diseaseObj = jsonArray.getJSONObject(i);
                String disease = diseaseObj.getString("disease");

                JSONArray symptomsArray = diseaseObj.getJSONArray("symptoms");
                List<String> symptoms = new ArrayList<>();

                for (int j = 0; j < symptomsArray.length(); j++) {
                    String symptom = symptomsArray.getString(j);
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
        predictionResult.setText("");
        findViewById(R.id.result_card).setVisibility(View.GONE);

        // Parse entered symptoms
        String[] inputSymptoms = input.split("\\s*,\\s*");
        List<String> userSymptoms = new ArrayList<>();
        for (String symptom : inputSymptoms) {
            if (!symptom.trim().isEmpty()) {
                userSymptoms.add(symptom.trim().toLowerCase());
            }
        }

        // Calculate disease matches
        Map<String, Double> diseaseScores = calculateDiseaseScores(userSymptoms);

        // Sort diseases by score (highest first)
        List<Map.Entry<String, Double>> sortedDiseases = new ArrayList<>(diseaseScores.entrySet());
        Collections.sort(sortedDiseases, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Build result text
        StringBuilder resultBuilder = new StringBuilder();

        if (sortedDiseases.isEmpty() || sortedDiseases.get(0).getValue() < 0.1) {
            resultBuilder.append("No significant matches found for the symptoms provided.\n\n");
            resultBuilder.append("Suggestions:\n");
            resultBuilder.append("• Try using more specific symptoms\n");
            resultBuilder.append("• Check symptom spelling\n");
            resultBuilder.append("• Add more symptoms if possible\n\n");
            resultBuilder.append("Remember: Always consult a healthcare professional for proper diagnosis.");
        } else {
            resultBuilder.append("Based on the symptoms you provided, here are potential conditions:\n\n");

            // Show top matches with at least 20% match
            int displayCount = 0;
            for (int i = 0; i < Math.min(5, sortedDiseases.size()); i++) {
                Map.Entry<String, Double> entry = sortedDiseases.get(i);
                String diseaseName = entry.getKey();
                double score = entry.getValue();

                if (score >= 0.2) { // Only show reasonable matches (20% or higher)
                    // Capitalize disease name properly
                    String formattedName = capitalizeDiseaseName(diseaseName);

                    // Add match level description
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

                    resultBuilder.append(formattedName)
                            .append("\n")
                            .append(matchLevel)
                            .append(" match (")
                            .append(String.format("%.0f%%", score * 100))
                            .append(")\n\n");

                    displayCount++;
                }
            }

            if (displayCount == 0) {
                resultBuilder.append("No significant matches found. Please check your symptoms or consult a healthcare provider.");
            }
            // The "IMPORTANT" text is removed here
        }

        // Hide progress and show results
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.result_card).setVisibility(View.VISIBLE);
        predictionResult.setText(resultBuilder.toString());
    }

    private Map<String, Double> calculateDiseaseScores(List<String> userSymptoms) {
        Map<String, Double> scores = new HashMap<>();
        Map<String, Integer> matchCounts = new HashMap<>();
        Map<String, List<String>> matchedSymptoms = new HashMap<>();

        // Normalize user symptoms (convert to lowercase)
        List<String> normalizedUserSymptoms = new ArrayList<>();
        for (String symptom : userSymptoms) {
            normalizedUserSymptoms.add(symptom.toLowerCase().trim());
        }

        // Initialize with all diseases at 0
        for (DiseaseEntry entry : diseaseEntries) {
            scores.put(entry.disease, 0.0);
            matchCounts.put(entry.disease, 0);
            matchedSymptoms.put(entry.disease, new ArrayList<>());
        }

        // For each disease entry, calculate symptom match
        for (DiseaseEntry entry : diseaseEntries) {
            List<String> entrySymptoms = new ArrayList<>();
            for (String symptom : entry.symptoms) {
                entrySymptoms.add(symptom.toLowerCase().trim());
            }

            int matchCount = 0;
            List<String> currentMatchedSymptoms = new ArrayList<>();

            // Check each user symptom against this disease's symptoms
            for (String userSymptom : normalizedUserSymptoms) {
                boolean matched = false;

                // Try exact matches first
                if (entrySymptoms.contains(userSymptom)) {
                    matched = true;
                    currentMatchedSymptoms.add(userSymptom);
                } else {
                    // Try partial matches
                    for (String entrySymptom : entrySymptoms) {
                        // Check if user symptom is contained in entry symptom or vice versa
                        if (entrySymptom.contains(userSymptom) || userSymptom.contains(entrySymptom)) {
                            matched = true;
                            currentMatchedSymptoms.add(userSymptom + " → " + entrySymptom);
                            break;
                        }

                        // Check for word-level matches (e.g., "chest pain" matches "pain in chest")
                        String[] userWords = userSymptom.split("\\s+");
                        String[] entryWords = entrySymptom.split("\\s+");
                        int wordMatches = 0;

                        for (String userWord : userWords) {
                            if (userWord.length() <= 3) continue; // Skip short words like "and", "the", etc.

                            for (String entryWord : entryWords) {
                                if (entryWord.length() <= 3) continue;

                                if (userWord.equals(entryWord) ||
                                        userWord.contains(entryWord) ||
                                        entryWord.contains(userWord)) {
                                    wordMatches++;
                                    break;
                                }
                            }
                        }

                        // If more than half of significant words match
                        if (userWords.length > 0 && wordMatches >= Math.max(1, userWords.length / 2)) {
                            matched = true;
                            currentMatchedSymptoms.add(userSymptom + " ~ " + entrySymptom);
                            break;
                        }
                    }
                }

                if (matched) {
                    matchCount++;
                }
            }

            // Calculate scores
            int totalDiseaseSymptoms = entry.symptoms.size();
            int totalUserSymptoms = normalizedUserSymptoms.size();

            if (totalUserSymptoms > 0 && totalDiseaseSymptoms > 0) {
                // How many of the user's symptoms match this disease
                double userCoverage = (double) matchCount / totalUserSymptoms;

                // How many of the disease's symptoms are covered by user input
                double diseaseCoverage = (double) matchCount / totalDiseaseSymptoms;

                // Combined score - weight disease coverage more
                double score = (userCoverage * 0.4) + (diseaseCoverage * 0.6);

                // Bonus for exact matches
                int exactMatches = 0;
                for (String matchedSymptom : currentMatchedSymptoms) {
                    if (!matchedSymptom.contains("→") && !matchedSymptom.contains("~")) {
                        exactMatches++;
                    }
                }

                // Add bonus for exact matches (up to 20% boost)
                if (exactMatches > 0) {
                    double exactMatchBonus = Math.min(0.2, 0.05 * exactMatches);
                    score = Math.min(1.0, score + exactMatchBonus);
                }

                // Update score if better than current
                if (score > scores.get(entry.disease)) {
                    scores.put(entry.disease, score);
                    matchedSymptoms.put(entry.disease, currentMatchedSymptoms);
                }

                // Update match count
                matchCounts.put(entry.disease, matchCounts.get(entry.disease) + matchCount);
            }
        }

        // Store matched symptoms for display (could be used to show why a disease was suggested)
        for (String disease : matchedSymptoms.keySet()) {
            if (!matchedSymptoms.get(disease).isEmpty()) {
                // You could store this in a class variable to show details to the user
                Log.d("SymptomMatches", disease + ": " + matchedSymptoms.get(disease));
            }
        }

        return scores;
    }

    // Helper method to properly capitalize disease names
    private String capitalizeDiseaseName(String diseaseName) {
        if (diseaseName == null || diseaseName.isEmpty()) {
            return "";
        }

        // Split by spaces, hyphens, and other separators
        String[] words = diseaseName.split("[ -]");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                // Don't capitalize certain medical articles and prepositions
                if (word.equalsIgnoreCase("of") || word.equalsIgnoreCase("the") ||
                        word.equalsIgnoreCase("and") || word.equalsIgnoreCase("in") ||
                        word.equalsIgnoreCase("with") || word.equalsIgnoreCase("or")) {
                    result.append(word.toLowerCase());
                } else {
                    // Capitalize first letter, lowercase the rest
                    result.append(word.substring(0, 1).toUpperCase());
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }

        return result.toString().trim();
    }

    // Class to hold disease data
    private static class DiseaseEntry {
        String disease;
        List<String> symptoms;

        DiseaseEntry(String disease, List<String> symptoms) {
            this.disease = disease;
            this.symptoms = symptoms;
        }
    }
}