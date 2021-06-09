package com.kekadoc.test.course.dollar.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kekadoc.test.course.dollar.MainActivity
import com.kekadoc.test.course.dollar.R
import com.kekadoc.test.course.dollar.isAppForeground
import com.kekadoc.test.course.dollar.model.numericValue
import com.kekadoc.test.course.dollar.repository.HttpRepository
import com.kekadoc.test.course.dollar.repository.LocalStorage

class CourseUpdateWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    companion object {
        private const val TAG: String = "CourseUpdateWorker-TAG"

        const val NOTIFICATION_ID = 0
        const val NOTIFICATION_NAME = "appName"
        const val NOTIFICATION_CHANNEL = "app_channel_01"
    }

    private val storage = LocalStorage.getInstance(appContext)

    override suspend fun doWork(): Result {
        try {
            val monthlyCourse = HttpRepository.loadMonthlyCourse()
            val dailyCourse = HttpRepository.loadDailyCourse()

            val newDailyDollarCourseValue = dailyCourse.valutes.find {
                it.id == HttpRepository.DOLLAR_ID
            }.numericValue
            val savedDollarCourseValue = storage.dailyCourse.value?.valutes?.find {
                it.id == HttpRepository.DOLLAR_ID
            }.numericValue

            Log.e(TAG, "doWork: $newDailyDollarCourseValue $savedDollarCourseValue")
            if (newDailyDollarCourseValue > savedDollarCourseValue && !applicationContext.isAppForeground()) {
                showNotification(applicationContext.getString(R.string.dollar_course_update_message), newDailyDollarCourseValue.toString())
            }
            storage.saveDailyCourse(dailyCourse)
            storage.saveMonthlyCourse(monthlyCourse)
            return Result.success()
        }catch (e: Throwable) {
            Log.e(TAG, "CourseUpdateWorker Error: $e")
            return Result.retry()
        }
    }

    private fun showNotification(title: String, content: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_money_24)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NOTIFICATION_NAME
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL, name, importance)

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())

    }

}