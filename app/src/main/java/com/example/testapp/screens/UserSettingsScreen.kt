package com.example.testapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

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
