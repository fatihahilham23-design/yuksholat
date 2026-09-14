package com.yuksholat.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object History : Screen("history", "Log Shalat")
    object Achievements : Screen("achievements", "Achievement")
    object Settings : Screen("settings", "Pengaturan")
}
