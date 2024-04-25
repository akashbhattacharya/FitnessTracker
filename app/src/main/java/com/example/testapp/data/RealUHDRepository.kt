package com.example.testapp.data

import kotlinx.coroutines.flow.Flow

class RealUHDRepository(private val uhdDao: UserHealthDetailsDao) : UHDRepository {

    override suspend fun insertUHD(details: UserHealthDetails) = uhdDao.insert(details)
    override fun getDetailStream(): Flow<UserHealthDetails> = uhdDao.getDetailStream()
    override suspend fun insertFood(foods : FoodListDetails) = uhdDao.insertFood(foods)
    //override fun getFoodStream(id:Int): Flow<FoodListDetails> = uhdDao.getFoodStream(id)
    override fun getAllFoodStream(): Flow<List<FoodListDetails>> = uhdDao.getAllFoodStream()
    override suspend fun insertSteps(steps: StepDetails) = uhdDao.insertSteps(steps)
    override fun getStepStream(): Flow<StepDetails> = uhdDao.getStepStream()

}