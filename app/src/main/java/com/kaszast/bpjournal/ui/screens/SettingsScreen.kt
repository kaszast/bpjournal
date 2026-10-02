package com.kaszast.bpjournal.ui.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
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

/**
 * Settings tab screen managing user configurations, integrations, and customization.
 *
 * Configurable domains:
 * - Measurement defaults: pre-selected arm and body posture.
 * - Health Connect: permission checks, automatic background syncing toggle, manual full batch sync.
 * - Measurement reminders: discrete morning, midday (noon), and evening triggers with native [TimePickerDialog].
 * - Appearance & theme: system default, light, or dark mode.
 * - Language: dynamic in-app language switching between System Default, Hungarian (hu), and English (en).
 * - Clinical reference table: official ESH/ESC guideline thresholds for systolic and diastolic ranges.
 */
@Composable
fun SettingsScreen(
    viewModel: BloodPressureViewModel
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val syncStatus by viewModel.syncStatusMessage.collectAsState()
    val hasHealthPermissions by viewModel.hasHealthPermissions.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.refreshHealthPermissions()
        val granted = results.values.any { it }
        if (granted) {
            Toast.makeText(context, "Health Connect hozzáférés engedélyezve!", Toast.LENGTH_SHORT).show()
            viewModel.syncAllRecords()
        } else {
            Toast.makeText(context, "Health Connect engedély elutasítva.", Toast.LENGTH_LONG).show()
        }
    }

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
                text = stringResource(R.string.settings_subtitle),
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
                        text = stringResource(R.string.settings_measurement_defaults),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlatePrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Alapértelmezett kar
                Text(
                    text = stringResource(R.string.default_arm_desc),
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
                    text = stringResource(R.string.default_pos_desc),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync",
                            tint = TealSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Health Connect",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlatePrimary
                        )
                    }

                    if (hasHealthPermissions) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF10B981).copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.status_active), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF047857))
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFF59E0B).copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.status_permission_needed), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFB45309))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.health_connect_desc),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!hasHealthPermissions) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            permissionLauncher.launch(viewModel.healthPermissions)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Health Connect engedély megadása", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = {
                            try {
                                context.startActivity(viewModel.getManagePermissionsIntent())
                            } catch (e: Exception) {
                                Toast.makeText(context, "Nem sikerült megnyitni a beállításokat", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Health Connect beállítások megnyitása", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Automatikus szinkronizálás kapcsoló
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_auto_sync),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (userSettings.autoSyncHealthConnect) stringResource(R.string.settings_auto_sync_desc) else "Csak helyi mentés",
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
                    onClick = {
                        if (!hasHealthPermissions) {
                            permissionLauncher.launch(viewModel.healthPermissions)
                        } else {
                            viewModel.syncAllRecords()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Sync, contentDescription = "Szinkronizálás", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.settings_sync_all_button), fontSize = 12.sp)
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
                        text = stringResource(R.string.reminders_title),
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.reminder_morning), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = userSettings.morningReminderTime, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = SlatePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    showTimePicker(userSettings.morningReminderTime) { newTime ->
                                        viewModel.setMorningReminder(userSettings.morningReminderEnabled, newTime)
                                    }
                                },
                                modifier = Modifier.height(26.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(stringResource(R.string.modify), fontSize = 10.sp)
                            }
                        }
                    }
                    Switch(
                        checked = userSettings.morningReminderEnabled,
                        onCheckedChange = { viewModel.setMorningReminder(it) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Déli emlékeztető
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.reminder_noon), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = userSettings.noonReminderTime, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = SlatePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    showTimePicker(userSettings.noonReminderTime) { newTime ->
                                        viewModel.setNoonReminder(userSettings.noonReminderEnabled, newTime)
                                    }
                                },
                                modifier = Modifier.height(26.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(stringResource(R.string.modify), fontSize = 10.sp)
                            }
                        }
                    }
                    Switch(
                        checked = userSettings.noonReminderEnabled,
                        onCheckedChange = { viewModel.setNoonReminder(it) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Esti emlékeztető
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.reminder_evening), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = userSettings.eveningReminderTime, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = SlatePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    showTimePicker(userSettings.eveningReminderTime) { newTime ->
                                        viewModel.setEveningReminder(userSettings.eveningReminderEnabled, newTime)
                                    }
                                },
                                modifier = Modifier.height(26.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(stringResource(R.string.modify), fontSize = 10.sp)
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
                        text = stringResource(R.string.settings_appearance),
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
                        Text(stringResource(R.string.theme_system), fontSize = 11.sp)
                    }
                    SegmentedButton(
                        selected = userSettings.themeMode == AppThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Text(stringResource(R.string.theme_light), fontSize = 11.sp)
                    }
                    SegmentedButton(
                        selected = userSettings.themeMode == AppThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) {
                        Text(stringResource(R.string.theme_dark), fontSize = 11.sp)
                    }
                }
            }
        }

        // 5. Nyelvválasztó Kártya
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
                        imageVector = Icons.Default.Language,
                        contentDescription = "Nyelv",
                        tint = SlatePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.settings_language),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlatePrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = userSettings.appLanguage == "system",
                        onClick = { viewModel.setAppLanguage("system") },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) {
                        Text(stringResource(R.string.language_system), fontSize = 11.sp)
                    }
                    SegmentedButton(
                        selected = userSettings.appLanguage == "hu",
                        onClick = { viewModel.setAppLanguage("hu") },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Text(stringResource(R.string.language_hu), fontSize = 11.sp)
                    }
                    SegmentedButton(
                        selected = userSettings.appLanguage == "en",
                        onClick = { viewModel.setAppLanguage("en") },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) {
                        Text(stringResource(R.string.language_en), fontSize = 11.sp)
                    }
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
                        text = "ESH / ESC Határértékek (Hgmm)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlatePrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fejléc
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kategória", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.5f))
                    Text("Szisztolés", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.1f))
                    Text("Diasztolés", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.1f))
                }

                Spacer(modifier = Modifier.height(6.dp))

                ReferenceRow("Optimális", "< 120", "< 80", CategoryOptimal)
                ReferenceRow("Normál", "120 - 129", "80 - 84", CategoryNormal)
                ReferenceRow("Emelkedett", "130 - 139", "85 - 89", CategoryHighNormal)
                ReferenceRow("I. fokozat", "140 - 159", "90 - 99", CategoryGrade1)
                ReferenceRow("II. fokozat", "160 - 179", "100 - 109", CategoryGrade2)
                ReferenceRow("III. fokozat", "≥ 180", "≥ 110", CategoryGrade3)
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
        Text(
            text = sys,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.1f)
        )
        Text(
            text = dia,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.1f)
        )
    }
}
