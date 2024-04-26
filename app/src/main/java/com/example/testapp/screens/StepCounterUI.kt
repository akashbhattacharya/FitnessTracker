package com.example.testapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testapp.viewmodel.StepCounterViewModel


@Composable
fun StepCounterUI(navController: NavController, paddingValues: PaddingValues, viewModel: StepCounterViewModel) {
    val steps = viewModel.steps.collectAsState().value
    val calories = viewModel.totalCalories.collectAsState().value
    val goal = viewModel.moveGoal.collectAsState().value
    val burnedCalories = viewModel.calories.collectAsState().value
    val progress = (calories.toFloat() / goal).coerceIn(0f, 1f)
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
            calories = calories,
            goalCalories = goal,
            darkMode = isDarkModeEnabled,
            modifier = Modifier.size(275.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        StatsRow(steps = steps, burnedCalories = calories - burnedCalories, goalCalories = goal, distance = distance)
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
                shape = RoundedCornerShape(24)
            ) {
                Text("Reset Steps", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("foodList") },
                colors = ButtonDefaults.buttonColors(Color(0xfff9aa33)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(24)
            ) {
                Text("Today's Food List", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.resetMeals() },
                colors = ButtonDefaults.buttonColors(Color(0xfff9aa33)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(24)
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
    val progressColor = if (darkMode) Color.White else Color(0xfff9aa33)
    val textColor = if (darkMode) Color.LightGray else Color.DarkGray

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
    ) {
        CircularProgressIndicator(
            progress = progress,
            color = progressColor,
            strokeWidth = 20.dp,
            modifier = Modifier.size(240.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "of Calorie Goal",
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }


    }
}
@Composable
fun StatsRow(steps: Int, burnedCalories: Double, goalCalories: Int, distance: Double) {
    Row(modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .fillMaxWidth()) {
        Box(
            modifier = Modifier
                .height(120.dp)
                .weight(1f)
                .padding(end = 4.dp)
                .background(Color(0xff4a6572), shape = RoundedCornerShape(10.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            StepsDisplay(steps)
        }

        Box(
            modifier = Modifier
                .height(120.dp)
                .weight(1f)
                .padding(horizontal = 4.dp) // Add end padding to separate boxes
                .background(Color(0xff4a6572), shape = RoundedCornerShape(10.dp)) // Rounded corners
                .padding(8.dp), // Padding inside the box
            contentAlignment = Alignment.Center
        ) {
            CaloriesDisplay(burnedCalories, goalCalories)
        }

        Box(
            modifier = Modifier
                .height(120.dp)
                .weight(1f)
                .padding(start = 4.dp) // Add end padding to separate boxes
                .background(Color(0xff4a6572), shape = RoundedCornerShape(10.dp)) // Rounded corners
                .padding(8.dp), // Padding inside the box
            contentAlignment = Alignment.Center
        ) {
            DistanceDisplay(distance)
        }
    }
}
@Composable
fun StepsDisplay(steps: Int) {
    val annotatedText = buildAnnotatedString {
        append("Steps:\n ")
        withStyle(style = SpanStyle(color = Color(0xfff9aa33))) {
            append("$steps")
        }
    }

    Text(
        text = annotatedText,
        fontWeight = FontWeight(600),
        fontSize = 22.sp,
        textAlign = TextAlign.Center,
        color = Color.White,  // Default color for the rest of the text
        modifier = Modifier.fillMaxWidth()
    )
}
@Composable
fun CaloriesDisplay(calories: Double, goalCalories: Int) {
    val progress = (calories.toFloat() / goalCalories).coerceIn(0f, 1f)

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.size(100.dp),
            color = Color(0xfff9aa33),
            strokeWidth = 6.dp
        )
        val annotatedText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 20.sp, color = Color(0xfff9aa33))) {
                append("$calories")
            }
            append("\n") // New line
            withStyle(style = SpanStyle(color = Color.White)) {
                append("/ $goalCalories kcal")
            }
        }
        Text(
            text = annotatedText,

            fontWeight = FontWeight(600),
            fontSize = 12.sp, // This will be overridden by the larger font size in the annotated part
            textAlign = TextAlign.Center, // Centers the text horizontally inside the Box
            modifier = Modifier.align(Alignment.Center) // Ensures the text is centered inside the Box
        )
    }
}

@Composable
fun DistanceDisplay(distance: Double) {
    val annotatedText = buildAnnotatedString {
        append("Distance:\n ")
        withStyle(style = SpanStyle(color = Color(0xfff9aa33))) {
            append(String.format("%.2f km", distance))
        }
    }

    Text(
        text = annotatedText,
        fontSize = 22.sp,
        fontWeight = FontWeight(600),
        textAlign = TextAlign.Center,
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    )
}
