package com.example.dicodingevent.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dicodingevent.R
import com.example.dicodingevent.data.remote.retrofit.ApiConfig
import com.example.dicodingevent.ui.detail.DetailActivity
import retrofit2.HttpException

class EventWorkerNotification(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "dicoding channel"
    }

    override suspend fun doWork(): Result =
        try {
            val apiService = ApiConfig.getApiService()
            val response = apiService.getEvents(-1, "", 1).execute()

            if (response.isSuccessful) {
                val event = response.body()?.listEvents?.firstOrNull()
                event?.let {
                    showNotification(
                        it.id ?: 0,
                        it.name ?: "Event Terdekat",
                        it.beginTime ?: "Waktu tidak tersedia",
                    )
                    Log.d("DailyReminderWorker", "Notifikasi berhasil dikirim: ${it.name}")
                }
            }
            Result.success()
        } catch (e: HttpException) {
            Log.e("DailyReminderWorker", "HTTP Error: ${e.message()}")
            Result.retry()
        } catch (e: Exception) {
            Log.e("DailyReminderWorker", "Error: ${e.message}")
            Result.failure()
        }

    private fun showNotification(
        id: Int,
        name: String,
        message: String,
    ) {
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent =
            Intent(applicationContext, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_EVENT_ID, id)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val pendingIntent =
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        val notification: NotificationCompat.Builder =
            NotificationCompat
                .Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(name)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }
}
