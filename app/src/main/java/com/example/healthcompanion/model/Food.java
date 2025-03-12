package com.example.healthcompanion.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "food_table")
public class Food {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "food_name")
    private final String name;

    @ColumnInfo(name = "calories")
    private final int calories;

    @ColumnInfo(name = "protein")
    private final double protein;

    @ColumnInfo(name = "carbs")
    private final double carbs;

    @ColumnInfo(name = "fats")
    private final double fats;

    @ColumnInfo(name = "sugar")
    private final double sugar;

    @ColumnInfo(name = "fibre")
    private final double fibre;

    @ColumnInfo(name = "cholesterol")
    private final double cholesterol;

    // Minerals
    @ColumnInfo(name = "calcium")
    private final double calcium;

    @ColumnInfo(name = "phosphorus")
    private final double phosphorus;

    @ColumnInfo(name = "magnesium")
    private final double magnesium;

    @ColumnInfo(name = "sodium")
    private final double sodium;

    @ColumnInfo(name = "potassium_mg")
    private final double potassiumMg;

    @ColumnInfo(name = "iron_mg")
    private final double ironMg;

    @ColumnInfo(name = "copper_mg")
    private final double copperMg;

    @ColumnInfo(name = "selenium_ug")
    private final double seleniumUg;

    @ColumnInfo(name = "chromium_mg")
    private final double chromiumMg;

    @ColumnInfo(name = "manganese_mg")
    private final double manganeseMg;

    @ColumnInfo(name = "molybdenum_mg")
    private final double molybdenumMg;

    @ColumnInfo(name = "zinc_mg")
    private final double zincMg;

    // Vitamins
    @ColumnInfo(name = "vit_a_ug")
    private final double vitAUg;

    @ColumnInfo(name = "vit_e_mg")
    private final double vitEMg;

    @ColumnInfo(name = "vitd2_ug")
    private final double vitD2Ug;

    @ColumnInfo(name = "vitd3_ug")
    private final double vitD3Ug;

    @ColumnInfo(name = "vitk1_ug")
    private final double vitK1Ug;

    @ColumnInfo(name = "vitk2_ug")
    private final double vitK2Ug;

    @ColumnInfo(name = "folate_ug")
    private final double folateUg;

    @ColumnInfo(name = "vitb1_mg")
    private final double vitB1Mg;

    @ColumnInfo(name = "vitb2_mg")
    private final double vitB2Mg;

    @ColumnInfo(name = "vitb3_mg")
    private final double vitB3Mg;

    @ColumnInfo(name = "vitb5_mg")
    private final double vitB5Mg;

    @ColumnInfo(name = "vitb6_mg")
    private final double vitB6Mg;

    @ColumnInfo(name = "vitb7_ug")
    private final double vitB7Ug;

    public Food(@NonNull String name, int calories, double protein, double carbs, double fats, double sugar, double fibre, double cholesterol,
                double calcium, double phosphorus, double magnesium, double sodium, double potassiumMg, double ironMg, double copperMg,
                double seleniumUg, double chromiumMg, double manganeseMg, double molybdenumMg, double zincMg,
                double vitAUg, double vitEMg, double vitD2Ug, double vitD3Ug, double vitK1Ug, double vitK2Ug,
                double folateUg, double vitB1Mg, double vitB2Mg, double vitB3Mg, double vitB5Mg, double vitB6Mg, double vitB7Ug) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.sugar = sugar;
        this.fibre = fibre;
        this.cholesterol = cholesterol;
        this.calcium = calcium;
        this.phosphorus = phosphorus;
        this.magnesium = magnesium;
        this.sodium = sodium;
        this.potassiumMg = potassiumMg;
        this.ironMg = ironMg;
        this.copperMg = copperMg;
        this.seleniumUg = seleniumUg;
        this.chromiumMg = chromiumMg;
        this.manganeseMg = manganeseMg;
        this.molybdenumMg = molybdenumMg;
        this.zincMg = zincMg;
        this.vitAUg = vitAUg;
        this.vitEMg = vitEMg;
        this.vitD2Ug = vitD2Ug;
        this.vitD3Ug = vitD3Ug;
        this.vitK1Ug = vitK1Ug;
        this.vitK2Ug = vitK2Ug;
        this.folateUg = folateUg;
        this.vitB1Mg = vitB1Mg;
        this.vitB2Mg = vitB2Mg;
        this.vitB3Mg = vitB3Mg;
        this.vitB5Mg = vitB5Mg;
        this.vitB6Mg = vitB6Mg;
        this.vitB7Ug = vitB7Ug;
    }

    @NonNull
    public String getName() { return name; }

    public int getCalories() { return calories; }

    public double getProtein() { return protein; }

    public double getCarbs() { return carbs; }

    public double getFats() { return fats; }

    public double getSugar() { return sugar; }

    public double getFibre() { return fibre; }

    public double getCholesterol() { return cholesterol; }

    // Getters for Minerals
    public double getCalcium() { return calcium; }

    public double getPhosphorus() { return phosphorus; }

    public double getMagnesium() { return magnesium; }

    public double getSodium() { return sodium; }

    public double getPotassiumMg() { return potassiumMg; }

    public double getIronMg() { return ironMg; }

    public double getCopperMg() { return copperMg; }

    public double getSeleniumUg() { return seleniumUg; }

    public double getChromiumMg() { return chromiumMg; }

    public double getManganeseMg() { return manganeseMg; }

    public double getMolybdenumMg() { return molybdenumMg; }

    public double getZincMg() { return zincMg; }

    // Getters for Vitamins
    public double getVitAUg() { return vitAUg; }

    public double getVitEMg() { return vitEMg; }

    public double getVitD2Ug() { return vitD2Ug; }

    public double getVitD3Ug() { return vitD3Ug; }

    public double getVitK1Ug() { return vitK1Ug; }

    public double getVitK2Ug() { return vitK2Ug; }

    public double getFolateUg() { return folateUg; }

    public double getVitB1Mg() { return vitB1Mg; }

    public double getVitB2Mg() { return vitB2Mg; }

    public double getVitB3Mg() { return vitB3Mg; }

    public double getVitB5Mg() { return vitB5Mg; }

    public double getVitB6Mg() { return vitB6Mg; }

    public double getVitB7Ug() { return vitB7Ug; }
}
