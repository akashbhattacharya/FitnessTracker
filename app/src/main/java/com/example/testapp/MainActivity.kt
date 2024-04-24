package com.example.testapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.testapp.screens.HealthDetailsScreen
import com.example.testapp.ui.theme.TestAppTheme
import com.example.testapp.viewmodel.StepCounterViewModel

class MainActivity : ComponentActivity() {
    private val activityRecognitionRequestCode = 1001 // Unique request code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionsIfNeeded()
    }

    private fun requestPermissionsIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                activityRecognitionRequestCode
            )
        } else {
            // Permission has already been granted
            setupUI()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == activityRecognitionRequestCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted
                setupUI()
            } else {
                // Permission was denied. Handle the feature limitation or close the app.
                finish() // Optionally, you could handle this with a dialog or a warning message.
            }
        }
    }

    private fun setupUI() {
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

    @Composable
    fun StepCounterApp(viewModel: StepCounterViewModel) {
        val navController = rememberNavController()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Step Counter") },
                    navigationIcon = if (navController.currentBackStackEntryAsState().value?.destination?.route != "main") {
                        // Show back button if not on the main screen
                        {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    } else {
                        null
                    },
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
}
