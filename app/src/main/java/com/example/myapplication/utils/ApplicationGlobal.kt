package com.example.myapplication.utils

import android.app.Application
import androidx.room.Room
import androidx.work.Configuration
import androidx.work.WorkManager

import com.example.myapplication.roomDatabase.TaskDatabase

class ApplicationGlobal:Application() {
    companion object {
        lateinit var database: TaskDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(applicationContext, TaskDatabase::class.java, "task_database").build()

    }
}