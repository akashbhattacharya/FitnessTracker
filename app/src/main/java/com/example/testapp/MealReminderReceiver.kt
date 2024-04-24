package com.example.testapp;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.testapp.R

class MealReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val mealName = intent?.getStringExtra("mealName") ?: "Meal"
        val builder = NotificationCompat.Builder(context!!, "meal_reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$mealName Time")
            .setContentText("It's time to have your $mealName!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(mealName.hashCode(), builder.build())
        }
    }
}
