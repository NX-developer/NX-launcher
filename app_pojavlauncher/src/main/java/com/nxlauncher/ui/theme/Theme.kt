package com.nxlauncher.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
            val activity = view.context.findActivity()
            if (activity != null) {
                WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = NXColorScheme,
        typography = NXTypography,
        content = content
    )
}

private fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
