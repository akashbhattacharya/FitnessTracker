package com.example.testapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.viewmodel.StepCounterViewModel
import kotlinx.coroutines.launch

@Composable
fun HealthDetailsScreen(navController: NavController, viewModel: StepCounterViewModel ) {
    var selectedAge by remember { mutableStateOf<String?>(null) }
    var isAgeDropdownExpanded by remember { mutableStateOf(false) } // Age dropdown expanded state
    var selectedHeight by remember { mutableStateOf<String?>(null) }
    var isHeightDropdownExpanded by remember { mutableStateOf(false) } // Height dropdown expanded state
    var selectedWeight by remember { mutableStateOf<String?>(null) }
    var isWeightDropdownExpanded by remember { mutableStateOf(false) } // Weight dropdown expanded state
    var selectedSex by remember { mutableStateOf<String?>(null) }
    var isSexDropdownExpanded by remember { mutableStateOf(false) } // Sex dropdown expanded state

    val ageOptions = (18..100).map { it.toString() }
    val heightOptions = (30..275).map { it.toString() }
    val weightOptions = (1..454).map { it.toString() }
    val sexOptions = listOf("Male", "Female")

    val coroutineScope = rememberCoroutineScope()


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Health Details", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        // Age selection
        Box(
            modifier = Modifier.fillMaxWidth().clickable { isAgeDropdownExpanded = true }
        ) {
            Text(
                text = selectedAge ?: "Select Age",
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
            )
            DropdownMenu(
                expanded = isAgeDropdownExpanded,
                onDismissRequest = { isAgeDropdownExpanded = false }
            ) {
                ageOptions.forEach { age ->
                    DropdownMenuItem(onClick = {
                        selectedAge = age
                        isAgeDropdownExpanded = false
                    }) {
                        Text(age)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Height selection
        Box(
            modifier = Modifier.fillMaxWidth().clickable { isHeightDropdownExpanded = true }
        ) {
            Text(
                text = selectedHeight ?: "Select Height (cm)",
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
            )
            DropdownMenu(
                expanded = isHeightDropdownExpanded,
                onDismissRequest = { isHeightDropdownExpanded = false }
            ) {
                heightOptions.forEach { height ->
                    DropdownMenuItem(onClick = {
                        selectedHeight = height
                        isHeightDropdownExpanded = false
                    }) {
                        Text(height)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weight selection
        Box(
            modifier = Modifier.fillMaxWidth().clickable { isWeightDropdownExpanded = true }
        ) {
            Text(
                text = selectedWeight ?: "Select Weight (kg)",
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
            )
            DropdownMenu(
                expanded = isWeightDropdownExpanded,
                onDismissRequest = { isWeightDropdownExpanded = false }
            ) {
                weightOptions.forEach { weight ->
                    DropdownMenuItem(onClick = {
                        selectedWeight = weight
                        isWeightDropdownExpanded = false
                    }) {
                        Text(weight)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sex selection
        Box(
            modifier = Modifier.fillMaxWidth().clickable { isSexDropdownExpanded = true }
        ) {
            Text(
                text = selectedSex ?: "Select Sex",
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
            )
            DropdownMenu(
                expanded = isSexDropdownExpanded,
                onDismissRequest = { isSexDropdownExpanded = false }
            ) {
                sexOptions.forEach { sex ->
                    DropdownMenuItem(onClick = {
                        selectedSex = sex
                        isSexDropdownExpanded = false
                    }) {
                        Text(sex)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val age = selectedAge?.toIntOrNull() ?: 0
            val height = selectedHeight?.toDoubleOrNull() ?: 0.0
            val weight = selectedWeight?.toDoubleOrNull() ?: 0.0
            coroutineScope.launch {
                viewModel.setHealthDetails(
                    height,
                    weight,
                    age,
                    selectedSex ?: ""
                )
            }

            navController.navigateUp()
        }) {
            Text("Save")
        }
    }
}
