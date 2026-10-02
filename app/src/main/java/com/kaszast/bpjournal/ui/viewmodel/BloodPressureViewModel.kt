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

/**
 * Aggregation time intervals for trend charts and statistics.
 */
enum class PeriodMode {
    DAILY,
    WEEKLY,
    MONTHLY
}

/**
 * Main ViewModel orchestrating the business logic, database operations,
 * Health Connect background synchronization, and reactive UI states.
 *
 * Designed using Clean Architecture & MVVM principles:
 * - Exposes UI state via immutable [StateFlow] streams.
 * - Handles coroutine scopes tied to the lifecycle ([viewModelScope]).
 * - Delegates low-level persistence to [BloodPressureRepository] and [UserSettingsManager].
 */
class BloodPressureViewModel(
    private val repository: BloodPressureRepository,
    private val healthConnectHelper: HealthConnectHelper,
    private val userSettingsManager: UserSettingsManager
) : ViewModel() {

    /** Observable flow of user preferences. */
    val userSettings: StateFlow<UserSettings> = userSettingsManager.settings

    /**
     * Hot stream of all recorded blood pressure entries sorted by timestamp descending.
     * Retains state in scope with a 5000ms subscription timeout to survive configuration changes.
     */
    val entries: StateFlow<List<BloodPressureEntry>> = repository.getAllEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Pre-populate with sample clinical records on first launch if database is clean
        viewModelScope.launch {
            val existing = repository.getAllEntriesSync()
            if (existing.isEmpty()) {
                addSampleData()
            }
        }
    }

    /**
     * Computed summary statistics (averages, min/max, distribution counts) derived from [entries].
     */
    val summaryStats: StateFlow<SummaryStatistics> = entries.map { list ->
        BloodPressureStatisticsCalculator.calculateSummary(list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BloodPressureStatisticsCalculator.calculateSummary(emptyList())
    )

    private val _selectedPeriod = MutableStateFlow(PeriodMode.DAILY)
    val selectedPeriod: StateFlow<PeriodMode> = _selectedPeriod.asStateFlow()

    /**
     * Aggregated averages mapped dynamically based on the active [PeriodMode] filter.
     */
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

    /** Switches the aggregation window (Daily, Weekly, Monthly). */
    fun setPeriodMode(mode: PeriodMode) {
        _selectedPeriod.value = mode
    }

    /**
     * Inserts a new blood pressure entry into SQLite and optionally synchronizes with Health Connect.
     */
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

    /** Updates an existing measurement record. */
    fun updateEntry(entry: BloodPressureEntry) {
        viewModelScope.launch {
            repository.updateEntry(entry)
        }
    }

    /** Deletes a measurement record by its unique database identifier. */
    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteEntry(id)
        }
    }

    /** Wipes all measurement records from the SQLite database. */
    fun deleteAllEntries() {
        viewModelScope.launch {
            repository.deleteAllEntries()
        }
    }

    /** Clears transient sync feedback message. */
    fun clearSyncMessage() {
        _syncStatusMessage.value = null
    }

    // --- Delegation to UserSettingsManager ---
    fun setDefaultArm(arm: Arm) = userSettingsManager.setDefaultArm(arm)
    fun setDefaultPosition(position: BodyPosition) = userSettingsManager.setDefaultPosition(position)
    fun setAutoSync(enabled: Boolean) = userSettingsManager.setAutoSync(enabled)
    fun setMorningReminder(enabled: Boolean, time: String = userSettings.value.morningReminderTime) = userSettingsManager.setMorningReminder(enabled, time)
    fun setNoonReminder(enabled: Boolean, time: String = userSettings.value.noonReminderTime) = userSettingsManager.setNoonReminder(enabled, time)
    fun setEveningReminder(enabled: Boolean, time: String = userSettings.value.eveningReminderTime) = userSettingsManager.setEveningReminder(enabled, time)
    fun setThemeMode(mode: AppThemeMode) = userSettingsManager.setThemeMode(mode)
    fun setAppLanguage(language: String) = userSettingsManager.setAppLanguage(language)

    // --- Health Connect Permission and Sync Operations ---
    val healthPermissions: Array<String> get() = healthConnectHelper.healthPermissions

    private val _hasHealthPermissions = MutableStateFlow(healthConnectHelper.hasPermissions())
    val hasHealthPermissions = _hasHealthPermissions.asStateFlow()

    fun refreshHealthPermissions() {
        _hasHealthPermissions.value = healthConnectHelper.hasPermissions()
    }

    fun getManagePermissionsIntent() = healthConnectHelper.getManagePermissionsIntent()

    /**
     * Batch synchronizes all local records to Google Health Connect.
     */
    fun syncAllRecords() {
        refreshHealthPermissions()
        viewModelScope.launch {
            val currentList = entries.value
            var successCount = 0
            var lastErrorMsg: String? = null
            for (entry in currentList) {
                when (val res = healthConnectHelper.syncBloodPressureRecord(entry)) {
                    is com.kaszast.bpjournal.health.HealthSyncResult.Success -> successCount++
                    is com.kaszast.bpjournal.health.HealthSyncResult.PermissionRequired -> {
                        lastErrorMsg = "Hiányzik a Health Connect írási engedély! Kérjük, adja meg az engedélyt a gombra kattintva."
                    }
                    is com.kaszast.bpjournal.health.HealthSyncResult.Error -> {
                        lastErrorMsg = "Hiba: ${res.message}"
                    }
                    is com.kaszast.bpjournal.health.HealthSyncResult.NotSupported -> {
                        lastErrorMsg = "A Health Connect nem támogatott ezen az Android verzión."
                    }
                }
            }
            if (lastErrorMsg != null && successCount == 0) {
                _syncStatusMessage.value = lastErrorMsg
            } else {
                _syncStatusMessage.value = "$successCount / ${currentList.size} mérés sikeresen szinkronizálva a Health Connect-be."
            }
        }
    }

    /**
     * Populates 7 sample historical measurements covering the previous week.
     */
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

    /** Factory for injecting repository and helper dependencies into [BloodPressureViewModel]. */
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
