package com.nxlauncher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nxlauncher.data.model.ModSource
import com.nxlauncher.data.repository.MojangRepository
import com.nxlauncher.navigation.NavRoute
import com.nxlauncher.navigation.bottomNavItems
import com.nxlauncher.ui.screens.HomeScreen
import com.nxlauncher.ui.screens.ModDetailScreen
import com.nxlauncher.ui.screens.ModsScreen
import com.nxlauncher.ui.screens.SettingsScreen
import com.nxlauncher.ui.screens.VersionsScreen
import com.nxlauncher.ui.theme.NXBackground
import com.nxlauncher.ui.theme.NXGreen
import com.nxlauncher.ui.theme.NXLauncherTheme
import com.nxlauncher.ui.theme.NXSurface
import com.nxlauncher.ui.theme.NXTextMuted

@Composable
fun NXLauncherRoot(onPlay: () -> Unit, onAccount: () -> Unit) {
    NXLauncherTheme {
        NXApp(onPlay = onPlay, onAccount = onAccount)
    }
}

@Composable
private fun NXApp(onPlay: () -> Unit, onAccount: () -> Unit) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    BackHandler(enabled = currentRoute != null && currentRoute != NavRoute.Home.route) {
        navController.popBackStack()
    }

    val latestVersion by produceState(initialValue = "26.1.2") {
        runCatching { MojangRepository.latestRelease() }
            .getOrNull()
            ?.let { value = it }
    }

    Scaffold(
        containerColor = NXBackground,
        bottomBar = {
            if (showBottomBar) {
                NXBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(NavRoute.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NXBackground)
                .statusBarsPadding()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = NavRoute.Home.route
            ) {
                composable(NavRoute.Home.route) {
                    HomeScreen(
                        selectedVersion = latestVersion,
                        onVersionClick = { navController.navigate(NavRoute.Versions.route) },
                        onSettingsClick = { navController.navigate(NavRoute.Settings.route) },
                        onPlay = onPlay,
                        onAccount = onAccount
                    )
                }
                composable(NavRoute.Versions.route) {
                    VersionsScreen()
                }
                composable(NavRoute.Mods.route) {
                    ModsScreen(
                        onModClick = { source, modId ->
                            navController.navigate(NavRoute.ModDetail.create(source.name, modId))
                        }
                    )
                }
                composable(NavRoute.Settings.route) {
                    SettingsScreen()
                }
                composable(
                    route = NavRoute.ModDetail.route,
                    arguments = listOf(
                        navArgument("source") { type = NavType.StringType },
                        navArgument("modId") { type = NavType.StringType }
                    )
                ) { entry ->
                    val sourceName = entry.arguments?.getString("source").orEmpty()
                    val modId = entry.arguments?.getString("modId").orEmpty()
                    val source = runCatching { ModSource.valueOf(sourceName) }.getOrDefault(ModSource.MODRINTH)
                    ModDetailScreen(
                        source = source,
                        id = modId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun NXBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = NXSurface,
        contentColor = NXTextMuted,
        modifier = Modifier.navigationBarsPadding()
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NXGreen,
                    selectedTextColor = NXGreen,
                    unselectedIconColor = NXTextMuted,
                    unselectedTextColor = NXTextMuted,
                    indicatorColor = NXGreen.copy(alpha = 0.16f)
                )
            )
        }
    }
}
