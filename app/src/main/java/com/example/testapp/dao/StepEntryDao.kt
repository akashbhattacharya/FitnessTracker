package com.example.testapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.testapp.model.StepEntry

@Dao
interface StepEntryDao {
    @Insert
    suspend fun insertStepEntry(stepEntry: StepEntry): Long // Return type should indicate the result of the insert operation

    @Query("SELECT * FROM step_entries ORDER BY timestamp DESC")
    suspend fun getAllStepEntries(): List<StepEntry> // Correct return type for fetching all step entries
}


