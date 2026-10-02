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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaszast.bpjournal.R
import com.kaszast.bpjournal.model.BloodPressureEntry
import com.kaszast.bpjournal.ui.components.BloodPressureCard
import com.kaszast.bpjournal.ui.viewmodel.BloodPressureViewModel

@Composable
fun HistoryScreen(
    viewModel: BloodPressureViewModel
) {
    val entries by viewModel.entries.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var editingEntry by remember { mutableStateOf<BloodPressureEntry?>(null) }
    var entryToDelete by remember { mutableStateOf<BloodPressureEntry?>(null) }

    val filteredEntries = remember(entries, searchQuery) {
        if (searchQuery.isBlank()) {
            entries
        } else {
            val q = searchQuery.lowercase()
            entries.filter { entry ->
                entry.notes.lowercase().contains(q) ||
                        entry.tags.any { it.lowercase().contains(q) } ||
                        entry.category.name.lowercase().contains(q) ||
                        "${entry.systolic}/${entry.diastolic}".contains(q)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fejléc és Kereső
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.nav_history),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${filteredEntries.size} mérés",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Keresés...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Keresés", modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Törlés", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        if (filteredEntries.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (searchQuery.isBlank()) stringResource(R.string.no_measurements) else "Nincs találat a keresésre.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredEntries, key = { it.id }) { entry ->
                    BloodPressureCard(
                        entry = entry,
                        onEditClick = { editingEntry = entry },
                        onDeleteClick = { entryToDelete = entry }
                    )
                }
            }
        }
    }

    val userSettings by viewModel.userSettings.collectAsState()

    editingEntry?.let { entryToEdit ->
        AddEditEntryDialog(
            initialEntry = entryToEdit,
            defaultArm = userSettings.defaultArm,
            defaultPosition = userSettings.defaultPosition,
            onDismiss = { editingEntry = null },
            onSave = { updated ->
                viewModel.updateEntry(updated)
                editingEntry = null
            }
        )
    }

    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = {
                Text(
                    text = "Mérés törlése",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Biztosan törölni szeretné ezt a mérést?\n\n${entry.formattedDateTime()} • ST ${entry.systolic} / DST ${entry.diastolic} Hgmm (Pulzus: ${entry.pulse} BPM)",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEntry(entry.id)
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { entryToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
