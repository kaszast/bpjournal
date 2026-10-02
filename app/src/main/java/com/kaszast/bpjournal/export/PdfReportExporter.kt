package com.kaszast.bpjournal.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.kaszast.bpjournal.data.BloodPressureStatisticsCalculator
import com.kaszast.bpjournal.model.BloodPressureCategory
import com.kaszast.bpjournal.model.BloodPressureEntry
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PdfReportExporter {

    private const val PAGE_WIDTH = 595 // A4 width in points
    private const val PAGE_HEIGHT = 842 // A4 height in points
    private const val MARGIN = 36f

    fun exportToPdf(
        context: Context,
        entries: List<BloodPressureEntry>,
        reportTitle: String = "BPJournal - Vérnyomás Lelet",
        filename: String = "bpjournal_report.pdf"
    ): File {
        val pdfDocument = PdfDocument()
        val stats = BloodPressureStatisticsCalculator.calculateSummary(entries)

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint().apply {
            isAntiAlias = true
        }

        var currentY = MARGIN + 20f

        // 1. Fejléc szekció (Slate orvosi dizájn)
        paint.color = Color.rgb(30, 58, 95) // #1E3A5F - orvosi mélykék
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(reportTitle, MARGIN, currentY, paint)

        currentY += 16f
        paint.color = Color.rgb(71, 85, 105) // Slate szürke
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        val nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
        canvas.drawText("Készült: $nowStr | Összes mérés: ${stats.totalCount} db", MARGIN, currentY, paint)

        currentY += 15f
        paint.color = Color.rgb(203, 213, 225) // Vékony elválasztó vonal
        paint.strokeWidth = 1f
        canvas.drawLine(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY, paint)

        // 2. Statisztikai összefoglaló kártya
        currentY += 15f
        paint.color = Color.rgb(241, 245, 249) // Card background
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY + 70f, 6f, 6f, paint)

        paint.color = Color.rgb(30, 58, 95)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("ÖSSZEFOGLALÓ STATISZTIKA", MARGIN + 12f, currentY + 16f, paint)

        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(15, 23, 42)

        val col1X = MARGIN + 12f
        val col2X = MARGIN + 180f
        val col3X = MARGIN + 350f

        val statRow1Y = currentY + 34f
        val statRow2Y = currentY + 52f

        canvas.drawText("Átlag vérnyomás: ${String.format("%.1f", stats.avgSystolic)} / ${String.format("%.1f", stats.avgDiastolic)} Hgmm", col1X, statRow1Y, paint)
        canvas.drawText("Átlag pulzus: ${String.format("%.1f", stats.avgPulse)} BPM", col1X, statRow2Y, paint)

        canvas.drawText("Min / Max Szisztolés: ${stats.minSystolic} / ${stats.maxSystolic} Hgmm", col2X, statRow1Y, paint)
        canvas.drawText("Min / Max Diasztolés: ${stats.minDiastolic} / ${stats.maxDiastolic} Hgmm", col2X, statRow2Y, paint)

        canvas.drawText("Átlag PP (pulzusnyomás): ${String.format("%.1f", stats.avgPulsePressure)} Hgmm", col3X, statRow1Y, paint)
        canvas.drawText("Átlag MAP (középnyomás): ${String.format("%.1f", stats.avgMeanArterialPressure)} Hgmm", col3X, statRow2Y, paint)

        currentY += 85f

        // 3. Mérési Napló Táblázat Fejléc
        paint.color = Color.rgb(15, 118, 110) // #0F766E - Teal fejléc
        paint.style = Paint.Style.FILL
        canvas.drawRect(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY + 20f, paint)

        paint.color = Color.WHITE
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val headerY = currentY + 14f
        canvas.drawText("Időpont", MARGIN + 6f, headerY, paint)
        canvas.drawText("Vérnyomás (ST / DST Hgmm)", MARGIN + 85f, headerY, paint)
        canvas.drawText("Pulzus", MARGIN + 190f, headerY, paint)
        canvas.drawText("Kar / Poz.", MARGIN + 235f, headerY, paint)
        canvas.drawText("Kategória (ESH)", MARGIN + 300f, headerY, paint)
        canvas.drawText("Címkék / Megjegyzés", MARGIN + 400f, headerY, paint)

        currentY += 20f

        // 4. Mérési sorok renderelése
        val sortedEntries = entries.sortedByDescending { it.timestamp }
        paint.textSize = 8f
        paint.typeface = Typeface.DEFAULT

        for ((index, entry) in sortedEntries.withIndex()) {
            // Új oldal ellenőrzése
            if (currentY + 22f > PAGE_HEIGHT - MARGIN) {
                // Lábléc az előző oldalra
                drawFooter(canvas, pageNumber)
                pdfDocument.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                currentY = MARGIN + 20f

                // Ismételt fejléc új oldalon
                paint.color = Color.rgb(15, 118, 110)
                paint.style = Paint.Style.FILL
                canvas.drawRect(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY + 18f, paint)

                paint.color = Color.WHITE
                paint.textSize = 8.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val newHeaderY = currentY + 13f
                canvas.drawText("Időpont", MARGIN + 6f, newHeaderY, paint)
                canvas.drawText("Vérnyomás (Hgmm)", MARGIN + 90f, newHeaderY, paint)
                canvas.drawText("Pulzus", MARGIN + 190f, newHeaderY, paint)
                canvas.drawText("Kar / Poz.", MARGIN + 235f, newHeaderY, paint)
                canvas.drawText("Kategória (ESH)", MARGIN + 300f, newHeaderY, paint)
                canvas.drawText("Címkék / Megjegyzés", MARGIN + 400f, newHeaderY, paint)
                currentY += 18f
            }

            // Váltakozó háttér
            if (index % 2 == 1) {
                paint.color = Color.rgb(248, 250, 252)
                paint.style = Paint.Style.FILL
                canvas.drawRect(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY + 18f, paint)
            }

            // Vonal
            paint.color = Color.rgb(226, 232, 240)
            paint.strokeWidth = 0.5f
            canvas.drawLine(MARGIN, currentY + 18f, PAGE_WIDTH - MARGIN, currentY + 18f, paint)

            val textY = currentY + 13f
            paint.color = Color.rgb(15, 23, 42)
            paint.typeface = Typeface.DEFAULT

            val timeStr = entry.formattedDateTime()
            canvas.drawText(timeStr, MARGIN + 6f, textY, paint)

            // Vérnyomás érték kiemelten
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("${entry.systolic} / ${entry.diastolic}", MARGIN + 90f, textY, paint)

            paint.typeface = Typeface.DEFAULT
            canvas.drawText("${entry.pulse} BPM", MARGIN + 190f, textY, paint)
            canvas.drawText("${entry.arm.name.take(1)} / ${entry.position.name.take(3)}", MARGIN + 235f, textY, paint)

            // Kategória szöveg és szín
            paint.color = getCategoryColor(entry.category)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val catText = getCategoryHungarianName(entry.category)
            canvas.drawText(catText, MARGIN + 300f, textY, paint)

            // Címkék & Megjegyzés
            paint.color = Color.rgb(71, 85, 105)
            paint.typeface = Typeface.DEFAULT
            val notesCombined = (entry.tags.joinToString(", ") + if (entry.notes.isNotBlank()) " | " + entry.notes else "").take(28)
            canvas.drawText(notesCombined, MARGIN + 400f, textY, paint)

            currentY += 18f
        }

        drawFooter(canvas, pageNumber)
        pdfDocument.finishPage(page)

        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        val file = File(exportDir, filename)
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return file
    }

    private fun drawFooter(canvas: Canvas, pageNumber: Int) {
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = 8f
            color = Color.rgb(148, 163, 184)
            typeface = Typeface.DEFAULT
        }
        canvas.drawText("BPJournal - Oldal $pageNumber", PAGE_WIDTH / 2f - 40f, PAGE_HEIGHT - MARGIN + 10f, paint)
    }

    private fun getCategoryColor(category: BloodPressureCategory): Int {
        return when (category) {
            BloodPressureCategory.OPTIMAL -> Color.rgb(22, 163, 74)
            BloodPressureCategory.NORMAL -> Color.rgb(13, 148, 136)
            BloodPressureCategory.HIGH_NORMAL -> Color.rgb(217, 119, 6)
            BloodPressureCategory.GRADE_1_HYPERTENSION -> Color.rgb(234, 88, 12)
            BloodPressureCategory.GRADE_2_HYPERTENSION -> Color.rgb(220, 38, 38)
            BloodPressureCategory.GRADE_3_HYPERTENSION -> Color.rgb(153, 27, 27)
            BloodPressureCategory.ISOLATED_SYSTOLIC -> Color.rgb(194, 65, 12)
        }
    }

    private fun getCategoryHungarianName(category: BloodPressureCategory): String {
        return when (category) {
            BloodPressureCategory.OPTIMAL -> "Optimális"
            BloodPressureCategory.NORMAL -> "Normál"
            BloodPressureCategory.HIGH_NORMAL -> "Emelkedett"
            BloodPressureCategory.GRADE_1_HYPERTENSION -> "I. fokú hipertónia"
            BloodPressureCategory.GRADE_2_HYPERTENSION -> "II. fokú hipertónia"
            BloodPressureCategory.GRADE_3_HYPERTENSION -> "III. fokú hipertónia"
            BloodPressureCategory.ISOLATED_SYSTOLIC -> "Izolált szisztolés"
        }
    }
}
