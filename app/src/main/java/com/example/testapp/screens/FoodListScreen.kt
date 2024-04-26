package com.example.testapp.screens

import android.content.ContentValues.TAG
import android.util.Log
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testapp.viewmodel.FoodItem
import com.example.testapp.viewmodel.StepCounterViewModel
import kotlinx.coroutines.launch

@Composable
fun FoodListScreen(viewModel: StepCounterViewModel, navController: NavController) {
    val foodList by viewModel.foodList.collectAsState()
    val totalCalories by viewModel.totalCalories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                        CalorieDisplay(totalCalories = totalCalories)
                },
                backgroundColor = Color(0xff4a6572)
            )
        },
        bottomBar = {
            BottomBarContent(viewModel = viewModel)
        },
        content = { padding ->
            Column(modifier = Modifier
                .padding(padding)
                .imePadding()) {
               //CalorieDisplay(totalCalories = totalCalories)
                StepCounter(foodList = foodList)
            }
        }
    )
}

@Composable
fun CalorieDisplay(totalCalories: Int) {
    val calorieText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.White)) {
            append("Total Calories: ")
        }
        withStyle(style = SpanStyle(color = Color(0xffdb8504))) {
            append("$totalCalories")
        }
    }

    Text(
        text = calorieText,
        style = MaterialTheme.typography.h6,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun BottomBarContent(viewModel: StepCounterViewModel) {
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
    var foodName by rememberSaveable { mutableStateOf("") }
    var calories by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding()
    ) {
        TextField_Custom(
            value = foodName,
            onValueChange = { foodName = it },
            label = "Food Name",
            imeAction = ImeAction.Next
        )
        TextField_Custom(
            value = calories,
            onValueChange = { calories = it },
            label = "Calories",
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )
        Button(
            onClick = {
                var food = foodName.ifBlank { "Zero Calorie Meal" }
                var cal = calories.ifBlank { "0" }
                coroutineScope.launch {
                    viewModel.addFoodItem(food, cal.toInt())
                }
                foodName = ""
                calories = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(24),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff344955))
        ) {
            Text("Add Food", fontSize = 18.sp, color = Color.White)
        }
    }
}
@Composable
fun TextField_Custom(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            backgroundColor = Color(0xffeab676),
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.DarkGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}
@Composable
fun FoodItemView(foodItem: FoodItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.h6.copy(color = Color(0xff262626)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${foodItem.calories} cal",
                    style = MaterialTheme.typography.body2.copy(color = Color.DarkGray)
                )
            }
            Text(
                text = "Added on: ${foodItem.timestamp}",
                style = MaterialTheme.typography.caption.copy(color = Color.Gray),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
