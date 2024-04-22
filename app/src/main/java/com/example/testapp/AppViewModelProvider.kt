package com.example.testapp

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.testapp.viewmodel.StepCounterViewModel


/*object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            StepCounterViewModel(calorieApplication().container.uhdRepository,calorieApplication())
        }
        initializer {
            StepCounterViewModel(calorieApplication().container.uhdRepository,calorieApplication())
        }
    }
}

fun CreationExtras.calorieApplication(): CalorieApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as CalorieApplication)*/