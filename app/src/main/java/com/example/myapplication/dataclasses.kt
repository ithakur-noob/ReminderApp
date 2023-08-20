package com.example.myapplication


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

// Task.kt
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    var dueDate: Long,
    var isCompleted: Boolean
):Serializable
