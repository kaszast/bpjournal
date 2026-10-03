package com.kaszast.bpjournal.reminder

import androidx.annotation.StringRes
import com.kaszast.bpjournal.R

/**
 * Enumeration representing available daily blood pressure measurement reminder checkpoints.
 *
 * @property requestCode Unique request code for AlarmManager pending intents.
 * @property action Explicit broadcast intent action matching manifest filters.
 * @property titleRes String resource ID for localized reminder notification title.
 * @property defaultTime Default trigger time in "HH:mm" 24-hour format.
 */
enum class ReminderType(
    val requestCode: Int,
    val action: String,
    @StringRes val titleRes: Int,
    val defaultTime: String
) {
    MORNING(
        requestCode = 1001,
        action = "com.kaszast.bpjournal.action.REMINDER_MORNING",
        titleRes = R.string.reminder_morning,
        defaultTime = "08:00"
    ),
    NOON(
        requestCode = 1002,
        action = "com.kaszast.bpjournal.action.REMINDER_NOON",
        titleRes = R.string.reminder_noon,
        defaultTime = "12:00"
    ),
    EVENING(
        requestCode = 1003,
        action = "com.kaszast.bpjournal.action.REMINDER_EVENING",
        titleRes = R.string.reminder_evening,
        defaultTime = "20:00"
    );

    companion object {
        fun fromAction(action: String?): ReminderType? {
            return entries.firstOrNull { it.action == action }
        }
    }
}
