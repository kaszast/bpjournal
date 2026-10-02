package com.kaszast.bpjournal.data

import com.kaszast.bpjournal.model.BloodPressureCategory
import com.kaszast.bpjournal.model.BloodPressureEntry
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class BloodPressureStatisticsTest {

    @Test
    fun testSummaryCalculation() {
        val entries = listOf(
            BloodPressureEntry(id = 1, systolic = 120, diastolic = 80, pulse = 70),
            BloodPressureEntry(id = 2, systolic = 140, diastolic = 90, pulse = 80),
            BloodPressureEntry(id = 3, systolic = 160, diastolic = 100, pulse = 90)
        )

        val summary = BloodPressureStatisticsCalculator.calculateSummary(entries)

        assertEquals(3, summary.totalCount)
        assertEquals(140.0, summary.avgSystolic, 0.01)
        assertEquals(90.0, summary.avgDiastolic, 0.01)
        assertEquals(80.0, summary.avgPulse, 0.01)
        assertEquals(120, summary.minSystolic)
        assertEquals(160, summary.maxSystolic)
        assertEquals(80, summary.minDiastolic)
        assertEquals(100, summary.maxDiastolic)

        assertEquals(1, summary.categoryDistribution[BloodPressureCategory.NORMAL])
        assertEquals(1, summary.categoryDistribution[BloodPressureCategory.GRADE_1_HYPERTENSION])
        assertEquals(1, summary.categoryDistribution[BloodPressureCategory.GRADE_2_HYPERTENSION])
    }

    @Test
    fun testEmptySummaryCalculation() {
        val summary = BloodPressureStatisticsCalculator.calculateSummary(emptyList())
        assertEquals(0, summary.totalCount)
        assertEquals(0.0, summary.avgSystolic, 0.01)
    }

    @Test
    fun testDailyAverages() {
        val zone = ZoneId.systemDefault()
        val day1 = LocalDate.of(2026, 10, 1).atTime(8, 0).atZone(zone).toInstant().toEpochMilli()
        val day1Evening = LocalDate.of(2026, 10, 1).atTime(20, 0).atZone(zone).toInstant().toEpochMilli()
        val day2 = LocalDate.of(2026, 10, 2).atTime(9, 0).atZone(zone).toInstant().toEpochMilli()

        val entries = listOf(
            BloodPressureEntry(id = 1, systolic = 120, diastolic = 80, pulse = 70, timestamp = day1),
            BloodPressureEntry(id = 2, systolic = 130, diastolic = 84, pulse = 74, timestamp = day1Evening),
            BloodPressureEntry(id = 3, systolic = 140, diastolic = 90, pulse = 80, timestamp = day2)
        )

        val daily = BloodPressureStatisticsCalculator.calculateDailyAverages(entries)
        assertEquals(2, daily.size)

        // Day 1 átlag: (120+130)/2 = 125, (80+84)/2 = 82
        assertEquals(125.0, daily[0].averageSystolic, 0.01)
        assertEquals(82.0, daily[0].averageDiastolic, 0.01)
        assertEquals(2, daily[0].count)

        // Day 2
        assertEquals(140.0, daily[1].averageSystolic, 0.01)
        assertEquals(1, daily[1].count)
    }
}
