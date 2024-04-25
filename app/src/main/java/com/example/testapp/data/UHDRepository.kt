package com.example.testapp.data

import kotlinx.coroutines.flow.Flow

interface UHDRepository {
    suspend fun insertUHD(details: UserHealthDetails)

    fun getDetailStream(): Flow<UserHealthDetails>

    suspend fun insertFood(foods : FoodListDetails)

    suspend fun deleteAllMeals()

    fun getAllFoodStream(): Flow<List<FoodListDetails>>

    suspend fun insertSteps(steps: StepDetails)

    fun getStepStream(): Flow<StepDetails>


}