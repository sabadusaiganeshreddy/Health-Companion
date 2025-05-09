package com.example.healthcompanion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String medName = intent.getStringExtra("medicine_name");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "med_channel")
                .setSmallIcon(R.drawable.ic_medicine)
                .setContentTitle("Medicine Reminder")
                .setContentText("Time to take: " + medName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        try {
            manager.notify(101, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(context, "Medicine Reminder!", Toast.LENGTH_SHORT).show();

        }


    }
}
