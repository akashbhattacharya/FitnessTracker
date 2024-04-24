package com.example.testapp.data

import android.content.Context


interface AppContainer {
    val uhdRepository: UHDRepository
}
class AppDataContainer(private val context: Context) : AppContainer {
    override val uhdRepository: UHDRepository by lazy {
        RealUHDRepository(UHDDatabase.getDatabase(context).uhdDao())
    }
}