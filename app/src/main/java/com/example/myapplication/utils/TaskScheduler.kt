package com.example.myapplication.utils

import ReminderWorker
import android.content.Context
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class TaskScheduler(private val context: Context) {

    fun scheduleTaskReminder(taskId: Long, dueTime: Long) {
        val currentTime = System.currentTimeMillis()
        val timeDifference = dueTime - currentTime

        if (timeDifference > 0 && timeDifference > 5 * 60 * 1000) {
            val inputData = Data.Builder()
                .putLong("taskId", taskId)
                .putLong("dueTime", dueTime)
                .build()

            val delay = timeDifference - 5 * 60 * 1000 // Schedule 5 mins before due time
            val workRequestTag = "workRequest_$taskId"
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // Any network type
                .setRequiresCharging(false) // Doesn't require charging
                .setRequiresBatteryNotLow(false) // Battery level can be low
                .setRequiresStorageNotLow(false) // Storage can be low
                .setRequiresDeviceIdle(false) // Device doesn't need to be idle
                .build()
            val workRequest = OneTimeWorkRequest.Builder(ReminderWorker::class.java)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .setConstraints(constraints)
                .addTag(workRequestTag)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

    fun deleteTaskByTag(id: Long) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag("workRequest_$id")
    }
}
