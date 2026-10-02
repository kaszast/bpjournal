package com.kaszast.bpjournal.export

import com.kaszast.bpjournal.model.Arm
import com.kaszast.bpjournal.model.BloodPressureEntry
import com.kaszast.bpjournal.model.BodyPosition
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvExporterTest {

    @Test
    fun testCsvStringGeneration() {
        val entry = BloodPressureEntry(
            id = 1,
            systolic = 120,
            diastolic = 80,
            pulse = 70,
            timestamp = 1790935200000L,
            arm = Arm.LEFT,
            position = BodyPosition.SITTING,
            tags = setOf("Nyugalmi"),
            notes = "Reggeli mérés"
        )

        val csv = CsvExporter.generateCsvString(listOf(entry))

        assertTrue(csv.contains("ID,Timestamp,Date,Time,Systolic_mmHg,Diastolic_mmHg"))
        assertTrue(csv.contains("1,1790935200000"))
        assertTrue(csv.contains("120,80,70,40,93"))
        assertTrue(csv.contains("LEFT,SITTING"))
        assertTrue(csv.contains("\"Nyugalmi\""))
        assertTrue(csv.contains("\"Reggeli mérés\""))
    }
}
