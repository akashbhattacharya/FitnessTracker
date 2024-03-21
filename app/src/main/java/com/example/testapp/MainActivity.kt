package com.example.testapp

// Import statements for necessary components (Add if not already present in your file)
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.Text
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import android.os.Bundle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

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
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step Counter") },
                actions = {
                    // User icon button added here
                    IconButton(onClick = { navController.navigate("userSettings") }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "User Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = "main") {
            composable("main") { StepCounterUI(navController, paddingValues) }
            composable("weeklySteps") { WeeklyStepsScreen() }
            // Add the composable for user settings screen
            composable("userSettings") { UserSettingsScreen(navController) }
        }
    }
}

@Composable
fun UserSettingsScreen(navController: NavController) {
    // Placeholder for User Settings UI
    Column(modifier = Modifier.padding(16.dp)) {
        Text("User Settings", style = MaterialTheme.typography.h5)
        // Implement the UI for updating user details
    }
}

@Composable
fun StepCounterUI(navController: NavController, paddingValues: PaddingValues) {
    val viewModel: StepCounterViewModel = viewModel()
    val steps = viewModel.steps.collectAsState().value
    val calories = viewModel.calories.collectAsState().value
    val goal = 100 // Your step goal

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(), // Fill the entire available space
        verticalArrangement = Arrangement.Center, // Center vertically in the space
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        CircularProgressBar(stepsCount = steps, goalCount = goal, modifier = Modifier.size(275.dp))
        Spacer(modifier = Modifier.height(16.dp)) // Create space between progress bar and text
        Text(text = "Steps: $steps")
        Text(text = "Calories: ${calories.toInt()} kcal")
        Button(onClick = { viewModel.resetSteps() }) {
            Text("Reset Steps")
        }
        Button(
            onClick = { navController.navigate("weeklySteps") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("View Weekly Steps")
        }
    }
}

@Composable
fun CircularProgressBar(stepsCount: Int, goalCount: Int, modifier: Modifier = Modifier) {
    val progress = (stepsCount.toFloat() / goalCount).coerceIn(0f, 1f)
    CircularProgressIndicator(progress = progress, modifier = modifier)
    Spacer(modifier = Modifier.height(8.dp)) // Spacing between progress bar and text
    Text(text = "${(progress * 100).toInt()}% of goal")
}

// Placeholder for the bar chart screen
@Composable
fun WeeklyStepsScreen() {
    val data = listOf(100, 200, 300, 400, 500) // Sample data for the bars
    val maxBarHeight = 200.dp // Max height for the bars

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxBarHeight)
        ) {
            val barWidth = size.width / data.size
            val maxDataValue = data.maxOrNull() ?: 0
            val scaleY = maxBarHeight.toPx() / maxDataValue

            data.forEachIndexed { index, value ->
                val barHeight = value * scaleY
                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(index * barWidth, size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
    }
}


