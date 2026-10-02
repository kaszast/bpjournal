package com.kaszast.bpjournal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaszast.bpjournal.R
import com.kaszast.bpjournal.model.BloodPressureEntry
import com.kaszast.bpjournal.ui.components.BloodPressureCard
import com.kaszast.bpjournal.ui.components.BloodPressureChart
import com.kaszast.bpjournal.ui.theme.SlatePrimary
import com.kaszast.bpjournal.ui.viewmodel.BloodPressureViewModel

@Composable
fun DashboardScreen(
    viewModel: BloodPressureViewModel,
    onNavigateToHistory: () -> Unit
) {
    val entries by viewModel.entries.collectAsState()
    val stats by viewModel.summaryStats.collectAsState()
    val chartData by viewModel.chartData.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.record_new)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fejléc
            item {
                Column {
                    Text(
                        text = "BPJournal",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlatePrimary
                    )
                    Text(
                        text = "Keringési és Vérnyomás Napló",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Statisztikai minikártyák sor
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardStatCard(
                        title = "Átlag Szisztolés",
                        value = if (stats.totalCount > 0) "${stats.avgSystolic.toInt()} Hgmm" else "--",
                        subtitle = "Cél: <120",
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "Átlag Diasztolés",
                        value = if (stats.totalCount > 0) "${stats.avgDiastolic.toInt()} Hgmm" else "--",
                        subtitle = "Cél: <80",
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "Átlag Pulzus",
                        value = if (stats.totalCount > 0) "${stats.avgPulse.toInt()} BPM" else "--",
                        subtitle = "${stats.totalCount} mérés",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Legutóbbi mérés
            val latest = entries.firstOrNull()
            if (latest != null) {
                item {
                    Text(
                        text = stringResource(R.string.latest_measurement),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BloodPressureCard(
                        entry = latest,
                        onDeleteClick = { viewModel.deleteEntry(latest.id) }
                    )
                }
            }

            // Trendgrafikon előnézet
            if (chartData.isNotEmpty()) {
                item {
                    Text(
                        text = "Trend és Dinamika",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BloodPressureChart(dataPoints = chartData.takeLast(14))
                }
            }

            // Friss mérések szekció
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Legutóbbi Rögzítések",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (entries.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.no_measurements),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(entries.take(5), key = { it.id }) { entry ->
                    BloodPressureCard(
                        entry = entry,
                        onDeleteClick = { viewModel.deleteEntry(entry.id) }
                    )
                }
            }
        }
    }

    val userSettings by viewModel.userSettings.collectAsState()

    if (showAddDialog) {
        AddEditEntryDialog(
            defaultArm = userSettings.defaultArm,
            defaultPosition = userSettings.defaultPosition,
            onDismiss = { showAddDialog = false },
            onSave = { entry ->
                viewModel.addEntry(entry)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun DashboardStatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SlatePrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
