package com.example.healthcompanion;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcompanion.database.MedicineDatabase;
import com.example.healthcompanion.model.Medicine;

import java.util.Calendar;

public class AddMedicineActivity extends AppCompatActivity {

    EditText editMedicineName, editDosage;
    TimePicker timePicker;
    DatePicker startDatePicker, endDatePicker;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        editMedicineName = findViewById(R.id.editMedicineName);
        editDosage = findViewById(R.id.editDosage);
        timePicker = findViewById(R.id.timePicker);
        startDatePicker = findViewById(R.id.startDatePicker);
        endDatePicker = findViewById(R.id.endDatePicker);
        btnSave = findViewById(R.id.btnSaveMedicine);

        btnSave.setOnClickListener(v -> {
            String name = editMedicineName.getText().toString().trim();
            String dosage = editDosage.getText().toString().trim();

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            Calendar startCal = Calendar.getInstance();
            startCal.set(startDatePicker.getYear(), startDatePicker.getMonth(), startDatePicker.getDayOfMonth());

            Calendar endCal = Calendar.getInstance();
            endCal.set(endDatePicker.getYear(), endDatePicker.getMonth(), endDatePicker.getDayOfMonth());

            if (name.isEmpty() || dosage.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Medicine medicine = new Medicine();
            medicine.name = name;
            medicine.dosage = dosage;
            medicine.hour = hour;
            medicine.minute = minute;
            medicine.startDateMillis = startCal.getTimeInMillis();
            medicine.endDateMillis = endCal.getTimeInMillis();

            new Thread(() -> {
                MedicineDatabase.getInstance(this).medicineDao().insert(medicine);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Medicine saved!", Toast.LENGTH_SHORT).show();
                    scheduleReminder(name, hour, minute);
                });
            }).start();
        });

    }
    private void scheduleReminder(String medicineName, int hour, int minute) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicine_name", medicineName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
