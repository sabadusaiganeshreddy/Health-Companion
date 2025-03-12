package com.example.healthcompanion.database;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.healthcompanion.model.Food;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Food.class}, version = 1, exportSchema = false)
public abstract class FoodDatabase extends RoomDatabase {
    public abstract FoodDao foodDao();
    private static volatile FoodDatabase INSTANCE;
    public static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static FoodDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FoodDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FoodDatabase.class, "food_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    executor.execute(() -> {
                                        FoodDatabase database = INSTANCE;
                                        if (database != null) {
                                            loadCsvData(context, database.foodDao());
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void loadCsvData(Context context, FoodDao dao) {
        List<Food> foodList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open("food_database2.csv")))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",\\s*");
                if (parts.length < 34) {
                    Log.e("CSV_ERROR", "Skipping invalid line: " + line);
                    continue;
                }

                try {
                    String name = parts[0].trim();
                    int calories = (int) Double.parseDouble(parts[1].trim());
                    double carbs = parseDoubleSafe(parts[2]);
                    double protein = parseDoubleSafe(parts[3]);
                    double fats = parseDoubleSafe(parts[4]);
                    double sugar = parseDoubleSafe(parts[5]);
                    double fibre = parseDoubleSafe(parts[6]);
                    double cholesterol = parseDoubleSafe(parts[7]);

                    double calcium = parseDoubleSafe(parts[8]);
                    double phosphorus = parseDoubleSafe(parts[9]);
                    double magnesium = parseDoubleSafe(parts[10]);
                    double sodium = parseDoubleSafe(parts[11]);
                    double potassiumMg = parseDoubleSafe(parts[12]);
                    double ironMg = parseDoubleSafe(parts[13]);
                    double copperMg = parseDoubleSafe(parts[14]);
                    double seleniumUg = parseDoubleSafe(parts[15]);
                    double chromiumMg = parseDoubleSafe(parts[16]);
                    double manganeseMg = parseDoubleSafe(parts[17]);
                    double molybdenumMg = parseDoubleSafe(parts[18]);
                    double zincMg = parseDoubleSafe(parts[19]);

                    double vitAUg = parseDoubleSafe(parts[20]);
                    double vitEMg = parseDoubleSafe(parts[21]);
                    double vitD2Ug = parseDoubleSafe(parts[22]);
                    double vitD3Ug = parseDoubleSafe(parts[23]);
                    double vitK1Ug = parseDoubleSafe(parts[24]);
                    double vitK2Ug = parseDoubleSafe(parts[25]);
                    double folateUg = parseDoubleSafe(parts[26]);
                    double vitB1Mg = parseDoubleSafe(parts[27]);
                    double vitB2Mg = parseDoubleSafe(parts[28]);
                    double vitB3Mg = parseDoubleSafe(parts[29]);
                    double vitB5Mg = parseDoubleSafe(parts[30]);
                    double vitB6Mg = parseDoubleSafe(parts[31]);
                    double vitB7Ug = parseDoubleSafe(parts[32]);

                    Food food = new Food(name, calories, protein, carbs, fats, sugar, fibre, cholesterol,
                            calcium, phosphorus, magnesium, sodium, potassiumMg, ironMg, copperMg,
                            seleniumUg, chromiumMg, manganeseMg, molybdenumMg, zincMg,
                            vitAUg, vitEMg, vitD2Ug, vitD3Ug, vitK1Ug, vitK2Ug,
                            folateUg, vitB1Mg, vitB2Mg, vitB3Mg, vitB5Mg, vitB6Mg, vitB7Ug);
                    foodList.add(food);

                } catch (NumberFormatException e) {
                    Log.e("CSV_ERROR", "Invalid number format: " + line, e);
                }
            }

            // Insert all food items in bulk for efficiency
            if (!foodList.isEmpty()) {
                dao.insertAll(foodList);
                Log.d("DB_INSERT", "Inserted " + foodList.size() + " food items.");
            }
        } catch (IOException e) {
            Log.e("CSV_ERROR", "Error reading CSV file", e);
        }
    }

    private static double parseDoubleSafe(String value) {
        try {
            return value.trim().isEmpty() ? 0.0 : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
