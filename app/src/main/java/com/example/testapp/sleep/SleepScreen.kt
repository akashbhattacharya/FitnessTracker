/*package com.example.testapp.sleep

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.testapp.StepCounterViewModel
import java.util.*

@Composable
fun SleepScreen(navController: NavController, viewModel: StepCounterViewModel) {
    // Use state flows from the viewModel for sleep start/end times, sleep status, and sleep quality
    val sleepStart by viewModel.sleepStartTime.collectAsState()
    val sleepEnd by viewModel.sleepEndTime.collectAsState()
    val isSleeping by viewModel.isSleeping.collectAsState()
    val sleepQuality by viewModel.sleepQuality.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sleep Tracking", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        // Display sleep start time
        Text("Sleep Start: ${sleepStart?.time.toString()}")
        // Display sleep end time
        Text("Sleep End: ${sleepEnd?.time.toString()}")

        Spacer(modifier = Modifier.height(16.dp))

        // Display sleep status
        Text("Sleep Status: ${if (isSleeping) "Sleeping" else "Not Sleeping"}")

        // Display sleep quality if available
        sleepQuality?.let {
            Text("Sleep Quality: $it")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to set sleep start time manually
        Button(onClick = {
            // Logic to open a time picker and set sleep start time
            viewModel.setSleepStartTime(Calendar.getInstance())
        }) {
            Text("Set Sleep Start")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to set sleep end time manually
        Button(onClick = {
            // Logic to open a time picker and set sleep end time
            viewModel.setSleepEndTime(Calendar.getInstance())
        }) {
            Text("Set Sleep End")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Additional UI components as needed
    }
}
*/