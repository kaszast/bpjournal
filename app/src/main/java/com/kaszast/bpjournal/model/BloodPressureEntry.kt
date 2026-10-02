package com.kaszast.bpjournal.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Arm {
    LEFT,
    RIGHT
}

enum class BodyPosition {
    SITTING,
    LYING,
    STANDING
}

enum class BloodPressureCategory {
    OPTIMAL,
    NORMAL,
    HIGH_NORMAL,
    GRADE_1_HYPERTENSION,
    GRADE_2_HYPERTENSION,
    GRADE_3_HYPERTENSION,
    ISOLATED_SYSTOLIC
}

data class BloodPressureEntry(
    val id: Long = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val arm: Arm = Arm.LEFT,
    val position: BodyPosition = BodyPosition.SITTING,
    val tags: Set<String> = emptySet(),
    val notes: String = ""
) {
    /**
     * Pulzusnyomás (Pulse Pressure): szisztolés és diasztolés közötti különbség.
     */
    val pulsePressure: Int
        get() = systolic - diastolic

    /**
     * Középnyomás (Mean Arterial Pressure - MAP): diasztolés + (PP / 3).
     */
    val meanArterialPressure: Int
        get() = diastolic + (pulsePressure / 3)

    /**
     * ESH / ESC irányelvek szerinti kategória besorolás.
     */
    val category: BloodPressureCategory
        get() {
            return when {
                systolic >= 140 && diastolic < 90 -> BloodPressureCategory.ISOLATED_SYSTOLIC
                systolic >= 180 || diastolic >= 110 -> BloodPressureCategory.GRADE_3_HYPERTENSION
                systolic >= 160 || diastolic >= 100 -> BloodPressureCategory.GRADE_2_HYPERTENSION
                systolic >= 140 || diastolic >= 90 -> BloodPressureCategory.GRADE_1_HYPERTENSION
                systolic >= 130 || diastolic >= 85 -> BloodPressureCategory.HIGH_NORMAL
                systolic >= 120 || diastolic >= 80 -> BloodPressureCategory.NORMAL
                else -> BloodPressureCategory.OPTIMAL
            }
        }

    val localDateTime: LocalDateTime
        get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())

    fun formattedDateTime(formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")): String {
        return localDateTime.format(formatter)
    }

    fun formattedDate(formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d", java.util.Locale.getDefault())): String {
        return localDateTime.format(formatter)
    }

    fun formattedTime(formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")): String {
        return localDateTime.format(formatter)
    }
}
