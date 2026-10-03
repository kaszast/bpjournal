package com.kaszast.bpjournal.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kaszast.bpjournal.MainActivity
import com.kaszast.bpjournal.R
import com.kaszast.bpjournal.data.UserSettingsManager

/**
 * BroadcastReceiver triggered by [android.app.AlarmManager] at the designated reminder time.
 * Responsible for creating the high-priority notification channel, displaying the
 * heads-up notification with sound and vibration, and scheduling the next day's alarm.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val type = ReminderType.fromAction(action) ?: return

        Log.d(TAG, "onReceive triggered for reminder: ${type.name}")

        // 1. Ensure notification channel exists
        createNotificationChannel(context)

        // 2. Dispatch reminder notification
        showNotification(context, type)

        // 3. Reschedule for tomorrow to preserve the recurring daily schedule
        rescheduleNextOccurrence(context, type)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val existing = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()

                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.notification_channel_desc)
                    enableLights(true)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                    setSound(soundUri, audioAttributes)
                    setShowBadge(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun showNotification(context: Context, type: ReminderType) {
        // Enforce runtime permission check for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                Log.w(TAG, "POST_NOTIFICATIONS not granted. Suppressing notification for ${type.name}")
                return
            }
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_RECORD_DIALOG, true)
        }
        val tapPendingIntent = PendingIntent.getActivity(
            context,
            type.requestCode + 20000,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val title = context.getString(type.titleRes)
        val body = context.getString(R.string.reminder_notification_body)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(tapPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        try {
            NotificationManagerCompat.from(context).notify(type.requestCode, builder.build())
            Log.d(TAG, "Notification successfully posted for ${type.name}")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while displaying notification", e)
        }
    }

    private fun rescheduleNextOccurrence(context: Context, type: ReminderType) {
        val userSettingsManager = UserSettingsManager(context)
        val currentSettings = userSettingsManager.settings.value

        val (isEnabled, timeStr) = when (type) {
            ReminderType.MORNING -> Pair(currentSettings.morningReminderEnabled, currentSettings.morningReminderTime)
            ReminderType.NOON -> Pair(currentSettings.noonReminderEnabled, currentSettings.noonReminderTime)
            ReminderType.EVENING -> Pair(currentSettings.eveningReminderEnabled, currentSettings.eveningReminderTime)
        }

        if (isEnabled) {
            ReminderScheduler.scheduleReminder(context, type, timeStr)
        }
    }

    companion object {
        const val CHANNEL_ID = "bpjournal_reminders_channel"
        const val EXTRA_REMINDER_TYPE = "extra_reminder_type"
        private const val TAG = "ReminderReceiver"
    }
}
