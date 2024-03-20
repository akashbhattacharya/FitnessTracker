package com.example.testapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class StepCounterViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private var sensorManager: SensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastAccelerationMagnitude = 0.0
    private var stepThreshold = 12.0 // Example threshold, may need adjustment
    private var lastStepTime = System.currentTimeMillis()

    val steps = MutableStateFlow(0)
    val calories = MutableStateFlow(0.0)

    init {
        val sensorToRegister = stepSensor ?: accelerometerSensor
        sensorManager.registerListener(this, sensorToRegister, SensorManager.SENSOR_DELAY_UI)
        createNotificationChannel()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val stepCount = event.values[0].toInt()
                viewModelScope.launch {
                    steps.value = stepCount
                    calories.value = calculateCalories(stepCount)
                    sendStepMilestoneNotification(stepCount)
                }
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val accelerationMagnitude = sqrt(x * x + y * y + z * z)
                detectStep(accelerationMagnitude)
            }
        }
    }

    private fun detectStep(accelerationMagnitude: Float) {
        val currentTime = System.currentTimeMillis()
        if (accelerationMagnitude > stepThreshold && currentTime - lastStepTime > 500) { // Debounce time of 500ms
            viewModelScope.launch {
                steps.value = steps.value + 1
                calories.value = calculateCalories(steps.value)
                sendStepMilestoneNotification(steps.value)
            }
            lastStepTime = currentTime
        }
        lastAccelerationMagnitude = accelerationMagnitude.toDouble()
    }

    private fun sendStepMilestoneNotification(stepCount: Int) {
        if (stepCount % 1000 == 0) {
            val notificationBuilder = NotificationCompat.Builder(getApplication(), "stepCounterChannel")
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle("Congratulations!")
                .setContentText("You've reached $stepCount steps!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(stepCount, notificationBuilder.build())
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Step Counter Channel"
            val descriptionText = "Notifications for step milestones"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("stepCounterChannel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun resetSteps() {
        steps.value = 0
        calories.value = 0.0
    }

    private fun calculateCalories(steps: Int): Double {
        return steps * 0.04 // Simplified calorie calculation. Adjust based on actual use case.
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
