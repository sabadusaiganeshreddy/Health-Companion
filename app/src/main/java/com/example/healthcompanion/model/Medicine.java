package com.example.healthcompanion.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicine_table")
public class Medicine {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String dosage;
    public int hour, minute;
    public long startDateMillis;
    public long endDateMillis;
}
