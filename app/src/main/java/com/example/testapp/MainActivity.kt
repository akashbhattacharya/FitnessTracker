package com.example.testapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.testapp.screens.HealthDetailsScreen
import com.example.testapp.ui.theme.TestAppTheme
import com.example.testapp.viewmodel.StepCounterViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            val viewModel: StepCounterViewModel = viewModel()
            val isDarkModeEnabled by viewModel.isDarkModeEnabled.collectAsState()

            TestAppTheme(darkTheme = isDarkModeEnabled) {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
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
                composable("main") {
                    com.example.testapp.screens.StepCounterUI(
                        navController,
                        paddingValues,
                        viewModel
                    )
                }
                composable("weeklySteps") { com.example.testapp.screens.WeeklyStepsScreen() }
                composable("userSettings") {
                    com.example.testapp.screens.UserSettingsScreen(
                        navController
                    )
                }
                composable("healthDetails") {
                    com.example.testapp.screens.HealthDetailsScreen(
                        navController,
                        viewModel
                    )
                }
                composable("foodList"){
                    com.example.testapp.screens.FoodListScreen(viewModel, navController)
                }
                composable("changeMoveGoal") {
                    com.example.testapp.screens.ChangeMoveGoalScreen(
                        navController,
                        viewModel
                    )
                }
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestAppTheme {
        HealthDetailsScreen(navController = rememberNavController(), viewModel = viewModel())
    }
}
// Placeholder for the bar chart screen






