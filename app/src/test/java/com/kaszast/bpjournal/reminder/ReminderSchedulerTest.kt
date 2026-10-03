package com.kaszast.bpjournal.reminder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ReminderSchedulerTest {

    @Test
    fun testReminderTypeFromAction() {
        assertEquals(ReminderType.MORNING, ReminderType.fromAction("com.kaszast.bpjournal.action.REMINDER_MORNING"))
        assertEquals(ReminderType.NOON, ReminderType.fromAction("com.kaszast.bpjournal.action.REMINDER_NOON"))
        assertEquals(ReminderType.EVENING, ReminderType.fromAction("com.kaszast.bpjournal.action.REMINDER_EVENING"))
        assertNull(ReminderType.fromAction("unknown.action"))
        assertNull(ReminderType.fromAction(null))
    }

    @Test
    fun testCalculateNextTriggerMillis_FutureTimeToday() {
        val baseCalendar = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 3, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val now = baseCalendar.timeInMillis

        // Target: 12:00 (later today)
        val triggerMillis = ReminderScheduler.calculateNextTriggerMillis("12:00", now)

        val triggerCal = Calendar.getInstance().apply { timeInMillis = triggerMillis }
        assertEquals(2026, triggerCal.get(Calendar.YEAR))
        assertEquals(Calendar.OCTOBER, triggerCal.get(Calendar.MONTH))
        assertEquals(3, triggerCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(12, triggerCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, triggerCal.get(Calendar.MINUTE))
        assertEquals(0, triggerCal.get(Calendar.SECOND))
        assertTrue(triggerMillis > now)
    }

    @Test
    fun testCalculateNextTriggerMillis_PastTimeToday_RollsOverToTomorrow() {
        val baseCalendar = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 3, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val now = baseCalendar.timeInMillis

        // Target: 08:00 (earlier today, must roll to tomorrow Oct 4)
        val triggerMillis = ReminderScheduler.calculateNextTriggerMillis("08:00", now)

        val triggerCal = Calendar.getInstance().apply { timeInMillis = triggerMillis }
        assertEquals(2026, triggerCal.get(Calendar.YEAR))
        assertEquals(Calendar.OCTOBER, triggerCal.get(Calendar.MONTH))
        assertEquals(4, triggerCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(8, triggerCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, triggerCal.get(Calendar.MINUTE))
        assertEquals(0, triggerCal.get(Calendar.SECOND))
        assertTrue(triggerMillis > now)
    }

    @Test
    fun testCalculateNextTriggerMillis_ExactSameMinute_RollsOverToTomorrow() {
        val baseCalendar = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 3, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val now = baseCalendar.timeInMillis

        // Target: 10:00 (exact current time, must roll to tomorrow to prevent immediate double-fire)
        val triggerMillis = ReminderScheduler.calculateNextTriggerMillis("10:00", now)

        val triggerCal = Calendar.getInstance().apply { timeInMillis = triggerMillis }
        assertEquals(4, triggerCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(10, triggerCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, triggerCal.get(Calendar.MINUTE))
        assertTrue(triggerMillis > now)
    }
}
