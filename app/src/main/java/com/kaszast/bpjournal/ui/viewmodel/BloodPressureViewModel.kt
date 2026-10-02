package com.kaszast.bpjournal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaszast.bpjournal.data.AggregatedAverage
import com.kaszast.bpjournal.data.BloodPressureRepository
import com.kaszast.bpjournal.data.BloodPressureStatisticsCalculator
import com.kaszast.bpjournal.data.SummaryStatistics
import com.kaszast.bpjournal.health.HealthConnectHelper
import com.kaszast.bpjournal.model.BloodPressureEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PeriodMode {
    DAILY,
    WEEKLY,
    MONTHLY
}

class BloodPressureViewModel(
    private val repository: BloodPressureRepository,
    private val healthConnectHelper: HealthConnectHelper
) : ViewModel() {

    val entries: StateFlow<List<BloodPressureEntry>> = repository.getAllEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val summaryStats: StateFlow<SummaryStatistics> = entries.map { list ->
        BloodPressureStatisticsCalculator.calculateSummary(list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BloodPressureStatisticsCalculator.calculateSummary(emptyList())
    )

    private val _selectedPeriod = MutableStateFlow(PeriodMode.DAILY)
    val selectedPeriod: StateFlow<PeriodMode> = _selectedPeriod.asStateFlow()

    val chartData: StateFlow<List<AggregatedAverage>> = entries.map { list ->
        when (_selectedPeriod.value) {
            PeriodMode.DAILY -> BloodPressureStatisticsCalculator.calculateDailyAverages(list)
            PeriodMode.WEEKLY -> BloodPressureStatisticsCalculator.calculateWeeklyAverages(list)
            PeriodMode.MONTHLY -> BloodPressureStatisticsCalculator.calculateMonthlyAverages(list)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _syncStatusMessage = MutableStateFlow<String?>(null)
    val syncStatusMessage: StateFlow<String?> = _syncStatusMessage.asStateFlow()

    fun setPeriodMode(mode: PeriodMode) {
        _selectedPeriod.value = mode
    }

    fun addEntry(entry: BloodPressureEntry) {
        viewModelScope.launch {
            val insertedId = repository.insertEntry(entry)
            val updatedEntry = entry.copy(id = insertedId)

            // Azonnali szinkronizáció a Health Connect / Google Fit tárolóba
            val syncResult = healthConnectHelper.syncBloodPressureRecord(updatedEntry)
            _syncStatusMessage.value = "Szinkronizáció: $syncResult"
        }
    }

    fun updateEntry(entry: BloodPressureEntry) {
        viewModelScope.launch {
            repository.updateEntry(entry)
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteEntry(id)
        }
    }

    fun clearSyncMessage() {
        _syncStatusMessage.value = null
    }

    class Factory(
        private val repository: BloodPressureRepository,
        private val healthConnectHelper: HealthConnectHelper
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BloodPressureViewModel(repository, healthConnectHelper) as T
        }
    }
}
