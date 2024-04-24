package com.example.testapp.data

import kotlinx.coroutines.flow.Flow

interface UHDRepository {
    suspend fun insertUHD(details: UserHealthDetails)

    fun getDetailStream(): Flow<UserHealthDetails>

    suspend fun updateUHD(details: UserHealthDetails)
}