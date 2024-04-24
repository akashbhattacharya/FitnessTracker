package com.example.testapp.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.viewmodel.StepCounterViewModel
import java.time.LocalTime
import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import java.time.format.DateTimeFormatter


@Composable
fun SetMealTimingsScreen(navController: NavController) {
    val mealTimeViewModel: StepCounterViewModel = viewModel()

    // States to hold the selected times
    val context = LocalContext.current
    val breakfastTime by mealTimeViewModel.breakfastTime.observeAsState()
    val lunchTime by mealTimeViewModel.lunchTime.observeAsState()
    val snackTime by mealTimeViewModel.snackTime.observeAsState()
    val dinnerTime by mealTimeViewModel.dinnerTime.observeAsState()

    // Function to show time picker and update time in ViewModel
    fun showTimePicker(currentTime: LocalTime?, updateTime: (LocalTime) -> Unit) {
        val time = currentTime ?: LocalTime.now()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                updateTime(LocalTime.of(hourOfDay, minute))
            },
            time.hour,
            time.minute,
            true // Use 24-hour clock
        ).show()
    }
    Column {
        Column {
            Button(onClick = { showTimePicker(breakfastTime) { mealTimeViewModel.breakfastTime.value = it } }) {
                        val breakfastButtonText = breakfastTime?.format(DateTimeFormatter.ISO_LOCAL_TIME) ?: "Not Set"
                        Text("Set Breakfast Time: $breakfastButtonText")
                    }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showTimePicker(lunchTime) { mealTimeViewModel.lunchTime.value = it } }) {
                val lunchButtonText = lunchTime?.format(DateTimeFormatter.ISO_LOCAL_TIME) ?: "Not Set"
                Text("Set Lunch Time: $lunchButtonText")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showTimePicker(snackTime) { mealTimeViewModel.snackTime.value = it } }) {
                val snackButtonText = snackTime?.format(DateTimeFormatter.ISO_LOCAL_TIME) ?: "Not Set"
                Text("Set Snack Time: $snackButtonText")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showTimePicker(dinnerTime) { mealTimeViewModel.dinnerTime.value = it } }) {
                val dinnerButtonText = dinnerTime?.format(DateTimeFormatter.ISO_LOCAL_TIME) ?: "Not Set"
                Text("Set Dinner Time: $dinnerButtonText")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                mealTimeViewModel.saveMealTimes(context)
            }) {
                Text("Save Meal Times")
            }

        }
    }
}
