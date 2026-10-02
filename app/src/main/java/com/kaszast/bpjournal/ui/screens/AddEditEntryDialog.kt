package com.kaszast.bpjournal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kaszast.bpjournal.R
import com.kaszast.bpjournal.model.Arm
import com.kaszast.bpjournal.model.BloodPressureEntry
import com.kaszast.bpjournal.model.BodyPosition
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditEntryDialog(
    initialEntry: BloodPressureEntry? = null,
    defaultArm: Arm = Arm.LEFT,
    defaultPosition: BodyPosition = BodyPosition.SITTING,
    onDismiss: () -> Unit,
    onSave: (BloodPressureEntry) -> Unit
) {
    val context = LocalContext.current
    var selectedTimestamp by remember { mutableLongStateOf(initialEntry?.timestamp ?: System.currentTimeMillis()) }
    var systolicText by remember { mutableStateOf(initialEntry?.systolic?.toString() ?: "120") }
    var diastolicText by remember { mutableStateOf(initialEntry?.diastolic?.toString() ?: "80") }
    var pulseText by remember { mutableStateOf(initialEntry?.pulse?.toString() ?: "72") }

    var selectedArm by remember { mutableStateOf(initialEntry?.arm ?: defaultArm) }
    var selectedPosition by remember { mutableStateOf(initialEntry?.position ?: defaultPosition) }
    var selectedTags by remember { mutableStateOf(initialEntry?.tags ?: setOf("Nyugalmi")) }
    var notesText by remember { mutableStateOf(initialEntry?.notes ?: "") }

    val availableTags = listOf(
        stringResource(R.string.tag_resting),
        stringResource(R.string.tag_post_medication),
        stringResource(R.string.tag_stress),
        stringResource(R.string.tag_caffeine),
        stringResource(R.string.tag_exercise)
    )

    val isInputValid = remember(systolicText, diastolicText, pulseText) {
        val sys = systolicText.toIntOrNull() ?: 0
        val dia = diastolicText.toIntOrNull() ?: 0
        val pulse = pulseText.toIntOrNull() ?: 0
        sys in 50..260 && dia in 30..180 && pulse in 30..220 && sys > dia
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialEntry == null) stringResource(R.string.record_new) else stringResource(R.string.edit),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dátum és Idő kiválasztása
                val calendar = remember(selectedTimestamp) {
                    Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
                }
                val formattedDateTime = remember(selectedTimestamp) {
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedTimestamp), ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
                }

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.date_and_time),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formattedDateTime,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = {
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            TimePickerDialog(
                                                context,
                                                { _, hourOfDay, minute ->
                                                    val newCal = Calendar.getInstance().apply {
                                                        set(Calendar.YEAR, year)
                                                        set(Calendar.MONTH, month)
                                                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                        set(Calendar.MINUTE, minute)
                                                        set(Calendar.SECOND, 0)
                                                    }
                                                    selectedTimestamp = newCal.timeInMillis
                                                },
                                                calendar.get(Calendar.HOUR_OF_DAY),
                                                calendar.get(Calendar.MINUTE),
                                                true
                                            ).show()
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                },
                                modifier = Modifier.height(36.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Módosítás",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Módosítás", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { selectedTimestamp = System.currentTimeMillis() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Update,
                                    contentDescription = "Most",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fő értékek bevitele (Szisztolés, Diasztolés)
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = systolicText,
                        onValueChange = { if (it.length <= 3) systolicText = it },
                        label = { Text(stringResource(R.string.systolic)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = diastolicText,
                        onValueChange = { if (it.length <= 3) diastolicText = it },
                        label = { Text(stringResource(R.string.diastolic)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pulzus bevitele
                OutlinedTextField(
                    value = pulseText,
                    onValueChange = { if (it.length <= 3) pulseText = it },
                    label = { Text(stringResource(R.string.pulse)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Kar kiválasztása
                Text(
                    text = stringResource(R.string.arm),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedArm == Arm.LEFT,
                        onClick = { selectedArm = Arm.LEFT },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(stringResource(R.string.arm_left))
                    }
                    SegmentedButton(
                        selected = selectedArm == Arm.RIGHT,
                        onClick = { selectedArm = Arm.RIGHT },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(stringResource(R.string.arm_right))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Testhelyzet kiválasztása
                Text(
                    text = stringResource(R.string.body_position),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedPosition == BodyPosition.SITTING,
                        onClick = { selectedPosition = BodyPosition.SITTING },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) {
                        Text(stringResource(R.string.position_sitting))
                    }
                    SegmentedButton(
                        selected = selectedPosition == BodyPosition.LYING,
                        onClick = { selectedPosition = BodyPosition.LYING },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Text(stringResource(R.string.position_lying))
                    }
                    SegmentedButton(
                        selected = selectedPosition == BodyPosition.STANDING,
                        onClick = { selectedPosition = BodyPosition.STANDING },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) {
                        Text(stringResource(R.string.position_standing))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Címkék kiválasztása
                Text(
                    text = stringResource(R.string.tags),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableTags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTags = if (isSelected) {
                                    selectedTags - tag
                                } else {
                                    selectedTags + tag
                                }
                            },
                            label = { Text(tag, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Megjegyzés
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text(stringResource(R.string.notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Gombok
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val entry = BloodPressureEntry(
                                id = initialEntry?.id ?: 0,
                                systolic = systolicText.toInt(),
                                diastolic = diastolicText.toInt(),
                                pulse = pulseText.toInt(),
                                timestamp = selectedTimestamp,
                                arm = selectedArm,
                                position = selectedPosition,
                                tags = selectedTags,
                                notes = notesText.trim()
                            )
                            onSave(entry)
                        },
                        enabled = isInputValid
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}
