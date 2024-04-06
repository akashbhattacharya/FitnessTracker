package com.example.testapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.testapp.viewmodel.StepCounterViewModel

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
