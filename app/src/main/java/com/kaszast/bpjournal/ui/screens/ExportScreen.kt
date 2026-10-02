package com.kaszast.bpjournal.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.kaszast.bpjournal.R
import com.kaszast.bpjournal.export.CsvExporter
import com.kaszast.bpjournal.export.PdfReportExporter
import com.kaszast.bpjournal.model.BloodPressureEntry
import com.kaszast.bpjournal.ui.theme.SlatePrimary
import com.kaszast.bpjournal.ui.theme.TealSecondary
import com.kaszast.bpjournal.ui.viewmodel.BloodPressureViewModel
import java.io.File

enum class ExportRange {
    ALL,
    DAYS_30,
    DAYS_90
}

@Composable
fun ExportScreen(
    viewModel: BloodPressureViewModel
) {
    val context = LocalContext.current
    val allEntries by viewModel.entries.collectAsState()
    var selectedRange by remember { mutableStateOf(ExportRange.ALL) }

    val filteredEntries = remember(allEntries, selectedRange) {
        val now = System.currentTimeMillis()
        when (selectedRange) {
            ExportRange.ALL -> allEntries
            ExportRange.DAYS_30 -> allEntries.filter { it.timestamp >= now - (30L * 24 * 60 * 60 * 1000) }
            ExportRange.DAYS_90 -> allEntries.filter { it.timestamp >= now - (90L * 24 * 60 * 60 * 1000) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Fejléc
        Column {
            Text(
                text = stringResource(R.string.export_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SlatePrimary
            )
            Text(
                text = stringResource(R.string.export_subtitle),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Időszak választó
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.export_period_title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedRange == ExportRange.ALL,
                        onClick = { selectedRange = ExportRange.ALL },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) {
                        Text(stringResource(R.string.export_range_all_short), fontSize = 12.sp)
                    }
                    SegmentedButton(
                        selected = selectedRange == ExportRange.DAYS_30,
                        onClick = { selectedRange = ExportRange.DAYS_30 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Text(stringResource(R.string.export_range_30d_short), fontSize = 12.sp)
                    }
                    SegmentedButton(
                        selected = selectedRange == ExportRange.DAYS_90,
                        onClick = { selectedRange = ExportRange.DAYS_90 },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) {
                        Text(stringResource(R.string.export_range_90d_short), fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.export_selected_count, filteredEntries.size),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // PDF Kártya
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.export_pdf_card_title),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlatePrimary
                        )
                        Text(
                            text = stringResource(R.string.export_pdf_card_desc),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (filteredEntries.isNotEmpty()) {
                            val pdfFile = PdfReportExporter.exportToPdf(
                                context = context,
                                entries = filteredEntries,
                                reportTitle = "BPJournal - Orvosi Vérnyomás Lelet"
                            )
                            shareFile(context, pdfFile, "application/pdf", "BPJournal Orvosi Lelet")
                        } else {
                            Toast.makeText(context, context.getString(R.string.no_export_entries_toast), Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                    enabled = filteredEntries.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Megosztás", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.export_pdf_button), fontSize = 13.sp)
                }
            }
        }

        // CSV Kártya
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "CSV",
                        tint = TealSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.export_csv_card_title),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlatePrimary
                        )
                        Text(
                            text = stringResource(R.string.export_csv_card_desc),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        if (filteredEntries.isNotEmpty()) {
                            val csvFile = CsvExporter.exportToFile(
                                context = context,
                                entries = filteredEntries
                            )
                            shareFile(context, csvFile, "text/csv", "BPJournal CSV Export")
                        } else {
                            Toast.makeText(context, "Nincs exportálható mérés az időszakban", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = filteredEntries.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Megosztás", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.export_csv_button), fontSize = 13.sp)
                }
            }
        }
    }
}

private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, title)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, title))
}
