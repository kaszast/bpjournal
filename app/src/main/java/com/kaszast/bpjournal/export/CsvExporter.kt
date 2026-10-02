package com.kaszast.bpjournal.export

import android.content.Context
import com.kaszast.bpjournal.model.BloodPressureEntry
import java.io.File
import java.io.FileWriter
import java.time.format.DateTimeFormatter

object CsvExporter {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    fun generateCsvString(entries: List<BloodPressureEntry>): String {
        val sb = StringBuilder()
        sb.append("ID,Timestamp,Date,Time,Systolic_mmHg,Diastolic_mmHg,Pulse_BPM,PulsePressure_mmHg,MAP_mmHg,Category,Arm,Position,Tags,Notes\n")

        for (entry in entries.sortedBy { it.timestamp }) {
            val dateStr = entry.localDateTime.format(dateFormatter)
            val timeStr = entry.localDateTime.format(timeFormatter)
            val escapedNotes = "\"" + entry.notes.replace("\"", "\"\"") + "\""
            val tagsStr = "\"" + entry.tags.joinToString(";") + "\""

            sb.append("${entry.id},")
                .append("${entry.timestamp},")
                .append("$dateStr,")
                .append("$timeStr,")
                .append("${entry.systolic},")
                .append("${entry.diastolic},")
                .append("${entry.pulse},")
                .append("${entry.pulsePressure},")
                .append("${entry.meanArterialPressure},")
                .append("${entry.category.name},")
                .append("${entry.arm.name},")
                .append("${entry.position.name},")
                .append("$tagsStr,")
                .append("$escapedNotes\n")
        }

        return sb.toString()
    }

    fun exportToFile(context: Context, entries: List<BloodPressureEntry>, filename: String = "bpjournal_export.csv"): File {
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        val file = File(exportDir, filename)
        FileWriter(file).use { writer ->
            writer.write(generateCsvString(entries))
        }
        return file
    }
}
