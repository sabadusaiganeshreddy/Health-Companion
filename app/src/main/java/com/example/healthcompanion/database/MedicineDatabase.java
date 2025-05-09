package com.example.healthcompanion.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.healthcompanion.model.Medicine;

@Database(entities = {Medicine.class}, version = 1)
public abstract class MedicineDatabase extends RoomDatabase {
    private static MedicineDatabase instance;

    public abstract MedicineDao medicineDao();

    public static synchronized MedicineDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MedicineDatabase.class, "medicine_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
