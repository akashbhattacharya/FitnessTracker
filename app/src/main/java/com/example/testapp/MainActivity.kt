package com.example.testapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
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
                    val viewModel: StepCounterViewModel = viewModel()
                    StepCounterApp(viewModel)
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
fun StepCounterApp(viewModel: StepCounterViewModel) {
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
            composable("main") { StepCounterUI(navController, paddingValues, viewModel) }
            composable("weeklySteps") { WeeklyStepsScreen() }
            // Add the composable for user settings screen
            composable("userSettings") { UserSettingsScreen(navController) }
            composable("healthDetails") { HealthDetailsScreen(navController, viewModel) }
            composable("changeMoveGoal") { ChangeMoveGoalScreen(navController, viewModel) } // Pass viewModel here
            composable("unitsOfMeasure") { UnitsOfMeasureScreen(navController) }
            composable("notifications") { NotificationsScreen(navController) }
        }
    }
}

@Composable
fun HealthDetailsScreen(navController: NavController, viewModel: StepCounterViewModel) {
    var selectedAge by remember { mutableStateOf<String?>(null) }
    var isAgeDropdownExpanded by remember { mutableStateOf(false) } // Age dropdown expanded state
    var selectedHeight by remember { mutableStateOf<String?>(null) }
    var isHeightDropdownExpanded by remember { mutableStateOf(false) } // Height dropdown expanded state
    var selectedWeight by remember { mutableStateOf<String?>(null) }
    var isWeightDropdownExpanded by remember { mutableStateOf(false) } // Weight dropdown expanded state
    var selectedSex by remember { mutableStateOf<String?>(null) }
    var isSexDropdownExpanded by remember { mutableStateOf(false) } // Sex dropdown expanded state

    val ageOptions = (18..100).map { it.toString() }
    val heightOptions = (30..275).map { it.toString() }
    val weightOptions = (1..454).map { it.toString() }
    val sexOptions = listOf("Male", "Female")

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Health Details", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        // Age selection
        Box(
            modifier = Modifier.fillMaxWidth().clickable { isAgeDropdownExpanded = true }
        ) {
            Text(
                text = selectedAge ?: "Select Age",
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
            )
            DropdownMenu(
                expanded = isAgeDropdownExpanded,
                onDismissRequest = { isAgeDropdownExpanded = false }
            ) {
                ageOptions.forEach { age ->
                    DropdownMenuItem(onClick = {
                        selectedAge = age
                        isAgeDropdownExpanded = false
                    }) {
                        Text(age)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Height selection
        Box(
            modifier = Modifier.fillMaxWidth().clickable { isHeightDropdownExpanded = true }
        ) {
            Text(
                text = selectedHeight ?: "Select Height (cm)",
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
            )
            DropdownMenu(
                expanded = isHeightDropdownExpanded,
                onDismissRequest = { isHeightDropdownExpanded = false }
            ) {
                heightOptions.forEach { height ->
                    DropdownMenuItem(onClick = {
                        selectedHeight = height
                        isHeightDropdownExpanded = false
                    }) {
                        Text(height)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weight selection
        Box(
            modifier = Modifier.fillMaxWidth().clickable { isWeightDropdownExpanded = true }
        ) {
            Text(
                text = selectedWeight ?: "Select Weight (kg)",
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
            )
            DropdownMenu(
                expanded = isWeightDropdownExpanded,
                onDismissRequest = { isWeightDropdownExpanded = false }
            ) {
                weightOptions.forEach { weight ->
                    DropdownMenuItem(onClick = {
                        selectedWeight = weight
                        isWeightDropdownExpanded = false
                    }) {
                        Text(weight)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sex selection
        Box(
            modifier = Modifier.fillMaxWidth().clickable { isSexDropdownExpanded = true }
        ) {
            Text(
                text = selectedSex ?: "Select Sex",
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
            )
            DropdownMenu(
                expanded = isSexDropdownExpanded,
                onDismissRequest = { isSexDropdownExpanded = false }
            ) {
                sexOptions.forEach { sex ->
                    DropdownMenuItem(onClick = {
                        selectedSex = sex
                        isSexDropdownExpanded = false
                    }) {
                        Text(sex)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val age = selectedAge?.toIntOrNull() ?: 0
            val height = selectedHeight?.toDoubleOrNull() ?: 0.0
            val weight = selectedWeight?.toDoubleOrNull() ?: 0.0
            viewModel.setHealthDetails(
                height,
                weight,
                age,
                selectedSex ?: ""
            )
            navController.navigateUp()
        }) {
            Text("Save")
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
fun StepCounterUI(navController: NavController, paddingValues: PaddingValues, viewModel: StepCounterViewModel) {
    val steps = viewModel.steps.collectAsState().value
    val calories = viewModel.calories.collectAsState().value
    val goal = viewModel.moveGoal.collectAsState().value

    val progress = (calories.toFloat() / goal).coerceIn(0f, 1f)
    val goalText = "$steps / $goal"
    val distance = viewModel.calculateDistance(steps, 0.75)

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(), // Fill the entire available space
        verticalArrangement = Arrangement.Center, // Center vertically in the space
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        CircularProgressBar(calories = calories.toInt(), goalCalories = goal, modifier = Modifier.size(275.dp))
        Spacer(modifier = Modifier.height(16.dp)) // Create space between progress bar and text
        Text(text = "Steps: $steps")
        Text(text = "Calories: ${calories.toInt()} / $goal kcal")
        Text(text = "Distance: ${String.format("%.2f", distance)} km") // Display distance
        // Text(text = "Goal: $goalText")
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

        // Show congratulations message if goal reached
        if (progress >= 1.0f) {
            Text("Congratulations! Goal Achieved!", color = Color.Green)
        }
    }
}



@Composable
fun CircularProgressBar(calories: Int, goalCalories: Int, modifier: Modifier = Modifier) {
    val progress = (calories.toFloat() / goalCalories).coerceIn(0f, 1f)
    CircularProgressIndicator(progress = progress, modifier = modifier)
    Spacer(modifier = Modifier.height(8.dp)) // Spacing between progress bar and text
    Text(text = "${(progress * 100).toInt()}% of goal")
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestAppTheme {
        HealthDetailsScreen(navController = rememberNavController(), viewModel = viewModel())
    }
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
fun ChangeMoveGoalScreen(navController: NavController, viewModel: StepCounterViewModel) {
    val currentGoal by viewModel.moveGoal.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Change Move Goal", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        // Display the current move goal
        Text("Move Goal: $currentGoal")

        // Plus and minus buttons to adjust the move goal
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { viewModel.increaseMoveGoal() }) {
                Text("+10")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { viewModel.decreaseMoveGoal() }) {
                Text("-10")
            }
        }

        // Save button to apply the changes
        Button(
            onClick = {
                // Directly navigate back since the viewModel is already updated
                navController.popBackStack()
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp)
        ) {
            Text("Save")
        }
    }
}



