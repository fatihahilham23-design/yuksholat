package com.yuksholat.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_logs",
    indices = [Index(value = ["date", "prayerName"], unique = true)]
)
data class PrayerLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD format
    val prayerName: String, // SUBUH, DZUHUR, ASHAR, MAGHRIB, ISYA
    val logTimestamp: Long = System.currentTimeMillis(),
    val isOnTime: Boolean = true,
    val pointsEarned: Int = 100,
    val note: String = ""
)