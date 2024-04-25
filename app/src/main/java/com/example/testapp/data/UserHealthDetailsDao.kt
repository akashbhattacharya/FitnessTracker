package com.example.testapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserHealthDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(details: UserHealthDetails)
    @Query("SELECT * from userHealthDetails WHERE id = 1")
    fun getDetailStream(): Flow<UserHealthDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(foods: FoodListDetails)

    @Query("SELECT * from foodListDetails")
    fun getAllFoodStream(): Flow<List<FoodListDetails>>
    @Query("DELETE FROM foodListDetails")
    suspend fun deleteAllMeals()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: StepDetails)
    @Query("SELECT * from stepDetails WHERE id = 1")
    fun getStepStream(): Flow<StepDetails>

}