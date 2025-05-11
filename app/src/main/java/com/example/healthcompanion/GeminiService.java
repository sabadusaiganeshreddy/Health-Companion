package com.example.healthcompanion;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiService {
    private static final String TAG = "GeminiService";
    private static final String API_KEY = "AIzaSyA-AqYGEMtYXiXDbuDwAPgQZJUpF9SDxlM";

    // Updated API URL - this is the most current Gemini API endpoint
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Example: 30 seconds connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Example: 60 seconds read timeout (for response)
            .writeTimeout(30, TimeUnit.SECONDS)   // Example: 30 seconds write timeout (for request - less critical here)
            .build();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public interface RecipeCallback {
        void onSuccess(Recipe recipe);
        void onFailure(String errorMessage);
    }

    public interface DiseaseDescriptionCallback {
        void onSuccess(String description);
        void onFailure(String errorMessage);
    }

    // New method for generating disease descriptions
    public static void getDiseaseDescription(String diseaseName, String language, DiseaseDescriptionCallback callback) {
        String languageInstruction = "";

        // Set language instruction based on selected language
        switch (language) {
            case "English":
                languageInstruction = "in English";
                break;
            case "Hindi":
                languageInstruction = "in Hindi language";
                break;
            case "Telugu":
                languageInstruction = "in Telugu language";
                break;
            case "Marathi":
                languageInstruction = "in Marathi language";
                break;
            case "Kannada":
                languageInstruction = "in Kannada language";
                break;
            case "Tamil":
                languageInstruction = "in Tamil language";
                break;
            default:
                languageInstruction = "in English";
                break;
        }

        String prompt = "Please provide a comprehensive and informative description of the disease \"" +
                diseaseName + "\" " + languageInstruction + ". Include information about: " +
                "1. Brief overview of the disease " +
                "2. Common symptoms " +
                "3. Causes " +
                "4. Risk factors " +
                "5. When to seek medical attention " +
                "6. Standard treatments " +
                "7. Prevention tips if applicable " +
                "Write in a clear, factual style that is accessible to general readers. " +
                "Avoid using overly technical language. Format with appropriate headings and paragraphs. " +
                "DO NOT use markdown formatting (no **, #, *, etc). Use plain text only.";

        JSONObject requestBody = new JSONObject();
        try {
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();

            part.put("text", prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);

            requestBody.put("contents", contents);

            Log.d(TAG, "Request Body: " + requestBody.toString(2));

        } catch (JSONException e) {
            callback.onFailure("JSON Error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(requestBody.toString(), JSON);

        // Print the full API URL for debugging
        Log.d(TAG, "API URL: " + API_URL);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API Request Failed: " + e.getMessage());
                callback.onFailure("API Request Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "No response body";
                Log.d(TAG, "API Response Code: " + response.code());
                Log.d(TAG, "API Response: " + responseData);

                if (!response.isSuccessful()) {
                    callback.onFailure("API Error " + response.code() + ": " + responseData);
                    return;
                }

                try {
                    JSONObject jsonResponse = new JSONObject(responseData);

                    if (jsonResponse.has("candidates") && jsonResponse.getJSONArray("candidates").length() > 0) {
                        JSONObject candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0);

                        if (candidate.has("content") &&
                                candidate.getJSONObject("content").has("parts") &&
                                candidate.getJSONObject("content").getJSONArray("parts").length() > 0) {

                            String generatedText = candidate.getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");

                            callback.onSuccess(generatedText);
                        } else {
                            callback.onFailure("Invalid response format");
                        }
                    } else {
                        callback.onFailure("No candidates in response");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }
        });
    }

    // Simple method for generating a recipe without the structured parsing
    public static void generateSimpleRecipe(String foodItem, RecipeCallback callback) {
        String prompt = "Generate a detailed recipe using " + foodItem +
                " as the main ingredient. Include name, ingredients, instructions, cooking time, and difficulty level.";

        JSONObject requestBody = new JSONObject();
        try {
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();

            part.put("text", prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);

            requestBody.put("contents", contents);

            Log.d(TAG, "Request Body: " + requestBody.toString(2));

        } catch (JSONException e) {
            callback.onFailure("JSON Error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(requestBody.toString(), JSON);

        // Print the full API URL for debugging
        Log.d(TAG, "API URL: " + API_URL);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API Request Failed: " + e.getMessage());
                callback.onFailure("API Request Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "No response body";
                Log.d(TAG, "API Response Code: " + response.code());
                Log.d(TAG, "API Response: " + responseData);

                if (!response.isSuccessful()) {
                    callback.onFailure("API Error " + response.code() + ": " + responseData);
                    return;
                }

                try {
                    JSONObject jsonResponse = new JSONObject(responseData);

                    if (jsonResponse.has("candidates") && jsonResponse.getJSONArray("candidates").length() > 0) {
                        JSONObject candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0);

                        if (candidate.has("content") &&
                                candidate.getJSONObject("content").has("parts") &&
                                candidate.getJSONObject("content").getJSONArray("parts").length() > 0) {

                            String generatedText = candidate.getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");

                            // Create a simple recipe object from the generated text
                            Recipe recipe = new Recipe();
                            recipe.setMainIngredient(foodItem);
                            recipe.setName(foodItem);

                            // Parse the text in a simple way
                            List<String> instructions = new ArrayList<>();
                            instructions.add(generatedText);
                            recipe.setInstructions(instructions);

                            callback.onSuccess(recipe);
                        } else {
                            callback.onFailure("Invalid response format");
                        }
                    } else {
                        callback.onFailure("No candidates in response");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }
        });
    }

    // Full recipe generation with structured output
    public static void generateRecipe(String foodItem, RecipeOptions options, RecipeCallback callback) {
        // Build a structured prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Please provide a traditional and authentic recipe for")
                .append(foodItem).append(",in indian style, including the following details:\n")
                .append(" - List of ingredients (with measurements).\n")
                .append(" - Clear and detailed step-by-step cooking instructions.\n")
                .append(" - Cooking time, servings, and difficulty level.\n")
                .append(" - No creative variations, substitutions, or twists—just the classic recipe.\n")
                .append(" - Make sure the recipe is suitable for home cooking and easy to follow.\n\n");

        if (options != null) {
            // Add options such as cuisine type, dietary preferences, etc.
            if (options.getCuisineType() != null && !options.getCuisineType().isEmpty() && !options.getCuisineType().equals("Any")) {
                promptBuilder.append("Cuisine type: ").append(options.getCuisineType()).append("\n");
            }

            if (options.getDietaryPreferences() != null && !options.getDietaryPreferences().isEmpty() && !options.getDietaryPreferences().equals("None")) {
                promptBuilder.append("Dietary preferences: ").append(options.getDietaryPreferences()).append("\n");
            }

            if (options.getDifficulty() != null && !options.getDifficulty().isEmpty() && !options.getDifficulty().equals("Any")) {
                promptBuilder.append("Difficulty level: ").append(options.getDifficulty()).append("\n");
            }

            if (options.getMaxCookingTime() > 0) {
                promptBuilder.append("Maximum cooking time: ").append(options.getMaxCookingTime()).append(" minutes\n");
            }

            if (options.getExcludedIngredients() != null && !options.getExcludedIngredients().isEmpty()) {
                promptBuilder.append("Exclude these ingredients: ").append(options.getExcludedIngredients()).append("\n");
            }
        }

        // Now use the promptBuilder to create a request to the API
        JSONObject requestBody = new JSONObject();
        try {
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();

            part.put("text", promptBuilder.toString());
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);

            requestBody.put("contents", contents);

            Log.d(TAG, "Request Body: " + requestBody.toString(2));

        } catch (JSONException e) {
            callback.onFailure("JSON Error: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(requestBody.toString(), JSON);

        // Send the request to the API
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API Request Failed: " + e.getMessage());
                callback.onFailure("API Request Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body() != null ? response.body().string() : "No response body";
                Log.d(TAG, "API Response Code: " + response.code());
                Log.d(TAG, "API Response: " + responseData);

                if (!response.isSuccessful()) {
                    callback.onFailure("API Error " + response.code() + ": " + responseData);
                    return;
                }

                try {
                    JSONObject jsonResponse = new JSONObject(responseData);

                    if (jsonResponse.has("candidates") && jsonResponse.getJSONArray("candidates").length() > 0) {
                        JSONObject candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0);

                        if (candidate.has("content") &&
                                candidate.getJSONObject("content").has("parts") &&
                                candidate.getJSONObject("content").getJSONArray("parts").length() > 0) {

                            String generatedText = candidate.getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");

                            // Create and return the recipe object
                            Recipe recipe = new Recipe();
                            recipe.setMainIngredient(foodItem);
                            recipe.setName(foodItem);

                            List<String> instructions = new ArrayList<>();
                            instructions.add(generatedText);
                            recipe.setInstructions(instructions);

                            callback.onSuccess(recipe);
                        } else {
                            callback.onFailure("Invalid response format");
                        }
                    } else {
                        callback.onFailure("No candidates in response");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }
        });
    }
}