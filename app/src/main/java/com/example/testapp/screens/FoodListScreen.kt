package com.example.testapp.screens

import android.content.ContentValues.TAG
import android.util.Log
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.testapp.viewmodel.FoodItem
import com.example.testapp.viewmodel.StepCounterViewModel
import kotlinx.coroutines.launch

@Composable
fun FoodListScreen(viewModel: StepCounterViewModel, navController: NavController) {
    val foodList by viewModel.foodList.collectAsState()
    val totalCalories by viewModel.totalCalories.collectAsState()

    Scaffold(
        bottomBar = {
            BottomBarContent(viewModel = viewModel)
        },
        content = { padding ->
            Column(modifier = Modifier
                .padding(padding)
                .imePadding()) {
                Text(
                    "Total Calories: $totalCalories",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
                StepCounter(foodList = foodList)
            }
        }
    )
}

@Composable
fun BottomBarContent(viewModel: StepCounterViewModel) {
    // Apply imePadding here to lift the entire bottom bar
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .imePadding()) {
        FoodEntryForm(viewModel = viewModel)
    }
}

@Composable
fun StepCounter(foodList: List<FoodItem>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.imePadding()) {
        items(foodList) { foodItem ->
            FoodItemView(foodItem)
        }
    }
}

@Composable
fun FoodEntryForm(viewModel: StepCounterViewModel) {
    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .imePadding()) {
        TextField(
            value = foodName,
            onValueChange = { foodName = it },
            label = { Text("Food Name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = calories,
            onValueChange = { calories = it },
            label = { Text("Calories") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,imeAction = ImeAction.Done ),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val food = foodName // Capture the current value of foodName
                val cal = calories // Capture the current value of calories
                coroutineScope.launch {
                    viewModel.addFoodItem(food, cal.toIntOrNull() ?: 0)
                }
                foodName = ""
                calories = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Food")
        }
    }
}

@Composable
fun FoodItemView(foodItem: FoodItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(foodItem.name, style = MaterialTheme.typography.h6)
            Text("${foodItem.calories} cal", style = MaterialTheme.typography.body2)
        }
        Text("Added on: ${foodItem.timestamp}", style = MaterialTheme.typography.caption)
    }
}