package com.kaszast.bpjournal.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kaszast.bpjournal.data.UserSettingsManager

/**
 * BroadcastReceiver triggered after device reboot ([Intent.ACTION_BOOT_COMPLETED])
 * or application package update ([Intent.ACTION_MY_PACKAGE_REPLACED]).
 * Re-schedules any enabled reminders that were cleared from the system [android.app.AlarmManager].
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            Log.d(TAG, "Reboot or package replaced detected ($action), restoring active reminders...")
            val userSettingsManager = UserSettingsManager(context)
            val settings = userSettingsManager.settings.value

            if (settings.morningReminderEnabled) {
                ReminderScheduler.scheduleReminder(context, ReminderType.MORNING, settings.morningReminderTime)
            }
            if (settings.noonReminderEnabled) {
                ReminderScheduler.scheduleReminder(context, ReminderType.NOON, settings.noonReminderTime)
            }
            if (settings.eveningReminderEnabled) {
                ReminderScheduler.scheduleReminder(context, ReminderType.EVENING, settings.eveningReminderTime)
            }
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
