package com.yuksholat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val requirementType: String,
    val requirementValue: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0
) {
    fun iconEmoji(): String = when (iconName) {
        "star" -> "⭐"
        "fire", "fire_big", "inferno" -> "🔥"
        "sword" -> "⚔️"
        "shield" -> "🛡️"
        "crown" -> "👑"
        "diamond" -> "💎"
        "clock" -> "⏰"
        "moon" -> "🌙"
        "crescent" -> "☪️"
        "gem" -> "💠"
        else -> "🏆"
    }

    companion object {
        val DEFAULT_ACHIEVEMENTS = listOf(
            Achievement("first_prayer", "Langkah Pertama", "Catat shalat pertamamu", "star", "PRAYERS_TOTAL", 1),
            Achievement("streak_3", "Api Kecil", "Streak 3 hari berturut-turut", "fire", "STREAK_DAYS", 3),
            Achievement("streak_7", "Api Menyala", "Streak 7 hari berturut-turut", "fire_big", "STREAK_DAYS", 7),
            Achievement("streak_30", "Tak Terpadamkan", "Streak 30 hari berturut-turut", "inferno", "STREAK_DAYS", 30),
            Achievement("prayers_50", "Pejuang Konsisten", "Catat 50 shalat", "sword", "PRAYERS_TOTAL", 50),
            Achievement("prayers_100", "Veteran Masjid", "Catat 100 shalat", "shield", "PRAYERS_TOTAL", 100),
            Achievement("tier_3", "Ksatria Sejati", "Capai Tier 3 Ksatria Masjid", "crown", "TIER_LEVEL", 3),
            Achievement("tier_5", "Legenda Hidup", "Capai Tier 5 Legenda Iman", "diamond", "TIER_LEVEL", 5),
            Achievement("ontime_30", "Tepat Waktu", "30 shalat tepat waktu", "clock", "ONTIME_TOTAL", 30),
            Achievement("subuh_7", "Penakluk Fajar", "7x shalat Subuh tercatat", "moon", "SUBUH_TOTAL", 7),
            Achievement("ramadan_warrior", "Mujahid Ramadan", "50 shalat selama Ramadan", "crescent", "RAMADAN_TOTAL", 50),
            Achievement("perfect_day", "Hari Sempurna", "Lengkapi 5/5 shalat dalam sehari", "gem", "PERFECT_DAYS", 1)
        )
    }
}
