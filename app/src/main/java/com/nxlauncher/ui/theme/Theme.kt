package com.nxlauncher.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val OnGreen = Color(0xFF06140A)

private val NXColorScheme = darkColorScheme(
    primary = NXGreen,
    onPrimary = OnGreen,
    primaryContainer = NXGreenDark,
    onPrimaryContainer = NXTextPrimary,
    secondary = NXGreenSoft,
    onSecondary = OnGreen,
    background = NXBackground,
    onBackground = NXTextPrimary,
    surface = NXSurface,
    onSurface = NXTextPrimary,
    surfaceVariant = NXSurfaceVariant,
    onSurfaceVariant = NXTextSecondary,
    outline = NXOutline,
    error = NXError,
    onError = NXTextPrimary
)

@Composable
fun NXLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = NXColorScheme,
        typography = NXTypography,
        content = content
    )
}
