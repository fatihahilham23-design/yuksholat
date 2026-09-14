package com.yuksholat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val avatarType: String = "DEFAULT_RED_PAS_FOTO",
    val totalPoints: Int = 0,
    val currentTierLevel: Int = 1,
    val cityName: String = "Jakarta",
    val latitude: Double = -6.2088,
    val longitude: Double = 106.8456,
    val timezoneOffset: Double = 7.0,
    val fajrAngle: Double = 20.0,
    val ishaAngle: Double = 18.0,
    val soundEnabled: Boolean = true,
    val alarmEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastStreakDate: String = "",
    val streakFreezeCount: Int = 1,
    val lastTierLevel: Int = 1,
    val ramadanStarted: Boolean = false,
    val ramadanGoalPrayers: Int = 150,
    val currentRamadanPrayers: Int = 0,
    val hasCompletedFirstLevelUp: Boolean = false
)
