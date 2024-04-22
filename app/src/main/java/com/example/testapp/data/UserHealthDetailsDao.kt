package com.example.testapp.data

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

interface UserHealthDetailsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(details: UserHealthDetails)
    @Update
    suspend fun update(details: UserHealthDetails)
}