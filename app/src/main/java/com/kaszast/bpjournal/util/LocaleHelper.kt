package com.kaszast.bpjournal.util

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.core.os.ConfigurationCompat
import java.util.Locale

object LocaleHelper {

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

    fun applyLanguage(context: Context, language: String): Context {
        val targetLocale = getTargetLocale(context, language)
        Locale.setDefault(targetLocale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            val appLocales = if (language == "system") LocaleList.getEmptyLocaleList() else LocaleList(targetLocale)
            localeManager?.applicationLocales = appLocales
        }

        val config = Configuration(context.resources.configuration)
        config.setLocale(targetLocale)
        config.setLayoutDirection(targetLocale)

        return context.createConfigurationContext(config)
    }
}
