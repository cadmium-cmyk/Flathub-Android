package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppTheme

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("flathub_theme_prefs", Context.MODE_PRIVATE)

    fun getTheme(): AppTheme {
        val saved = prefs.getString(KEY_THEME, AppTheme.LIGHT.name)
        return try {
            AppTheme.valueOf(saved ?: AppTheme.LIGHT.name)
        } catch (e: Exception) {
            AppTheme.LIGHT
        }
    }

    fun setTheme(theme: AppTheme) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    companion object {
        private const val KEY_THEME = "selected_app_theme"
    }
}
