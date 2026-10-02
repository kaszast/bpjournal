package com.kaszast.bpjournal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaszast.bpjournal.model.BloodPressureCategory
import com.kaszast.bpjournal.ui.components.BloodPressureChart
import com.kaszast.bpjournal.ui.theme.CategoryGrade1
import com.kaszast.bpjournal.ui.theme.CategoryGrade2
import com.kaszast.bpjournal.ui.theme.CategoryGrade3
import com.kaszast.bpjournal.ui.theme.CategoryHighNormal
import com.kaszast.bpjournal.ui.theme.CategoryIsolated
import com.kaszast.bpjournal.ui.theme.CategoryNormal
import com.kaszast.bpjournal.ui.theme.CategoryOptimal
import com.kaszast.bpjournal.ui.theme.SlatePrimary
import com.kaszast.bpjournal.ui.viewmodel.BloodPressureViewModel
import com.kaszast.bpjournal.ui.viewmodel.PeriodMode

@Composable
fun StatisticsScreen(
    viewModel: BloodPressureViewModel
) {
    val stats by viewModel.summaryStats.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Fejléc
        item {
            Column {
                Text(
                    text = "Grafikonok és Átlagok",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlatePrimary
                )
                Text(
                    text = "Napi, heti és havi keringési dinamika",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Időszak választó (Napi / Heti / Havi)
        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = selectedPeriod == PeriodMode.DAILY,
                    onClick = { viewModel.setPeriodMode(PeriodMode.DAILY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) {
                    Text("Napi", fontSize = 13.sp)
                }
                SegmentedButton(
                    selected = selectedPeriod == PeriodMode.WEEKLY,
                    onClick = { viewModel.setPeriodMode(PeriodMode.WEEKLY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) {
                    Text("Heti", fontSize = 13.sp)
                }
                SegmentedButton(
                    selected = selectedPeriod == PeriodMode.MONTHLY,
                    onClick = { viewModel.setPeriodMode(PeriodMode.MONTHLY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) {
                    Text("Havi", fontSize = 13.sp)
                }
            }
        }

        // Fő grafikon
        item {
            BloodPressureChart(dataPoints = chartData)
        }

        // ESH Kategória megoszlás
        item {
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
                        text = "Orvosi Kategória Megoszlás (ESH)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (stats.totalCount == 0) {
                        Text(
                            text = "Nincs rögzített adat",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        CategoryBar(
                            name = "Optimális",
                            range = "<120/80",
                            color = CategoryOptimal,
                            count = stats.categoryDistribution[BloodPressureCategory.OPTIMAL] ?: 0,
                            total = stats.totalCount
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryBar(
                            name = "Normál",
                            range = "120-129 / 80-84",
                            color = CategoryNormal,
                            count = stats.categoryDistribution[BloodPressureCategory.NORMAL] ?: 0,
                            total = stats.totalCount
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryBar(
                            name = "Emelkedett",
                            range = "130-139 / 85-89",
                            color = CategoryHighNormal,
                            count = stats.categoryDistribution[BloodPressureCategory.HIGH_NORMAL] ?: 0,
                            total = stats.totalCount
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryBar(
                            name = "I. fokozat",
                            range = "140-159 / 90-99",
                            color = CategoryGrade1,
                            count = stats.categoryDistribution[BloodPressureCategory.GRADE_1_HYPERTENSION] ?: 0,
                            total = stats.totalCount
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryBar(
                            name = "II. fokozat",
                            range = "160-179 / 100-109",
                            color = CategoryGrade2,
                            count = stats.categoryDistribution[BloodPressureCategory.GRADE_2_HYPERTENSION] ?: 0,
                            total = stats.totalCount
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryBar(
                            name = "III. fokozat",
                            range = "≥180 / ≥110",
                            color = CategoryGrade3,
                            count = stats.categoryDistribution[BloodPressureCategory.GRADE_3_HYPERTENSION] ?: 0,
                            total = stats.totalCount
                        )
                    }
                }
            }
        }

        // Összesített időszaki átlagok listája kompakt egybefüggő kártyában
        item {
            Column {
                Text(
                    text = "Időszaki Részletezés",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (chartData.isEmpty()) {
                    Text(
                        text = "Nincs rögzített adat",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            val reversedList = chartData.reversed()
                            reversedList.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = item.periodLabel,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${item.count} mérés alapján",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "ST ${item.averageSystolic.toInt()} / DST ${item.averageDiastolic.toInt()}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            softWrap = false
                                        )
                                        Text(
                                            text = "Pulzus: ${item.averagePulse.toInt()} BPM",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (index < reversedList.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                        thickness = 0.8.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryBar(
    name: String,
    range: String,
    color: Color,
    count: Int,
    total: Int
) {
    val fraction = if (total > 0) count.toFloat() / total.toFloat() else 0f
    val percent = (fraction * 100).toInt()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = color, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "($range)",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$count db ($percent%)",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
