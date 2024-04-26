package com.example.testapp.viewmodel



import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.testapp.CalorieApplication
import com.example.testapp.MealReminderReceiver
import com.example.testapp.R
import com.example.testapp.data.FoodListDetails
import com.example.testapp.data.HealthDetails
import com.example.testapp.data.StepDetails
import com.example.testapp.data.UserHealthDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

data class FoodItem(val name: String, val calories: Int, val timestamp: String)

data class Achievement(
    val milestone: Int,
    val isAchieved: Boolean,
    var notified: Boolean = false // Add this field to track notification state
)

data class UserAchievement(
    val userId: String,
    val achievements: List<Achievement>
)

class StepCounterViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val appContainer = (application as CalorieApplication).container
    private val uhdRepository = appContainer.uhdRepository
    private var sensorManager: SensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastAccelerationMagnitude = 0.0
    private var stepThreshold = 12.0
    private var lastStepTime = System.currentTimeMillis()

    private val TIME_THRESHOLD_IN_MILLIS = 2 * 60 * 1000
    private val INACTIVITY_CHECK_INTERVAL = 10 * 60 * 1000
    private val INACTIVITY_NOTIFICATION_ID = 12345

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _total_steps = MutableStateFlow(0)
    val total_steps: StateFlow<Int> = _total_steps.asStateFlow()

    private val _calories = MutableStateFlow(0.0)
    val calories: StateFlow<Double> = _calories

    private val _moveGoal = MutableStateFlow(1000)
    val moveGoal: StateFlow<Int> = _moveGoal

    private val _foodList = MutableStateFlow<List<FoodItem>>(emptyList())
    val foodList: StateFlow<List<FoodItem>> = _foodList

    private val _motivationalMessages = listOf(
        "Keep going, you're doing great!",
        "One step at a time, you'll get there!",
        "You're making progress, don't give up!",
        "Believe in yourself, you can do it!",
        "Every step counts towards your goal!",
        "Remember, walking helps you burn more calories!"
    )

    private val _isDarkModeEnabled = MutableStateFlow(false)
    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled

    private var healthDetails: HealthDetails? = null

    private val MALE_BMR_CONSTANT = 66.5
    private val FEMALE_BMR_CONSTANT = 655.1
    private val HEIGHT_CONSTANT = 5.003
    private val WEIGHT_CONSTANT = 13.75
    private val AGE_CONSTANT = 6.755

    private var _achievements = MutableStateFlow(UserAchievement("user123", listOf(
        Achievement(5000, false),
        Achievement(10000, false),
        Achievement(25000, false),
        Achievement(50000, false),
        Achievement(100000, false)
    )))
    val achievements: StateFlow<UserAchievement> = _achievements.asStateFlow()

    init {
        val sensorToRegister = stepSensor ?: accelerometerSensor
        sensorManager.registerListener(this, sensorToRegister, SensorManager.SENSOR_DELAY_UI)
        createNotificationChannel()
        startInactivityCheck()
        loadDarkModePreference()

        observeStepsAndCheckAchievements()
        viewModelScope.launch {
            uhdRepository.getDetailStream().collect { userHealthDetails ->
                if (userHealthDetails != null) {
                    healthDetails = HealthDetails(
                        userHealthDetails.age,
                        userHealthDetails.weight,
                        userHealthDetails.height,
                        userHealthDetails.sex,
                        userHealthDetails.bmr
                    )
                    _moveGoal.value = calculateBMR(healthDetails!!).toInt()
                }
            }
        }
            viewModelScope.launch{
            uhdRepository.getAllFoodStream().collect{foodListDetails ->
                if(foodListDetails != null){
                    for(foodEntry in foodListDetails){
                        val existingItem = _foodList.value.find { it.timestamp == foodEntry.timestamp }
                        if (existingItem == null) {
                            val foodItem = FoodItem(foodEntry.name, foodEntry.calories, foodEntry.timestamp)
                            _foodList.value += foodItem
                        }
                    }
                }
            }
        }
            viewModelScope.launch {
                uhdRepository.getStepStream().collect{stepDetails ->
                    if(stepDetails!=null){
                        _steps.value = stepDetails.tempSteps
                        _total_steps.value = stepDetails.totalSteps
                        _calories.value = calculateCalories(stepDetails.tempSteps)

                    }
                    else{
                        uhdRepository.insertSteps(StepDetails(1,0,0))
                    }
            }
        }
    }
    suspend fun addFoodItem(name: String, calories: Int) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val newItem = FoodItem(name, calories, timestamp)
        _foodList.value += newItem
        val food = FoodListDetails(name = name, calories = calories, timestamp = timestamp)
        uhdRepository.insertFood(food)
    }
    val totalCalories: StateFlow<Int> = _foodList.map { list ->
        list.sumOf { it.calories }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    private fun observeStepsAndCheckAchievements() {
        viewModelScope.launch {
            total_steps.collectLatest { stepCount ->
                updateAchievements(stepCount)
            }
        }
    }

    private fun updateAchievements(newSteps: Int) {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("AchievementsPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        val currentAchievements = _achievements.value.achievements.map { achievement ->
            if (!achievement.isAchieved && newSteps >= achievement.milestone && !achievement.notified) {
                showNotification(achievement.milestone)
                achievement.copy(isAchieved = true, notified = true).also {
                    editor.putBoolean("Notified_${achievement.milestone}", true)
                }
            } else {
                achievement
            }
        }
        _achievements.value = UserAchievement(_achievements.value.userId, currentAchievements)
        editor.apply()
    }


    private fun showNotification(milestone: Int) {
        // Obtain the application context from the AndroidViewModel
        val context = getApplication<Application>().applicationContext

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(getApplication(), "achievement_channel").apply {
            setSmallIcon(R.drawable.applogo_black) // Set the icon of the notification
            setContentTitle("Achievement Unlocked")
            setContentText("Congratulations! You've reached $milestone steps!")
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(true)
        }

        // Notification ID is unique to each notification you create.
        notificationManager.notify(milestone, builder.build())
    }
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val stepCount = it.values[0].toInt()
                viewModelScope.launch {
                    _steps.value = stepCount
                    _calories.value = calculateCalories(stepCount)
                    sendStepMilestoneNotification(stepCount)
                }
            } else if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val accelerationMagnitude = sqrt(x * x + y * y + z * z)
                detectStep(accelerationMagnitude)
            }
            else {}
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
            var details : StepDetails
            viewModelScope.launch {
                var stepDetails = uhdRepository.getStepStream().first()
                details = StepDetails(1, stepDetails.tempSteps + 1, stepDetails.totalSteps + 1)
                uhdRepository.insertSteps(details)
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
        if (totalCalories.value >= halfGoal && totalCalories.value < goal) {
            sendCalorieMilestoneNotification()
        }
    }

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

        // Calculate BMR based on user details
        val bmr = calculateBMR(healthDetails!!)
        uhdRepository.insertUHD(UserHealthDetails(1,age, weight, height, sex,bmr))

        // Set default move goal based on BMR
        setDefaultMoveGoal(bmr)
    }

    private suspend fun calculateBMR(userHealthDetails: HealthDetails): Double {
        return if (userHealthDetails.sex.equals("male", ignoreCase = true)) {
            1.2*(MALE_BMR_CONSTANT + (WEIGHT_CONSTANT * userHealthDetails.weight!!) + (HEIGHT_CONSTANT * userHealthDetails.height!!) - (AGE_CONSTANT * userHealthDetails.age!!))
        } else {
           1.2*(FEMALE_BMR_CONSTANT + (WEIGHT_CONSTANT * userHealthDetails.weight!!) + (HEIGHT_CONSTANT * userHealthDetails.height!!) - (AGE_CONSTANT * userHealthDetails.age!!))
        }
    }

    private fun setDefaultMoveGoal(bmr: Double) {
        _moveGoal.value = bmr.toInt()
    }

    fun calculateDistance(steps: Int, strideLength: Double): Double {
        return steps * strideLength / 1000 // Convert meters to kilometers
    }

    fun resetSteps() {
        _steps.value = 0
        _calories.value = 0.0
        viewModelScope.launch {
           var stepDetails = uhdRepository.getStepStream().first()
            var details = StepDetails(1, 0, stepDetails.totalSteps)
            uhdRepository.insertSteps(details)
        }
    }

    fun resetMeals(){
        _foodList.value = emptyList()
        viewModelScope.launch {
            uhdRepository.deleteAllMeals()
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

    val breakfastTime = MutableLiveData<LocalTime>()
    val lunchTime = MutableLiveData<LocalTime>()
    val snackTime = MutableLiveData<LocalTime>()
    val dinnerTime = MutableLiveData<LocalTime>()

    fun saveMealTimes(context: Context) {
        // Example logging action
        breakfastTime.value?.let {
            scheduleMealTimeReminder(context, "Breakfast", it)
            Log.d("MealTimeViewModel", "Breakfast time set and alarm scheduled for: ${it.format(DateTimeFormatter.ISO_LOCAL_TIME)}")
        }

        lunchTime.value?.let {
            scheduleMealTimeReminder(context, "Lunch", it)
            Log.d("MealTimeViewModel", "Lunch time set and alarm scheduled for: ${it.format(DateTimeFormatter.ISO_LOCAL_TIME)}")
        }

        snackTime.value?.let {
            scheduleMealTimeReminder(context, "Snack", it)
            Log.d("MealTimeViewModel", "Snack time set and alarm scheduled for: ${it.format(DateTimeFormatter.ISO_LOCAL_TIME)}")
        }

        dinnerTime.value?.let {
            scheduleMealTimeReminder(context, "Dinner", it)
            Log.d("MealTimeViewModel", "Dinner time set and alarm scheduled for: ${it.format(DateTimeFormatter.ISO_LOCAL_TIME)}")
        }
    }

    private fun scheduleMealTimeReminder(context: Context, mealName: String, time: LocalTime) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MealReminderReceiver::class.java).apply {
            putExtra("mealName", mealName)
        }
        val requestCode = mealName.hashCode()
        val alarmIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmMgr.canScheduleExactAlarms()) {
                val permissionIntent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(permissionIntent)
                return
            }
        }


        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.timeInMillis, alarmIntent)
        } else {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmTime.timeInMillis, alarmIntent)
        }
    }

}

