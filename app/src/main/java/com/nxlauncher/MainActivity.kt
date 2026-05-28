package com.nxlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nxlauncher.data.sample.SampleData
import com.nxlauncher.navigation.NavRoute
import com.nxlauncher.navigation.bottomNavItems
import com.nxlauncher.ui.screens.HomeScreen
import com.nxlauncher.ui.screens.ModDetailScreen
import com.nxlauncher.ui.screens.ModsScreen
import com.nxlauncher.ui.screens.SettingsScreen
import com.nxlauncher.ui.screens.VersionsScreen
import com.nxlauncher.ui.theme.NXBackground
import com.nxlauncher.ui.theme.NXGreen
import com.nxlauncher.ui.theme.NXOutline
import com.nxlauncher.ui.theme.NXSurface
import com.nxlauncher.ui.theme.NXTextMuted
import com.nxlauncher.ui.theme.NXLauncherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            NXLauncherTheme {
                NXApp()
            }
        }
    }
}

@Composable
private fun NXApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }
    val selectedVersion = SampleData.versions.firstOrNull { it.installed }?.id ?: SampleData.versions.first().id

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
                        selectedVersion = selectedVersion,
                        onVersionClick = { navController.navigate(NavRoute.Versions.route) },
                        onSettingsClick = { navController.navigate(NavRoute.Settings.route) }
                    )
                }
                composable(NavRoute.Versions.route) {
                    VersionsScreen()
                }
                composable(NavRoute.Mods.route) {
                    ModsScreen(
                        onModClick = { modId ->
                            navController.navigate(NavRoute.ModDetail.create(modId))
                        }
                    )
                }
                composable(NavRoute.Settings.route) {
                    SettingsScreen()
                }
                composable(
                    route = NavRoute.ModDetail.route,
                    arguments = listOf(navArgument("modId") { type = NavType.StringType })
                ) { entry ->
                    val modId = entry.arguments?.getString("modId").orEmpty()
                    ModDetailScreen(
                        modId = modId,
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
                icon = {
                    androidx.compose.material3.Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelRes)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.labelRes),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
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
