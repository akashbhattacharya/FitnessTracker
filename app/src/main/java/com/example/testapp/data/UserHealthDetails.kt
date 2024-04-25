package com.example.testapp.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userHealthDetails")
data class UserHealthDetails(
    @PrimaryKey val id: Int = 1,
      val age: Int,
      val weight: Double,
      val height: Double,
      val sex: String,
      val bmr: Double = 0.0
)

@Entity(tableName = "foodListDetails")
data class FoodListDetails(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Int,
    val timestamp: String
)

@Entity(tableName = "stepDetails")
data class StepDetails(
    @PrimaryKey val id: Int = 1,
    val tempSteps: Int,
    val totalSteps: Int
)

