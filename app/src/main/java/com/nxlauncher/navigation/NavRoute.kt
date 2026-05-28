package com.nxlauncher.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoute(val route: String) {
    data object Home : NavRoute("home")
    data object Versions : NavRoute("versions")
    data object Mods : NavRoute("mods")
    data object Settings : NavRoute("settings")
    data object Launch : NavRoute("launch/{version}") {
        fun create(version: String) = "launch/$version"
    }
    data object ModDetail : NavRoute("mod_detail/{source}/{modId}") {
        fun create(source: String, modId: String) = "mod_detail/$source/$modId"
    }
}

data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(NavRoute.Home.route, com.nxlauncher.R.string.nav_home, Icons.Filled.SportsEsports),
    BottomNavItem(NavRoute.Versions.route, com.nxlauncher.R.string.nav_versions, Icons.Filled.GridView),
    BottomNavItem(NavRoute.Mods.route, com.nxlauncher.R.string.nav_mods, Icons.Filled.Extension),
    BottomNavItem(NavRoute.Settings.route, com.nxlauncher.R.string.nav_settings, Icons.Filled.Settings)
)
