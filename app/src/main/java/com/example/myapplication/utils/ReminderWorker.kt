import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.R
import com.example.myapplication.utils.ApplicationGlobal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val taskId = inputData.getLong("taskId", 0L)
        val dueTime = inputData.getLong("dueTime", 0L)

        if (taskId > 0L && dueTime > 0L) {
            val currentTime = System.currentTimeMillis()
            val timeDifference = dueTime - currentTime

            if (timeDifference > 0 && timeDifference <= 5 * 60 * 1000) {
//                val serviceIntent = Intent(applicationContext, ForegroundService::class.java)
//                ContextCompat.startForegroundService(applicationContext, serviceIntent)


//                // Show notification
                createNotification(taskId)
                ApplicationGlobal.database.taskDao().updateTaskCompletion(taskId, true)
            }
        }

        Result.success()
    }

    private fun createNotification(taskId: Long) {
        val context = applicationContext
        val channelId = "reminder_channel"
        val notificationId = taskId // You can use a unique ID for each notification

        // Create a notification channel (required for Android Oreo and later)
        createNotificationChannel(context, channelId)
//        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val soundUri = Uri.parse("android.resource://com.example.myapplication/${R.raw.alarm}")

        // Create a notification using NotificationCompat
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Task Reminder")
            .setContentText("Your task is due soon!")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 400, 200, 400, 200, 400, 200, 400))
            .setAutoCancel(true)
            .build()

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId.toInt(), notification)
        }
    }

    private fun createNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminder Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for task reminders"
            }
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            val soundUri = Uri.parse("android.resource://com.example.myapplication/${R.raw.alarm}")

            channel.setSound(soundUri, audioAttributes)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0, 400, 200, 400, 200, 400, 200, 400)
            // Register the channel with the system
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
