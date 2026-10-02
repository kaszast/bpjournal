package com.kaszast.bpjournal.util

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.core.os.ConfigurationCompat
import java.util.Locale

/**
 * Utility helper for managing application-level language preferences and locale configurations.
 *
 * This singleton provides mechanisms to:
 * 1. Resolve the active [Locale] according to user settings ("system", "hu", or "en").
 * 2. Update the system [LocaleManager] on Android 13+ (API level 33+) for per-app language support.
 * 3. Construct a localized [Configuration] context for Jetpack Compose runtime rendering.
 */
object LocaleHelper {

    /**
     * Resolves the target [Locale] corresponding to the given language preference key.
     *
     * @param context Application or activity context used to inspect default system locales.
     * @param language Language code: "hu" for Hungarian, "en" for English, or "system" to follow device settings.
     * @return The resolved [Locale] instance.
     */
    fun getTargetLocale(context: Context, language: String): Locale {
        return when (language) {
            "hu" -> Locale.forLanguageTag("hu")
            "en" -> Locale.ENGLISH
            else -> {
                val sysLocales = ConfigurationCompat.getLocales(context.resources.configuration)
                if (!sysLocales.isEmpty) sysLocales.get(0) ?: Locale.getDefault() else Locale.getDefault()
            }
        }
    }

    /**
     * Configures the process-wide and context-specific locale settings.
     *
     * Steps executed:
     * - Updates the JVM default locale via [Locale.setDefault].
     * - On Android 13+ (Tiramisu, API 33+), synchronizes with the system's [LocaleManager] so that
     *   OS-level language selectors and external pickers reflect the selection.
     * - Creates an overridden [Configuration] with the resolved locale and layout direction,
     *   returning a specialized configuration [Context] for Jetpack Compose providers.
     *
     * @param context Base context to derive configuration from.
     * @param language Language code ("system", "hu", "en").
     * @return Localized configuration context suitable for resource resolution.
     */
    fun applyLanguage(context: Context, language: String): Context {
        val targetLocale = getTargetLocale(context, language)
        Locale.setDefault(targetLocale)

        // API 33+ Per-App Language Preferences integration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runCatching {
                val localeManager = context.getSystemService(LocaleManager::class.java)
                val appLocales = if (language == "system") LocaleList.getEmptyLocaleList() else LocaleList(targetLocale)
                if (localeManager?.applicationLocales != appLocales) {
                    localeManager?.applicationLocales = appLocales
                }
            }
        }

        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(targetLocale))
        } else {
            config.setLocale(targetLocale)
        }
        config.setLayoutDirection(targetLocale)

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        return context.createConfigurationContext(config)
    }
}
