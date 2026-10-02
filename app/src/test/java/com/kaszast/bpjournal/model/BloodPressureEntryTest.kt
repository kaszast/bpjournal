package com.kaszast.bpjournal.model

import org.junit.Assert.assertEquals
import org.junit.Test

class BloodPressureEntryTest {

    @Test
    fun testCalculatedProperties() {
        val entry = BloodPressureEntry(
            systolic = 120,
            diastolic = 80,
            pulse = 72
        )

        assertEquals(40, entry.pulsePressure)
        assertEquals(93, entry.meanArterialPressure)
    }

    @Test
    fun testEshCategories() {
        val optimal = BloodPressureEntry(systolic = 118, diastolic = 78, pulse = 70)
        assertEquals(BloodPressureCategory.OPTIMAL, optimal.category)

        val normalSys = BloodPressureEntry(systolic = 125, diastolic = 75, pulse = 70)
        assertEquals(BloodPressureCategory.NORMAL, normalSys.category)

        val normalDia = BloodPressureEntry(systolic = 115, diastolic = 82, pulse = 70)
        assertEquals(BloodPressureCategory.NORMAL, normalDia.category)

        val highNormal = BloodPressureEntry(systolic = 135, diastolic = 88, pulse = 70)
        assertEquals(BloodPressureCategory.HIGH_NORMAL, highNormal.category)

        val grade1 = BloodPressureEntry(systolic = 145, diastolic = 92, pulse = 70)
        assertEquals(BloodPressureCategory.GRADE_1_HYPERTENSION, grade1.category)

        val grade2 = BloodPressureEntry(systolic = 165, diastolic = 105, pulse = 70)
        assertEquals(BloodPressureCategory.GRADE_2_HYPERTENSION, grade2.category)

        val grade3 = BloodPressureEntry(systolic = 185, diastolic = 115, pulse = 70)
        assertEquals(BloodPressureCategory.GRADE_3_HYPERTENSION, grade3.category)

        val isolatedSys = BloodPressureEntry(systolic = 145, diastolic = 85, pulse = 70)
        assertEquals(BloodPressureCategory.ISOLATED_SYSTOLIC, isolatedSys.category)
    }
}
