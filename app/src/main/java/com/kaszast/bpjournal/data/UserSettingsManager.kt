package com.kaszast.bpjournal.data

import android.content.Context
import android.content.SharedPreferences
import com.kaszast.bpjournal.model.Arm
import com.kaszast.bpjournal.model.BodyPosition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserSettings(
    val defaultArm: Arm = Arm.LEFT,
    val defaultPosition: BodyPosition = BodyPosition.SITTING,
    val autoSyncHealthConnect: Boolean = true,
    val morningReminderEnabled: Boolean = false,
    val morningReminderTime: String = "08:00",
    val noonReminderEnabled: Boolean = false,
    val noonReminderTime: String = "12:00",
    val eveningReminderEnabled: Boolean = false,
    val eveningReminderTime: String = "20:00",
    val themeMode: AppThemeMode = AppThemeMode.LIGHT,
    val appLanguage: String = "system"
)

class UserSettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("bpjournal_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        val armStr = prefs.getString(KEY_DEFAULT_ARM, Arm.LEFT.name) ?: Arm.LEFT.name
        val posStr = prefs.getString(KEY_DEFAULT_POS, BodyPosition.SITTING.name) ?: BodyPosition.SITTING.name
        val themeStr = prefs.getString(KEY_THEME_MODE, AppThemeMode.LIGHT.name) ?: AppThemeMode.LIGHT.name
        val langStr = prefs.getString(KEY_APP_LANGUAGE, "system") ?: "system"

        return UserSettings(
            defaultArm = runCatching { Arm.valueOf(armStr) }.getOrDefault(Arm.LEFT),
            defaultPosition = runCatching { BodyPosition.valueOf(posStr) }.getOrDefault(BodyPosition.SITTING),
            autoSyncHealthConnect = prefs.getBoolean(KEY_AUTO_SYNC, true),
            morningReminderEnabled = prefs.getBoolean(KEY_REMINDER_MORNING_ENABLED, false),
            morningReminderTime = prefs.getString(KEY_REMINDER_MORNING_TIME, "08:00") ?: "08:00",
            noonReminderEnabled = prefs.getBoolean(KEY_REMINDER_NOON_ENABLED, false),
            noonReminderTime = prefs.getString(KEY_REMINDER_NOON_TIME, "12:00") ?: "12:00",
            eveningReminderEnabled = prefs.getBoolean(KEY_REMINDER_EVENING_ENABLED, false),
            eveningReminderTime = prefs.getString(KEY_REMINDER_EVENING_TIME, "20:00") ?: "20:00",
            themeMode = runCatching { AppThemeMode.valueOf(themeStr) }.getOrDefault(AppThemeMode.LIGHT),
            appLanguage = langStr
        )
    }

    fun setDefaultArm(arm: Arm) {
        prefs.edit().putString(KEY_DEFAULT_ARM, arm.name).apply()
        _settings.value = _settings.value.copy(defaultArm = arm)
    }

    fun setDefaultPosition(position: BodyPosition) {
        prefs.edit().putString(KEY_DEFAULT_POS, position.name).apply()
        _settings.value = _settings.value.copy(defaultPosition = position)
    }

    fun setAutoSync(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SYNC, enabled).apply()
        _settings.value = _settings.value.copy(autoSyncHealthConnect = enabled)
    }

    fun setMorningReminder(enabled: Boolean, time: String = _settings.value.morningReminderTime) {
        prefs.edit()
            .putBoolean(KEY_REMINDER_MORNING_ENABLED, enabled)
            .putString(KEY_REMINDER_MORNING_TIME, time)
            .apply()
        _settings.value = _settings.value.copy(morningReminderEnabled = enabled, morningReminderTime = time)
    }

    fun setNoonReminder(enabled: Boolean, time: String = _settings.value.noonReminderTime) {
        prefs.edit()
            .putBoolean(KEY_REMINDER_NOON_ENABLED, enabled)
            .putString(KEY_REMINDER_NOON_TIME, time)
            .apply()
        _settings.value = _settings.value.copy(noonReminderEnabled = enabled, noonReminderTime = time)
    }

    fun setEveningReminder(enabled: Boolean, time: String = _settings.value.eveningReminderTime) {
        prefs.edit()
            .putBoolean(KEY_REMINDER_EVENING_ENABLED, enabled)
            .putString(KEY_REMINDER_EVENING_TIME, time)
            .apply()
        _settings.value = _settings.value.copy(eveningReminderEnabled = enabled, eveningReminderTime = time)
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun setAppLanguage(language: String) {
        prefs.edit().putString(KEY_APP_LANGUAGE, language).apply()
        _settings.value = _settings.value.copy(appLanguage = language)
    }

    companion object {
        private const val KEY_DEFAULT_ARM = "pref_default_arm"
        private const val KEY_DEFAULT_POS = "pref_default_pos"
        private const val KEY_AUTO_SYNC = "pref_auto_sync"
        private const val KEY_REMINDER_MORNING_ENABLED = "pref_reminder_morning_enabled"
        private const val KEY_REMINDER_MORNING_TIME = "pref_reminder_morning_time"
        private const val KEY_REMINDER_NOON_ENABLED = "pref_reminder_noon_enabled"
        private const val KEY_REMINDER_NOON_TIME = "pref_reminder_noon_time"
        private const val KEY_REMINDER_EVENING_ENABLED = "pref_reminder_evening_enabled"
        private const val KEY_REMINDER_EVENING_TIME = "pref_reminder_evening_time"
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_APP_LANGUAGE = "pref_app_language"
    }
}
