package com.example.testapp

import android.app.Application
import com.example.testapp.data.AppContainer
import com.example.testapp.data.AppDataContainer

class CalorieApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}