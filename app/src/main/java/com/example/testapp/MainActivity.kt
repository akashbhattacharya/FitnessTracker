package com.example.testapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.testapp.ui.theme.TestAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            TestAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    StepCounterApp()
                }
            }
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
            composable("healthDetails") { HealthDetailsScreen(navController) }
            composable("changeMoveGoal") { ChangeMoveGoalScreen(navController) }
            composable("unitsOfMeasure") { UnitsOfMeasureScreen(navController) }
            composable("notifications") { NotificationsScreen(navController) }
        }
    }
}


@Composable
fun UserSettingsScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("User Settings", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("healthDetails") }) {
            Text("Health Details")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("changeMoveGoal") }) {
            Text("Change Move Goal")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("unitsOfMeasure") }) {
            Text("Units of Measure")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("notifications") }) {
            Text("Notifications")
        }
    }
}




@Composable
fun HealthDetailsScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Health Details", style = MaterialTheme.typography.h5)
        // Implement UI for health details here
    }
}

@Composable
fun UnitsOfMeasureScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Units of Measure", style = MaterialTheme.typography.h5)
        // Implement UI for units of measure here
    }
}

@Composable
fun NotificationsScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Notifications", style = MaterialTheme.typography.h5)
        // Implement UI for notifications settings here
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

@Composable
fun ChangeMoveGoalScreen(navController: NavController) {
    val viewModel: StepCounterViewModel = viewModel()
    val currentGoal = remember { mutableStateOf(viewModel.moveGoal.value) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Change Move Goal", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        // Display the current move goal
        Text("Move Goal: ${currentGoal.value}")

        // Plus and minus buttons to adjust the move goal
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { increaseGoal(currentGoal) }) {
                Text("+10")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { decreaseGoal(currentGoal) }) {
                Text("-10")
            }
        }

        // Save button to apply the changes
        Button(
            onClick = {
                viewModel.setMoveGoal(currentGoal.value) // Set the new move goal in the ViewModel
                navController.popBackStack() // Navigate back
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp)
        ) {
            Text("Save")
        }
    }
}

private fun increaseGoal(currentGoal: MutableState<Int>) {
    currentGoal.value += 10
}

private fun decreaseGoal(currentGoal: MutableState<Int>) {
    currentGoal.value -= 10
}

