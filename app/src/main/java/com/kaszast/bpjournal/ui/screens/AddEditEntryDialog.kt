package com.kaszast.bpjournal.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kaszast.bpjournal.R
import com.kaszast.bpjournal.model.Arm
import com.kaszast.bpjournal.model.BloodPressureEntry
import com.kaszast.bpjournal.model.BodyPosition
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlinx.coroutines.launch

/**
 * Dialog for creating or modifying blood pressure measurements.
 *
 * Optimized for ergonomics:
 * - Fitted into a single screen viewport without vertical scroll barriers.
 * - Employs smooth wheel pickers ([WheelPicker]) for Systolic (ST), Diastolic (DST), and Pulse (BPM).
 * - Interactive timestamp chip opening native [DatePickerDialog] and [TimePickerDialog].
 * - Segmented pickers for arm and posture; multi-select chips for clinical context tags.
 */
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

    var systolicValue by remember { mutableIntStateOf(initialEntry?.systolic ?: 120) }
    var diastolicValue by remember { mutableIntStateOf(initialEntry?.diastolic ?: 80) }
    var pulseValue by remember { mutableIntStateOf(initialEntry?.pulse ?: 72) }

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

    val calendar = remember(selectedTimestamp) {
        Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
    }
    val formattedDateTime = remember(selectedTimestamp) {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedTimestamp), ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("MM.dd HH:mm", java.util.Locale.getDefault()))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // 1. Fejléc: Cím és kompakt Dátum/Idő választó
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialEntry == null) stringResource(R.string.record_new) else stringResource(R.string.edit),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
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
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Időpont módosítása",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formattedDateTime,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = { selectedTimestamp = System.currentTimeMillis() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Update,
                                contentDescription = "Most",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Háromoszlopos Görgethető Választó (Wheel Pickers)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumberWheelPicker(
                        range = 70..230,
                        value = systolicValue,
                        onValueChange = { systolicValue = it },
                        label = "ST",
                        unit = "Hgmm",
                        modifier = Modifier.weight(1f)
                    )

                    NumberWheelPicker(
                        range = 40..150,
                        value = diastolicValue,
                        onValueChange = { diastolicValue = it },
                        label = "DST",
                        unit = "Hgmm",
                        modifier = Modifier.weight(1f)
                    )

                    NumberWheelPicker(
                        range = 40..180,
                        value = pulseValue,
                        onValueChange = { pulseValue = it },
                        label = "Pulzus",
                        unit = "BPM",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Kar választó
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.arm),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.width(180.dp)) {
                        SegmentedButton(
                            selected = selectedArm == Arm.LEFT,
                            onClick = { selectedArm = Arm.LEFT },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Text("Bal", fontSize = 11.sp)
                        }
                        SegmentedButton(
                            selected = selectedArm == Arm.RIGHT,
                            onClick = { selectedArm = Arm.RIGHT },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Text("Jobb", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 4. Testhelyzet választó
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.body_position),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.width(220.dp)) {
                        SegmentedButton(
                            selected = selectedPosition == BodyPosition.SITTING,
                            onClick = { selectedPosition = BodyPosition.SITTING },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                        ) {
                            Text("Ülő", fontSize = 11.sp)
                        }
                        SegmentedButton(
                            selected = selectedPosition == BodyPosition.LYING,
                            onClick = { selectedPosition = BodyPosition.LYING },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                        ) {
                            Text("Fekvő", fontSize = 11.sp)
                        }
                        SegmentedButton(
                            selected = selectedPosition == BodyPosition.STANDING,
                            onClick = { selectedPosition = BodyPosition.STANDING },
                            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                        ) {
                            Text("Álló", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 4. Címkék (Vízszintesen görgethető kompakt lista)
                Text(
                    text = stringResource(R.string.tags),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(3.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableTags) { tag ->
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
                            label = { Text(tag, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 5. Megjegyzés mező (egysoros, kompakt)
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    placeholder = { Text("Megjegyzés (opcionális)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 6. Mégse / Mentés gombok
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val entry = BloodPressureEntry(
                                id = initialEntry?.id ?: 0,
                                systolic = systolicValue,
                                diastolic = diastolicValue,
                                pulse = pulseValue,
                                timestamp = selectedTimestamp,
                                arm = selectedArm,
                                position = selectedPosition,
                                tags = selectedTags,
                                notes = notesText.trim()
                            )
                            onSave(entry)
                        },
                        enabled = systolicValue > diastolicValue
                    ) {
                        Text(stringResource(R.string.save), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * Reusable snap-fling scrollable wheel picker for selecting numeric values within an integer range.
 *
 * Employs [rememberSnapFlingBehavior] with a [LazyColumn] to snap smoothly to each number.
 * Displays a center highlight band and scales the selected item text.
 *
 * @param range The selectable range of numbers (e.g. 60..260).
 * @param value Currently selected integer value.
 * @param onValueChange Callback invoked when a new number snaps into focus.
 * @param label Metric abbreviation header (ST, DST, Pulzus).
 * @param unit Unit label (Hgmm, BPM).
 */
@Composable
private fun NumberWheelPicker(
    range: IntRange,
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val values = remember(range) { range.toList() }
    val initialIndex = remember { (value - range.first).coerceIn(0, values.size - 1) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val itemHeight = 36.dp

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val snappedIndex = listState.firstVisibleItemIndex.coerceIn(0, values.size - 1)
            val snappedVal = values[snappedIndex]
            if (snappedVal != value) {
                onValueChange(snappedVal)
            }
        }
    }

    LaunchedEffect(value) {
        val targetIdx = (value - range.first).coerceIn(0, values.size - 1)
        if (listState.firstVisibleItemIndex != targetIdx && !listState.isScrollInProgress) {
            listState.animateScrollToItem(targetIdx)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "($unit)",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * 3)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Középső kijelölő sáv
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )

            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                contentPadding = PaddingValues(vertical = itemHeight),
                modifier = Modifier.fillMaxSize()
            ) {
                items(values.size) { index ->
                    val num = values[index]
                    val isSelected = listState.firstVisibleItemIndex == index
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .clickable {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$num",
                            fontSize = if (isSelected) 20.sp else 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                        )
                    }
                }
            }
        }
    }
}
