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
import com.kaszast.bpjournal.data.AppThemeMode
import com.kaszast.bpjournal.data.UserSettings
import com.kaszast.bpjournal.data.UserSettingsManager
import com.kaszast.bpjournal.model.Arm
import com.kaszast.bpjournal.model.BodyPosition
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
    private val healthConnectHelper: HealthConnectHelper,
    private val userSettingsManager: UserSettingsManager
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = userSettingsManager.settings

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

            if (userSettings.value.autoSyncHealthConnect) {
                val syncResult = healthConnectHelper.syncBloodPressureRecord(updatedEntry)
                _syncStatusMessage.value = "Szinkronizáció: $syncResult"
            }
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

    // Beállítások módosító függvényei
    fun setDefaultArm(arm: Arm) = userSettingsManager.setDefaultArm(arm)
    fun setDefaultPosition(position: BodyPosition) = userSettingsManager.setDefaultPosition(position)
    fun setAutoSync(enabled: Boolean) = userSettingsManager.setAutoSync(enabled)
    fun setMorningReminder(enabled: Boolean, time: String = userSettings.value.morningReminderTime) = userSettingsManager.setMorningReminder(enabled, time)
    fun setEveningReminder(enabled: Boolean, time: String = userSettings.value.eveningReminderTime) = userSettingsManager.setEveningReminder(enabled, time)
    fun setThemeMode(mode: AppThemeMode) = userSettingsManager.setThemeMode(mode)

    fun syncAllRecords() {
        viewModelScope.launch {
            val currentList = entries.value
            var successCount = 0
            for (entry in currentList) {
                val res = healthConnectHelper.syncBloodPressureRecord(entry)
                if (res is com.kaszast.bpjournal.health.HealthSyncResult.Success) {
                    successCount++
                }
            }
            _syncStatusMessage.value = "$successCount / ${currentList.size} mérés sikeresen szinkronizálva a Health Connect-be."
        }
    }

    fun addSampleData() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val dayMillis = 24L * 60 * 60 * 1000
            val samples = listOf(
                BloodPressureEntry(systolic = 118, diastolic = 78, pulse = 68, timestamp = now - 6 * dayMillis, notes = "Nyugodt reggel"),
                BloodPressureEntry(systolic = 124, diastolic = 82, pulse = 72, timestamp = now - 5 * dayMillis, notes = "Munka után"),
                BloodPressureEntry(systolic = 132, diastolic = 86, pulse = 76, timestamp = now - 4 * dayMillis, tags = setOf("Koffein"), notes = "Kávé után"),
                BloodPressureEntry(systolic = 128, diastolic = 84, pulse = 70, timestamp = now - 3 * dayMillis, tags = setOf("Nyugalmi")),
                BloodPressureEntry(systolic = 120, diastolic = 80, pulse = 71, timestamp = now - 2 * dayMillis, tags = setOf("Gyógyszer után")),
                BloodPressureEntry(systolic = 138, diastolic = 88, pulse = 80, timestamp = now - 1 * dayMillis, tags = setOf("Stressz")),
                BloodPressureEntry(systolic = 122, diastolic = 79, pulse = 69, timestamp = now, tags = setOf("Nyugalmi"), notes = "Mai mérés")
            )
            for (s in samples) {
                repository.insertEntry(s)
            }
        }
    }

    class Factory(
        private val repository: BloodPressureRepository,
        private val healthConnectHelper: HealthConnectHelper,
        private val userSettingsManager: UserSettingsManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BloodPressureViewModel(repository, healthConnectHelper, userSettingsManager) as T
        }
    }
}
