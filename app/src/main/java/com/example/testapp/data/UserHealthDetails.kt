package com.example.testapp.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userHealthDetails")
data class UserHealthDetails(
   // @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @PrimaryKey val age: Int,
      val weight: Double,
      val height: Double,
      val sex: String
)
