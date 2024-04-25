package com.example.testapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testapp.screens.*
import com.example.testapp.ui.theme.TestAppTheme
import com.example.testapp.viewmodel.StepCounterViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.launch

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
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                activityRecognitionRequestCode
            )
        } else {
            setupUI()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == activityRecognitionRequestCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                setupUI()
            } else {
                finish()  // Close the app if permission is denied
            }
        }
    }

    private fun setupUI() {
        createNotificationChannels()
        setContent {
            TestAppTheme {
                AppNavigation()
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    "stepCounterChannel",
                    "Step Counter Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Notifications for step milestones" },
                NotificationChannel(
                    "meal_reminder_channel",
                    "Meal Reminder Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Notifications for meal reminders" }
            )
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(channels)
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun AppNavigation() {
        val scaffoldState = rememberScaffoldState()
        val navController = rememberNavController()
        val viewModel: StepCounterViewModel = viewModel()

        val coroutineScope = rememberCoroutineScope() // Remember a CoroutineScope for coroutine launch

        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                DrawerContent(navController, scaffoldState)
            },
            topBar = {
                TopAppBar(
                    title = { Text("Calorie & Step Tracking") },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                scaffoldState.drawerState.open() // Call open() inside a coroutine
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) {paddingValues ->
            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    StepCounterUI(navController, paddingValues, viewModel)
                }
                composable("healthDetails") { HealthDetailsScreen(navController, viewModel) }
                composable("setMealTimings") { SetMealTimingsScreen(navController) }
                composable("achievementScreen") { AchievementsScreen(viewModel) }
                composable("foodList") { FoodListScreen(viewModel, navController) }
            }
        }
    }



    data class DrawerItemData(val label: String, val icon: ImageVector, val navRoute: String)

    @Composable
    fun DrawerContent(navController: NavController, scaffoldState: ScaffoldState) {
        val coroutineScope = rememberCoroutineScope()
        val currentRoute = remember {
            mutableStateOf("")
        }

        // Observe the current back stack entry
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        currentRoute.value = navBackStackEntry?.destination?.route ?: ""

        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)) {

            IconButton(onClick = {
                coroutineScope.launch {
                    scaffoldState.drawerState.close()
                }
            }, modifier = Modifier.align(Alignment.End)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Close Drawer", modifier = Modifier.size(30.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // List of items for the drawer
            val drawerItems = listOf(
                DrawerItemData("Home", Icons.Filled.Home, "main"),
                DrawerItemData("Health Details", Icons.Filled.MonitorHeart, "healthDetails"),
                DrawerItemData("Meal Timings", Icons.Filled.Fastfood, "setMealTimings"),
                DrawerItemData("My Achievements", Icons.Filled.MilitaryTech, "achievementScreen"),
            )

            drawerItems.forEach { item ->
                DrawerItem(
                    icon = item.icon,
                    label = item.label,
                    isActive = currentRoute.value == item.navRoute,
                    navController = navController,
                    navRoute = item.navRoute,
                    scaffoldState = scaffoldState,
                    coroutineScope = coroutineScope
                )
                Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f), thickness = 1.dp)
            }
        }
    }

    @Composable
    fun DrawerItem(
        icon: ImageVector,
        label: String,
        isActive: Boolean,
        navController: NavController,
        navRoute: String,
        scaffoldState: ScaffoldState,
        coroutineScope: CoroutineScope
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    coroutineScope.launch {
                        navController.navigate(navRoute) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        scaffoldState.drawerState.close()
                    }
                }
                .padding(10.dp)
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, fontSize = 20.sp,color =if (isActive) Color.Black else Color.Gray, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium)
        }
    }


}
