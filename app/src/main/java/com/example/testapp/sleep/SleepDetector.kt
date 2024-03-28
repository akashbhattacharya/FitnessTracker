// SleepDetector.kt

package com.example.testapp.sleep

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SleepDetector(private val sensorManager: SensorManager) {

    private val accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val ambientLightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val _isSleeping = MutableStateFlow(false)
    val isSleeping: StateFlow<Boolean> = _isSleeping

    private var lastMovementTime: Long = 0
    private var lastDarknessTime: Long = 0

    init {
        registerSensorListeners()
    }

    private fun registerSensorListeners() {
        accelerometerSensor?.let { sensor ->
            sensorManager.registerListener(object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor == sensor) {
                        processAccelerometerData(event.values)
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Ignore for now
                }
            }, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        ambientLightSensor?.let { sensor ->
            sensorManager.registerListener(object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor == sensor) {
                        processAmbientLightData(event.values[0])
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Ignore for now
                }
            }, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun processAccelerometerData(values: FloatArray) {
        val acceleration = Math.sqrt(
            (values[0] * values[0] + values[1] * values[1] + values[2] * values[2]).toDouble()
        ).toFloat()

        if (acceleration < ACCELEROMETER_THRESHOLD) {
            // User is not moving
            lastMovementTime = System.currentTimeMillis()
        }
        checkSleepStatus()
    }

    private fun processAmbientLightData(lightIntensity: Float) {
        if (lightIntensity < AMBIENT_LIGHT_THRESHOLD) {
            // Environment is dark
            lastDarknessTime = System.currentTimeMillis()
        }
        checkSleepStatus()
    }

    private fun checkSleepStatus() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastMovementTime > INACTIVITY_THRESHOLD &&
            currentTime - lastDarknessTime > DARKNESS_THRESHOLD
        ) {
            _isSleeping.value = true
        } else {
            _isSleeping.value = false
        }
    }

    companion object {
        private const val ACCELEROMETER_THRESHOLD = 0.5f // Adjust as needed
        private const val AMBIENT_LIGHT_THRESHOLD = 10f // Adjust as needed
        private const val INACTIVITY_THRESHOLD = 40 * 60 * 1000 // 40 minutes in milliseconds
        private const val DARKNESS_THRESHOLD = 15 * 60 * 1000 // 15 minutes in milliseconds
    }
}
