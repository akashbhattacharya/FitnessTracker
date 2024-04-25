package com.example.testapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.viewmodel.StepCounterViewModel


@Composable
fun StepCounterUI(navController: NavController, paddingValues: PaddingValues, viewModel: StepCounterViewModel) {
    val steps = viewModel.steps.collectAsState().value
    val calories = viewModel.totalCalories.collectAsState().value
    val goal = viewModel.moveGoal.collectAsState().value
    val burnedCalories = viewModel.calories.collectAsState().value
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
        Text(text = "Calories: ${calories-burnedCalories} / $goal kcal")
        Text(text = "Distance: ${String.format("%.2f", distance)} km") // Display distance
        // Text(text = "Goal: $goalText")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { viewModel.resetSteps() },
                colors = ButtonDefaults.buttonColors( Color(0xfff9aa33)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Reset Steps", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("foodList") },
                colors = ButtonDefaults.buttonColors(Color(0xfff9aa33)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Today's Food List", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.resetMeals() },
                colors = ButtonDefaults.buttonColors(Color(0xfff9aa33)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Reset Food List", fontSize = 20.sp)
            }
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
