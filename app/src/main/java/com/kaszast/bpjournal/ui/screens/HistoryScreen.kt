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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.kaszast.bpjournal.ui.theme.SlatePrimary
import com.kaszast.bpjournal.ui.viewmodel.BloodPressureViewModel

@Composable
fun HistoryScreen(
    viewModel: BloodPressureViewModel
) {
    val entries by viewModel.entries.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var editingEntry by remember { mutableStateOf<BloodPressureEntry?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.nav_history),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlatePrimary
                )
                Text(
                    text = "${filteredEntries.size} rögzített mérés",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Keresés (címke, megjegyzés, érték)...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Keresés")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Törlés")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredEntries, key = { it.id }) { entry ->
                        BloodPressureCard(
                            entry = entry,
                            onEditClick = { editingEntry = entry },
                            onDeleteClick = { viewModel.deleteEntry(entry.id) }
                        )
                    }
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

    editingEntry?.let { entryToEdit ->
        AddEditEntryDialog(
            initialEntry = entryToEdit,
            onDismiss = { editingEntry = null },
            onSave = { updated ->
                viewModel.updateEntry(updated)
                editingEntry = null
            }
        )
    }
}
