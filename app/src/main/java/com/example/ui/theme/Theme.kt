package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppTheme(val label: String) {
    SYSTEM("Auto"),
    LIGHT("Light"),
    DARK("Dark")
}

private val DarkColorScheme = darkColorScheme(
    primary = FlathubBlueLight,
    onPrimary = Color.Black,
    primaryContainer = FlathubBlueDark,
    onPrimaryContainer = Color.White,
    secondary = FlathubBlue,
    onSecondary = Color.White,
    background = FlathubDarkBg,
    onBackground = FlathubDarkText,
    surface = FlathubDarkSurface,
    onSurface = FlathubDarkText,
    surfaceVariant = FlathubDarkCard,
    onSurfaceVariant = FlathubDarkTextSecondary,
    outline = FlathubDarkBorder,
    outlineVariant = FlathubDarkBorder.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = FlathubBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEBFF),
    onPrimaryContainer = FlathubBlueDark,
    secondary = FlathubBlueDark,
    onSecondary = Color.White,
    background = FlathubLightBg,
    onBackground = FlathubLightText,
    surface = FlathubLightSurface,
    onSurface = FlathubLightText,
    surfaceVariant = FlathubLightCard,
    onSurfaceVariant = FlathubLightTextSecondary,
    outline = FlathubLightBorder,
    outlineVariant = FlathubLightBorder.copy(alpha = 0.6f)
)

@Composable
fun MyApplicationTheme(
    appTheme: AppTheme = AppTheme.LIGHT,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (appTheme) {
        AppTheme.SYSTEM -> isSystemDark
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                // When in light theme (!isDark), set dark icons (isAppearanceLightStatusBars = true)
                // When in dark theme (isDark), set light icons (isAppearanceLightStatusBars = false)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
