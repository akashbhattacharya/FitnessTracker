package com.example.testapp.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.CalorieApplication
import com.example.testapp.data.HealthDetails
import com.example.testapp.data.UHDRepository
import com.example.testapp.data.UserHealthDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
class StepCounterViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val appContainer = (application as CalorieApplication).container
    private val uhdRepository = appContainer.uhdRepository
    private var sensorManager: SensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private var accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastAccelerationMagnitude = 0.0
    private var stepThreshold = 12.0 // Example threshold, may need adjustment
    private var lastStepTime = System.currentTimeMillis()

    private val TIME_THRESHOLD_IN_MILLIS = 2 * 60 * 1000 // Change to 2 minutes
    private val INACTIVITY_CHECK_INTERVAL = 10 * 60 * 1000 // Check for inactivity every 10 minutes
    private val INACTIVITY_NOTIFICATION_ID = 12345 // Unique notification ID

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private val _calories = MutableStateFlow(0.0)
    val calories: StateFlow<Double> = _calories

    private val _moveGoal = MutableStateFlow(1000)
    val moveGoal: StateFlow<Int> = _moveGoal

    private val _motivationalMessages = listOf(
        "Keep going, you're doing great!",
        "One step at a time, you'll get there!",
        "You're making progress, don't give up!",
        "Believe in yourself, you can do it!",
        "Every step counts towards your goal!"
    )

    private val _isDarkModeEnabled = MutableStateFlow(false)
    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled

    private var healthDetails: HealthDetails? = null

    private val MALE_BMR_CONSTANT = 66.5 // Constant for male BMR calculation
    private val FEMALE_BMR_CONSTANT = 655.1 // Constant for female BMR calculation
    private val HEIGHT_CONSTANT = 5.003 // Constant for height calculation in BMR formula
    private val WEIGHT_CONSTANT = 13.75 // Constant for weight calculation in BMR formula
    private val AGE_CONSTANT = 6.755 // Constant for age calculation in BMR formula

    init {
        val sensorToRegister = stepSensor ?: accelerometerSensor
        sensorManager.registerListener(this, sensorToRegister, SensorManager.SENSOR_DELAY_UI)
        createNotificationChannel()

        // Start checking for inactivity periodically
        startInactivityCheck()
        loadDarkModePreference()

        viewModelScope.launch {
            uhdRepository.getDetailStream().collect { userHealthDetails ->
                if(userHealthDetails != null) {
                    // Update steps and calories with initial values from the database
                    healthDetails = HealthDetails(
                        userHealthDetails.age,
                        userHealthDetails.weight,
                        userHealthDetails.height,
                        userHealthDetails.sex
                    )
                    _moveGoal.value = calculateBMR(healthDetails!!).toInt()
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val stepCount = event.values[0].toInt()
                viewModelScope.launch {
                    _steps.value = stepCount
                    _calories.value = calculateCalories(stepCount)
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

    private fun startInactivityCheck() {
        viewModelScope.launch {
            while (true) {
                delay(INACTIVITY_CHECK_INTERVAL.toLong()) // Delay for a specific interval
                checkInactivity()
            }
        }
    }

    private fun checkInactivity() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastStepTime > TIME_THRESHOLD_IN_MILLIS) {
            sendInactivityNotification()
        }
    }

    private fun sendInactivityNotification() {
        val notificationBuilder = NotificationCompat.Builder(getApplication(), "stepCounterChannel")
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("Get Moving!")
            .setContentText("It seems like you haven't walked for a while. Take a break and walk around!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(INACTIVITY_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun detectStep(accelerationMagnitude: Float) {
        val currentTime = System.currentTimeMillis()
        if (accelerationMagnitude > stepThreshold && currentTime - lastStepTime > 500) { // Debounce time of 500ms
            viewModelScope.launch {
                _steps.value += 1
                _calories.value = calculateCalories(_steps.value)
                sendStepMilestoneNotification(_steps.value)
            }
            lastStepTime = currentTime
        }
        lastAccelerationMagnitude = accelerationMagnitude.toDouble()
    }

    // Function to update steps and calories
    fun updateStepsAndCalories(newSteps: Int, newCalories: Double) {
        viewModelScope.launch {
            _steps.value = newSteps
            _calories.value = newCalories
            checkStepMilestone()
            checkCalorieMilestone()
        }
    }

    // Function to check if the user has reached a step milestone
    private fun checkStepMilestone() {
        val goal = _moveGoal.value
        if (_steps.value % goal == 0 && _steps.value != 0) {
            sendStepMilestoneNotification(_steps.value)
        }
    }

    // Function to check if the user has reached a calorie milestone (half of the goal)
    private fun checkCalorieMilestone() {
        val goal = _moveGoal.value
        val halfGoal = goal / 2
        if (_calories.value >= halfGoal && _calories.value < goal) {
            sendCalorieMilestoneNotification()
        }
    }

    // Function to send a notification for reaching a step milestone
    // Function to send a notification for reaching a step milestone
    private fun sendStepMilestoneNotification(value: Int) {
        val notificationBuilder = NotificationCompat.Builder(getApplication(), "stepCounterChannel")
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("Almost There!")
            .setContentText("You've reached 80% of your step goal. Keep going!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(STEP_MILESTONE_NOTIFICATION_ID, notificationBuilder.build())
    }

    // Function to send a notification for reaching a calorie milestone
    private fun sendCalorieMilestoneNotification() {
        val message = _motivationalMessages.random() // Get a random motivational message
        val notificationBuilder = NotificationCompat.Builder(getApplication(), CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("Keep Going!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(CALORIE_MILESTONE_NOTIFICATION_ID, notificationBuilder.build())
    }

    // Function to create notification channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Step Counter Channel"
            val descriptionText = "Notifications for step and calorie milestones"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "stepCounterChannel"
        private const val STEP_MILESTONE_NOTIFICATION_ID = 1
        private const val CALORIE_MILESTONE_NOTIFICATION_ID = 2
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    suspend fun setHealthDetails(height: Double, weight: Double, age: Int, sex: String) {
        healthDetails = HealthDetails(age, weight, height, sex)
        uhdRepository.insertUHD(UserHealthDetails(1,age, weight, height, sex))

    // Calculate BMR based on user details
        val bmr = calculateBMR(healthDetails!!)
        // Set default move goal based on BMR
        setDefaultMoveGoal(bmr)
    }

    private suspend fun calculateBMR(userHealthDetails: HealthDetails): Double {
         return if (userHealthDetails.sex.equals("male", ignoreCase = true)) {
             MALE_BMR_CONSTANT + (WEIGHT_CONSTANT * userHealthDetails.weight!!) + (HEIGHT_CONSTANT * userHealthDetails.height!!) - (AGE_CONSTANT * userHealthDetails.age!!)
        } else {
            FEMALE_BMR_CONSTANT + (WEIGHT_CONSTANT * userHealthDetails.weight!!) + (HEIGHT_CONSTANT * userHealthDetails.height!!) - (AGE_CONSTANT * userHealthDetails.age!!)
        }
    }

    private fun setDefaultMoveGoal(bmr: Double) {
        // Default move goal can be set based on a percentage of BMR
        // You can adjust the percentage based on your application requirements
        val percentageOfBMR = 0.2 // For example, set move goal to 20% of BMR
        val defaultMoveGoal = (percentageOfBMR * bmr).toInt()
        _moveGoal.value = defaultMoveGoal
    }

    fun calculateDistance(steps: Int, strideLength: Double): Double {
        return steps * strideLength / 1000 // Convert meters to kilometers
    }

    fun resetSteps() {
        _steps.value = 0
        _calories.value = 0.0
    }

    fun increaseMoveGoal() {
        _moveGoal.value += 10
    }

    fun decreaseMoveGoal() {
        if (_moveGoal.value > 10) { // Prevent negative goals
            _moveGoal.value -= 10
        }
    }

    private fun calculateCalories(steps: Int): Double {
        return steps * 0.04 // Simplified calorie calculation. Adjust based on actual use case.
    }

    fun toggleDarkMode() {
        _isDarkModeEnabled.value = !_isDarkModeEnabled.value
        saveDarkModePreference()
    }

    private fun loadDarkModePreference() {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
        _isDarkModeEnabled.value = sharedPrefs.getBoolean("darkMode", false)
    }

    private fun saveDarkModePreference() {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean("darkMode", _isDarkModeEnabled.value)
            apply()
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
