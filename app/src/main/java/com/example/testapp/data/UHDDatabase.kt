package com.example.testapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserHealthDetails::class], version = 1, exportSchema = false)
abstract class UHDDatabase : RoomDatabase() {
    abstract fun uhdDao(): UserHealthDetailsDao

    companion object {
        @Volatile
        private var Instance: UHDDatabase? = null
        fun getDatabase(context: Context): UHDDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, UHDDatabase::class.java, "uhd_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}