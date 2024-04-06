package com.example.testapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.testapp.viewmodel.StepCounterViewModel

@Composable
fun NotificationsScreen(navController: NavController, viewModel: StepCounterViewModel) {
    val isDarkModeEnabled by viewModel.isDarkModeEnabled.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Notifications", style = MaterialTheme.typography.h5)
        // Existing UI components...

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.toggleDarkMode() }
        ) {
            Text(if (isDarkModeEnabled) "Disable Dark Mode" else "Enable Dark Mode")
        }
    }
}
