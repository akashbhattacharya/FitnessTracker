package com.example.testapp.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userHealthDetails")
data class UserHealthDetails(
    @PrimaryKey val id: Int = 1,
      val age: Int,
      val weight: Double,
      val height: Double,
      val sex: String
)
// @PrimaryKey(autoGenerate = true) val id: Int = 0,
