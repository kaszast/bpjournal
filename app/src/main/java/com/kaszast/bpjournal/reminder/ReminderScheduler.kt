package com.kaszast.bpjournal.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.kaszast.bpjournal.MainActivity
import java.util.Calendar

/**
 * Utility responsible for calculating trigger times and scheduling/cancelling
 * precise, high-priority alarms via [AlarmManager].
 *
 * Uses [AlarmManager.setAlarmClock] to guarantee delivery even in Doze mode
 * and under aggressive OEM battery optimizations (e.g. Xiaomi / HyperOS / MIUI).
 */
object ReminderScheduler {
    private const val TAG = "ReminderScheduler"

    /**
     * Calculates the next epoch timestamp (in milliseconds) for a given "HH:mm" time string.
     * If the specified time for today has already elapsed relative to [nowMillis],
     * it advances the schedule to the next calendar day.
     *
     * @param timeStr Time formatted as "HH:mm" (24-hour).
     * @param nowMillis Baseline timestamp in milliseconds (defaults to current system time).
     * @return Next epoch trigger time in milliseconds.
     */
    fun calculateNextTriggerMillis(timeStr: String, nowMillis: Long = System.currentTimeMillis()): Long {
        val parts = timeStr.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        val calendar = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= nowMillis) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis
    }

    /**
     * Schedules a precise alarm for the specified [ReminderType].
     *
     * Primary strategy: [AlarmManager.setAlarmClock].
     * Fallback strategy: [AlarmManager.setExactAndAllowWhileIdle].
     */
    fun scheduleReminder(context: Context, type: ReminderType, timeStr: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerMillis = calculateNextTriggerMillis(timeStr)

        val receiverIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = type.action
            putExtra(ReminderReceiver.EXTRA_REMINDER_TYPE, type.name)
        }
        val operationPendingIntent = PendingIntent.getBroadcast(
            context,
            type.requestCode,
            receiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_RECORD_DIALOG, true)
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            type.requestCode + 10000,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        var scheduled = false
        if (canScheduleExact) {
            try {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerMillis, showPendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, operationPendingIntent)
                scheduled = true
                Log.d(TAG, "Scheduled AlarmClock for ${type.name} at epoch $triggerMillis ($timeStr)")
            } catch (e: SecurityException) {
                Log.w(TAG, "SecurityException on setAlarmClock for ${type.name}, falling back to exact while idle", e)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, operationPendingIntent)
                        scheduled = true
                        Log.d(TAG, "Scheduled setExactAndAllowWhileIdle for ${type.name}")
                    }
                } catch (e2: SecurityException) {
                    Log.w(TAG, "SecurityException on setExactAndAllowWhileIdle for ${type.name}", e2)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed setAlarmClock for ${type.name}", e)
            }
        }

        if (!scheduled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, operationPendingIntent)
                    Log.d(TAG, "Scheduled fallback setAndAllowWhileIdle for ${type.name} at epoch $triggerMillis")
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, operationPendingIntent)
                    Log.d(TAG, "Scheduled legacy set for ${type.name}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fatal: All alarm scheduling strategies failed for ${type.name}", e)
            }
        }
    }

    /**
     * Cancels any active or pending alarm for the given [ReminderType].
     */
    fun cancelReminder(context: Context, type: ReminderType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val receiverIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = type.action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            type.requestCode,
            receiverIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled alarm for ${type.name}")
        }
    }
}
