package com.example.testapp.data

data class HealthDetails(
    val age: Int,
    val weight: Double,
    val height: Double,
    val sex: String,
    val bmr: Double = 0.0
)