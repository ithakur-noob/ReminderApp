package com.example.myapplication.roomDatabase


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>

    @Insert
    fun insertTask(task: Task): Long

    @Update
    fun updateTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean)

    @Delete
    fun deleteTask(task: Task)

    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate = :dueDate")
    suspend fun doesDueDateExist(dueDate: Long): Int
}
