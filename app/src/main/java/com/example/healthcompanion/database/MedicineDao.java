package com.example.healthcompanion.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.healthcompanion.model.Medicine;

import java.util.List;

@Dao
public interface MedicineDao {
    @Insert
    void insert(Medicine medicine);

    @Query("SELECT * FROM medicine_table")
    List<Medicine> getAllMedicines();
}
