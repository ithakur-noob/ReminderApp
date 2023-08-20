package com.example.myapplication.roomDatabase


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.Task


@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
