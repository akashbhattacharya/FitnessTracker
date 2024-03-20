package com.example.testapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            StepCounterApp()
        }
    }

    private fun createNotificationChannel() {
        val name = "Step Counter Notifications"
        val descriptionText = "Notifications for step milestones"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("stepCounterChannel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}


@Composable
fun StepCounterApp() {
    val viewModel: StepCounterViewModel = viewModel()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Step Counter") }) }
    ) { paddingValues ->
        StepCounterUI(viewModel = viewModel, paddingValues = paddingValues)
    }
}

@Composable
fun StepCounterUI(viewModel: StepCounterViewModel, paddingValues: PaddingValues) {
    val steps = viewModel.steps.collectAsState().value
    val calories = viewModel.calories.collectAsState().value
    val goal = 1000 // Your step goal

    Column(modifier = Modifier.padding(paddingValues)) {
        Text(text = "Steps: $steps")
        Text(text = "Calories: ${calories.toInt()} kcal")
        Button(onClick = { viewModel.resetSteps() }) {
            Text("Reset Steps")
        }
        CircularProgressBar(stepsCount = steps, goalCount = goal)
    }
}

@Composable
fun CircularProgressBar(stepsCount: Int, goalCount: Int, modifier: Modifier = Modifier) {
    val progress = (stepsCount.toFloat() / goalCount).coerceIn(0f, 1f)
    CircularProgressIndicator(progress = progress, modifier = modifier)
    Text(text = "${(progress * 100).toInt()}% of goal")
}
