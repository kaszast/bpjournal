package com.kaszast.bpjournal.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaszast.bpjournal.R
import com.kaszast.bpjournal.data.AppThemeMode
import com.kaszast.bpjournal.model.Arm
import com.kaszast.bpjournal.model.BodyPosition
import com.kaszast.bpjournal.ui.theme.CategoryGrade1
import com.kaszast.bpjournal.ui.theme.CategoryGrade2
import com.kaszast.bpjournal.ui.theme.CategoryGrade3
import com.kaszast.bpjournal.ui.theme.CategoryHighNormal
import com.kaszast.bpjournal.ui.theme.CategoryNormal
import com.kaszast.bpjournal.ui.theme.CategoryOptimal
import com.kaszast.bpjournal.ui.theme.SlatePrimary
import com.kaszast.bpjournal.ui.theme.TealSecondary
import com.kaszast.bpjournal.ui.viewmodel.BloodPressureViewModel

@Composable
fun SettingsScreen(
    viewModel: BloodPressureViewModel
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val syncStatus by viewModel.syncStatusMessage.collectAsState()

    fun showTimePicker(currentTime: String, onTimePicked: (String) -> Unit) {
        val parts = currentTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        TimePickerDialog(
            context,
            { _, h, m ->
                val formatted = String.format("%02d:%02d", h, m)
                onTimePicked(formatted)
            },
            hour,
            minute,
            true
        ).show()
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
                text = stringResource(R.string.nav_settings),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SlatePrimary
            )
            Text(
                text = "Mérési alapértelmezések, szinkronizáció és megjelenés",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. Mérési Alapértelmezések Kártya
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
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Alapértékek",
                        tint = SlatePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mérési Alapértelmezések",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlatePrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Alapértelmezett kar
                Text(
                    text = "Alapértelmezett mérési kar új rögzítéskor:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = userSettings.defaultArm == Arm.LEFT,
                        onClick = { viewModel.setDefaultArm(Arm.LEFT) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(stringResource(R.string.arm_left), fontSize = 12.sp)
                    }
                    SegmentedButton(
                        selected = userSettings.defaultArm == Arm.RIGHT,
                        onClick = { viewModel.setDefaultArm(Arm.RIGHT) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(stringResource(R.string.arm_right), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Alapértelmezett testhelyzet
                Text(
                    text = "Alapértelmezett testhelyzet új rögzítéskor:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = userSettings.defaultPosition == BodyPosition.SITTING,
                        onClick = { viewModel.setDefaultPosition(BodyPosition.SITTING) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) {
                        Text(stringResource(R.string.position_sitting), fontSize = 12.sp)
                    }
                    SegmentedButton(
                        selected = userSettings.defaultPosition == BodyPosition.LYING,
                        onClick = { viewModel.setDefaultPosition(BodyPosition.LYING) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Text(stringResource(R.string.position_lying), fontSize = 12.sp)
                    }
                    SegmentedButton(
                        selected = userSettings.defaultPosition == BodyPosition.STANDING,
                        onClick = { viewModel.setDefaultPosition(BodyPosition.STANDING) },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) {
                        Text(stringResource(R.string.position_standing), fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. Health Connect és Google Fit Szinkronizáció Kártya
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
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync",
                        tint = TealSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.health_connect_title),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlatePrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Azonnali szinkronizáció a Health Connect / Google Fit tárolóba minden mentéskor.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Automatikus szinkronizálás kapcsoló
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Automatikus szinkronizálás",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (userSettings.autoSyncHealthConnect) "Aktív: mentéskor azonnal küldés" else "Kikapcsolva: csak helyi mentés",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = userSettings.autoSyncHealthConnect,
                        onCheckedChange = { viewModel.setAutoSync(it) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Összes adat szinkronizálása gomb
                OutlinedButton(
                    onClick = { viewModel.syncAllRecords() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Sync, contentDescription = "Szinkronizálás", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Összes korábbi mérés szinkronizálása most", fontSize = 12.sp)
                }

                if (syncStatus != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = syncStatus ?: "",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 3. Napi Mérési Emlékeztetők Kártya
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
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Emlékeztetők",
                        tint = SlatePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mérési Emlékeztetők",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlatePrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reggeli emlékeztető
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Reggeli mérés", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Időpont: ${userSettings.morningReminderTime}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            OutlinedButton(
                                onClick = {
                                    showTimePicker(userSettings.morningReminderTime) { newTime ->
                                        viewModel.setMorningReminder(userSettings.morningReminderEnabled, newTime)
                                    }
                                },
                                modifier = Modifier.height(28.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("Módosítás", fontSize = 10.sp)
                            }
                        }
                    }
                    Switch(
                        checked = userSettings.morningReminderEnabled,
                        onCheckedChange = { viewModel.setMorningReminder(it) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Esti emlékeztető
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Esti mérés", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Időpont: ${userSettings.eveningReminderTime}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            OutlinedButton(
                                onClick = {
                                    showTimePicker(userSettings.eveningReminderTime) { newTime ->
                                        viewModel.setEveningReminder(userSettings.eveningReminderEnabled, newTime)
                                    }
                                },
                                modifier = Modifier.height(28.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                            ) {
                                Text("Módosítás", fontSize = 10.sp)
                            }
                        }
                    }
                    Switch(
                        checked = userSettings.eveningReminderEnabled,
                        onCheckedChange = { viewModel.setEveningReminder(it) }
                    )
                }
            }
        }

        // 4. Megjelenés és Téma Kártya
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
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Téma",
                        tint = SlatePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Megjelenés és Téma",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlatePrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = userSettings.themeMode == AppThemeMode.SYSTEM,
                        onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) {
                        Text("Rendszer", fontSize = 11.sp)
                    }
                    SegmentedButton(
                        selected = userSettings.themeMode == AppThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Text("Világos", fontSize = 11.sp)
                    }
                    SegmentedButton(
                        selected = userSettings.themeMode == AppThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) {
                        Text("Sötét", fontSize = 11.sp)
                    }
                }
            }
        }

        // 5. Adatkezelés Kártya
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
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Adatkezelés",
                        tint = SlatePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Adatkezelés és Tesztelés",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlatePrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { viewModel.addSampleData() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("1 heti minta mérések betöltése", fontSize = 12.sp)
                }
            }
        }

        // 6. Orvosi ESH Referencia Kártya
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = SlatePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ESH / ESC Kategóriák",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlatePrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                ReferenceRow("Optimális", "< 120 Hgmm", "< 80 Hgmm", CategoryOptimal)
                ReferenceRow("Normál", "120 - 129 Hgmm", "80 - 84 Hgmm", CategoryNormal)
                ReferenceRow("Emelkedett normál", "130 - 139 Hgmm", "85 - 89 Hgmm", CategoryHighNormal)
                ReferenceRow("I. fokú hipertónia", "140 - 159 Hgmm", "90 - 99 Hgmm", CategoryGrade1)
                ReferenceRow("II. fokú hipertónia", "160 - 179 Hgmm", "100 - 109 Hgmm", CategoryGrade2)
                ReferenceRow("III. fokú hipertónia", "≥ 180 Hgmm", "≥ 110 Hgmm", CategoryGrade3)
            }
        }
    }
}

@Composable
private fun ReferenceRow(name: String, sys: String, dia: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
        Text(
            text = "$sys / $dia",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
