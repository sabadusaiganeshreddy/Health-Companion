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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Food.class}, version = 1, exportSchema = false)
public abstract class FoodDatabase extends RoomDatabase {
    public abstract FoodDao foodDao();
    private static volatile FoodDatabase INSTANCE;
    private static final ExecutorService executor = Executors.newFixedThreadPool(4); // Faster inserts

    public static FoodDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FoodDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FoodDatabase.class, "food_database")
                            .fallbackToDestructiveMigration()  // Prevents crashes on updates
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    executor.execute(() -> {
                                        FoodDao dao = INSTANCE.foodDao();  // Ensure INSTANCE is available
                                        loadCsvData(context, dao);
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
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open("food_data.csv")))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {  // Skip the header line
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",\\s*"); // Split and remove extra spaces
                if (parts.length < 5) {
                    Log.e("CSV_ERROR", "Skipping invalid line: " + line);
                    continue;
                }

                try {
                    String name = parts[0].trim();
                    int calories = (int) Double.parseDouble(parts[1].trim());  // Convert float to int
                    double protein = Double.parseDouble(parts[2].trim());
                    double carbs = Double.parseDouble(parts[3].trim());
                    double fats = Double.parseDouble(parts[4].trim());

                    Food food = new Food(name, calories, protein, carbs, fats);
                    dao.insertAll(food);
                    Log.d("DB_INSERT", "Inserted: " + name);
                } catch (NumberFormatException e) {
                    Log.e("CSV_ERROR", "Invalid number format in line: " + line, e);
                }
            }
        } catch (IOException e) {
            Log.e("CSV_ERROR", "Error reading CSV file", e);
        }
    }
}
