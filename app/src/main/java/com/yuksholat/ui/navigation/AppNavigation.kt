package com.yuksholat.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yuksholat.ui.screens.history.HistoryScreen
import com.yuksholat.ui.screens.history.HistoryViewModel
import com.yuksholat.ui.screens.achievements.AchievementsScreen
import com.yuksholat.ui.screens.achievements.AchievementsViewModel
import com.yuksholat.ui.screens.home.HomeScreen
import com.yuksholat.ui.screens.home.HomeViewModel
import com.yuksholat.ui.screens.settings.SettingsScreen
import com.yuksholat.ui.screens.settings.SettingsViewModel
import com.yuksholat.ui.theme.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .shadow(12.dp)
                    .background(PixelBgDark)
                    .border(2.dp, PixelWoodDark),
                containerColor = PixelBgDark,
                contentColor = PixelGold
            ) {
                val items = listOf(
                    Triple(Screen.Home, "Home", Icons.Default.Home),
                    Triple(Screen.History, "Log Shalat", Icons.Default.DateRange),
                    Triple(Screen.Achievements, "Achievement", Icons.Default.Star),
                    Triple(Screen.Settings, "Pengaturan", Icons.Default.Settings)
                )

                items.forEach { (screen, label, icon) ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (selected) PixelGold else EarthSand
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = PixelTypography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) PixelGold else EarthSand
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PixelGold,
                            unselectedIconColor = EarthSand,
                            indicatorColor = PixelWoodDark
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel()
                HomeScreen(viewModel = homeViewModel)
            }
            composable(Screen.History.route) {
                val historyViewModel: HistoryViewModel = viewModel()
                HistoryScreen(viewModel = historyViewModel)
            }
            composable(Screen.Achievements.route) {
                val achievementsViewModel: AchievementsViewModel = viewModel()
                AchievementsScreen(viewModel = achievementsViewModel)
            }
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel()
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}
