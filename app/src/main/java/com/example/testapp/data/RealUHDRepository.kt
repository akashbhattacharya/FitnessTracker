package com.example.testapp.data

class RealUHDRepository(private val uhdDao: UserHealthDetailsDao) : UHDRepository {

    override suspend fun insertUHD(details: UserHealthDetails) = uhdDao.insert(details)
    override suspend fun updateUHD(details: UserHealthDetails) = uhdDao.update(details)
}