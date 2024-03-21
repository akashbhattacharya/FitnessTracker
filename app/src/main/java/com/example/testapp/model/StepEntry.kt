package com.example.testapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_entries")
data class StepEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val stepCount: Int,
    val timestamp: Long
)

