package com.yuksholat.data.model

enum class PrayerType(
    val id: String,
    val displayName: String,
    val arabicName: String,
    val basePoints: Int = 100,
    val latePoints: Int = 30
) {
    SUBUH("SUBUH", "Subuh", "الفجر", 100, 30),
    DZUHUR("DZUHUR", "Dzuhur", "الظهر", 100, 30),
    ASHAR("ASHAR", "Ashar", "العصر", 100, 30),
    MAGHRIB("MAGHRIB", "Maghrib", "المغرب", 100, 30),
    ISYA("ISYA", "Isya", "العشاء", 100, 30);

    fun next(): PrayerType = when (this) {
        SUBUH -> DZUHUR
        DZUHUR -> ASHAR
        ASHAR -> MAGHRIB
        MAGHRIB -> ISYA
        ISYA -> SUBUH
    }

    fun previous(): PrayerType = when (this) {
        SUBUH -> ISYA
        DZUHUR -> SUBUH
        ASHAR -> DZUHUR
        MAGHRIB -> ASHAR
        ISYA -> MAGHRIB
    }

    companion object {
        fun fromId(id: String): PrayerType {
            return entries.firstOrNull { 
                it.id.equals(id, ignoreCase = true) || it.displayName.equals(id, ignoreCase = true) 
            } ?: SUBUH
        }
    }
}