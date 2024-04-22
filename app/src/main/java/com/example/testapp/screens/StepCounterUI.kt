package com.example.testapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.viewmodel.StepCounterViewModel


@Composable
fun StepCounterUI(navController: NavController, paddingValues: PaddingValues, viewModel: StepCounterViewModel) {
    val steps = viewModel.steps.collectAsState().value
    val calories = viewModel.calories.collectAsState().value
    val goal = viewModel.moveGoal.collectAsState().value

    val progress = (calories.toFloat() / goal).coerceIn(0f, 1f)
    val goalText = "$steps / $goal"
    val distance = viewModel.calculateDistance(steps, 0.75)
    val isDarkModeEnabled by viewModel.isDarkModeEnabled.collectAsState()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(), // Fill the entire available space
        verticalArrangement = Arrangement.Center, // Center vertically in the space
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        CircularProgressBar(
            calories = calories.toInt(),
            goalCalories = goal,
            darkMode = isDarkModeEnabled,
            modifier = Modifier.size(275.dp)
        )
        Spacer(modifier = Modifier.height(16.dp)) // Create space between progress bar and text
        Text(text = "Steps: $steps")
        Text(text = "Calories: ${calories.toInt()} / $goal kcal")
        Text(text = "Distance: ${String.format("%.2f", distance)} km") // Display distance
        // Text(text = "Goal: $goalText")
        androidx.compose.material3.Button(onClick = { viewModel.resetSteps() }) {
            Text("Reset Steps")
        }
        androidx.compose.material3.Button(
            onClick = { navController.navigate("SleepTracker") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Sleep Tracker")
        }

        // Show congratulations message if goal reached
        if (progress >= 1.0f) {
            Text("Congratulations! Goal Achieved!", color = Color.Green)
        }
    }
}


@Composable
fun CircularProgressBar(
    calories: Int,
    goalCalories: Int,
    darkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = (calories.toFloat() / goalCalories).coerceIn(0f, 1f)
    val progressColor = if (darkMode) Color.Yellow else MaterialTheme.colors.primary

    CircularProgressIndicator(
        progress = progress,
        modifier = modifier,
        color = progressColor
    )
    Spacer(modifier = Modifier.height(8.dp)) // Spacing between progress bar and text
    Text(text = "${(progress * 100).toInt()}% of goal")
}
