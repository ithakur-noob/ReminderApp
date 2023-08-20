package com.example.myapplication.roomDatabase

import com.example.myapplication.Task
import com.example.myapplication.utils.ApplicationGlobal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TaskRepository {

    private val taskDao: TaskDao = ApplicationGlobal.database.taskDao()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }


    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }


    suspend fun doesDueDateExist(dueDate: Long): Boolean {
        return taskDao.doesDueDateExist(dueDate) > 0
    }

    fun getAllTasks() = taskDao.getAllTasks()
}
