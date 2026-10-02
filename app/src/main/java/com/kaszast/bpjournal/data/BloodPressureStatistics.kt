package com.kaszast.bpjournal.data

import com.kaszast.bpjournal.model.BloodPressureCategory
import com.kaszast.bpjournal.model.BloodPressureEntry
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.IsoFields

data class AggregatedAverage(
    val periodLabel: String,
    val shortLabel: String = "",
    val averageSystolic: Double,
    val averageDiastolic: Double,
    val averagePulse: Double,
    val count: Int,
    val timestamp: Long
)

data class SummaryStatistics(
    val totalCount: Int,
    val avgSystolic: Double,
    val avgDiastolic: Double,
    val avgPulse: Double,
    val minSystolic: Int,
    val maxSystolic: Int,
    val minDiastolic: Int,
    val maxDiastolic: Int,
    val avgPulsePressure: Double,
    val avgMeanArterialPressure: Double,
    val categoryDistribution: Map<BloodPressureCategory, Int>
)

object BloodPressureStatisticsCalculator {

    fun calculateSummary(entries: List<BloodPressureEntry>): SummaryStatistics {
        if (entries.isEmpty()) {
            return SummaryStatistics(
                totalCount = 0,
                avgSystolic = 0.0,
                avgDiastolic = 0.0,
                avgPulse = 0.0,
                minSystolic = 0,
                maxSystolic = 0,
                minDiastolic = 0,
                maxDiastolic = 0,
                avgPulsePressure = 0.0,
                avgMeanArterialPressure = 0.0,
                categoryDistribution = emptyMap()
            )
        }

        val total = entries.size
        val avgSys = entries.map { it.systolic }.average()
        val avgDia = entries.map { it.diastolic }.average()
        val avgPulse = entries.map { it.pulse }.average()
        val minSys = entries.minOf { it.systolic }
        val maxSys = entries.maxOf { it.systolic }
        val minDia = entries.minOf { it.diastolic }
        val maxDia = entries.maxOf { it.diastolic }
        val avgPP = entries.map { it.pulsePressure }.average()
        val avgMAP = entries.map { it.meanArterialPressure }.average()

        val dist = entries.groupingBy { it.category }.eachCount()

        return SummaryStatistics(
            totalCount = total,
            avgSystolic = avgSys,
            avgDiastolic = avgDia,
            avgPulse = avgPulse,
            minSystolic = minSys,
            maxSystolic = maxSys,
            minDiastolic = minDia,
            maxDiastolic = maxDia,
            avgPulsePressure = avgPP,
            avgMeanArterialPressure = avgMAP,
            categoryDistribution = dist
        )
    }

    private fun getLocalizedShortDatePattern(locale: java.util.Locale): String {
        return when (locale.language) {
            "hu" -> "MM. dd."
            "de", "pl", "cs", "sk", "ru" -> "dd.MM."
            "en" -> if (locale.country == "GB" || locale.country == "AU") "d/M" else "M/d"
            "fr", "es", "it", "pt" -> "dd/MM"
            else -> "MM/dd"
        }
    }

    private fun getLocalizedMonthPattern(locale: java.util.Locale): String {
        return when (locale.language) {
            "hu" -> "yyyy. MMM"
            else -> "MMM yyyy"
        }
    }

    /**
     * Napi csoportosítás és átlagok regionális dátumformázással
     */
    fun calculateDailyAverages(entries: List<BloodPressureEntry>): List<AggregatedAverage> {
        val zone = ZoneId.systemDefault()
        val locale = java.util.Locale.getDefault()
        val fullDateFormatter = java.time.format.DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.MEDIUM).withLocale(locale)
        val shortDateFormatter = java.time.format.DateTimeFormatter.ofPattern(getLocalizedShortDatePattern(locale), locale)

        return entries
            .groupBy { entry ->
                entry.localDateTime.toLocalDate()
            }
            .map { (date, dayEntries) ->
                AggregatedAverage(
                    periodLabel = date.format(fullDateFormatter),
                    shortLabel = date.format(shortDateFormatter),
                    averageSystolic = dayEntries.map { it.systolic }.average(),
                    averageDiastolic = dayEntries.map { it.diastolic }.average(),
                    averagePulse = dayEntries.map { it.pulse }.average(),
                    count = dayEntries.size,
                    timestamp = date.atStartOfDay(zone).toInstant().toEpochMilli()
                )
            }
            .sortedBy { it.timestamp }
    }

    /**
     * Heti csoportosítás és átlagok
     */
    fun calculateWeeklyAverages(entries: List<BloodPressureEntry>): List<AggregatedAverage> {
        val locale = java.util.Locale.getDefault()
        return entries
            .groupBy { entry ->
                val date = entry.localDateTime.toLocalDate()
                "${date.year}-W${date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)}"
            }
            .map { (_, weekEntries) ->
                val representativeTimestamp = weekEntries.minOf { it.timestamp }
                val date = weekEntries.first().localDateTime.toLocalDate()
                val weekNum = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                val fullLabel = if (locale.language == "hu") "${date.year}. ${weekNum}. hét" else "Week $weekNum, ${date.year}"
                val shortLabel = if (locale.language == "hu") "${weekNum}. hét" else "W$weekNum"

                AggregatedAverage(
                    periodLabel = fullLabel,
                    shortLabel = shortLabel,
                    averageSystolic = weekEntries.map { it.systolic }.average(),
                    averageDiastolic = weekEntries.map { it.diastolic }.average(),
                    averagePulse = weekEntries.map { it.pulse }.average(),
                    count = weekEntries.size,
                    timestamp = representativeTimestamp
                )
            }
            .sortedBy { it.timestamp }
    }

    /**
     * Havi csoportosítás és átlagok
     */
    fun calculateMonthlyAverages(entries: List<BloodPressureEntry>): List<AggregatedAverage> {
        val locale = java.util.Locale.getDefault()
        val fullMonthFormatter = java.time.format.DateTimeFormatter.ofPattern(getLocalizedMonthPattern(locale), locale)
        val shortMonthFormatter = java.time.format.DateTimeFormatter.ofPattern("MMM", locale)

        return entries
            .groupBy { entry ->
                val date = entry.localDateTime.toLocalDate()
                "${date.year}-${String.format("%02d", date.monthValue)}"
            }
            .map { (_, monthEntries) ->
                val representativeTimestamp = monthEntries.minOf { it.timestamp }
                val date = monthEntries.first().localDateTime.toLocalDate()

                AggregatedAverage(
                    periodLabel = date.format(fullMonthFormatter),
                    shortLabel = date.format(shortMonthFormatter),
                    averageSystolic = monthEntries.map { it.systolic }.average(),
                    averageDiastolic = monthEntries.map { it.diastolic }.average(),
                    averagePulse = monthEntries.map { it.pulse }.average(),
                    count = monthEntries.size,
                    timestamp = representativeTimestamp
                )
            }
            .sortedBy { it.timestamp }
    }
}
