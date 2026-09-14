package com.yuksholat.data.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class PrayerTimesResult(
    val date: LocalDate,
    val subuh: LocalTime,
    val terbit: LocalTime,
    val dzuhur: LocalTime,
    val ashar: LocalTime,
    val maghrib: LocalTime,
    val isya: LocalTime
) {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun getFormattedTime(prayerType: PrayerType): String {
        return getTimeFor(prayerType).format(timeFormatter)
    }

    fun getTimeFor(prayerType: PrayerType): LocalTime {
        return when (prayerType) {
            PrayerType.SUBUH -> subuh
            PrayerType.DZUHUR -> dzuhur
            PrayerType.ASHAR -> ashar
            PrayerType.MAGHRIB -> maghrib
            PrayerType.ISYA -> isya
        }
    }

    fun getCurrentPrayerWindow(currentTime: LocalTime): PrayerType {
        return when {
            (currentTime.isAfter(subuh) || currentTime == subuh) && currentTime.isBefore(terbit) -> PrayerType.SUBUH
            (currentTime.isAfter(dzuhur) || currentTime == dzuhur) && currentTime.isBefore(ashar) -> PrayerType.DZUHUR
            (currentTime.isAfter(ashar) || currentTime == ashar) && currentTime.isBefore(maghrib) -> PrayerType.ASHAR
            (currentTime.isAfter(maghrib) || currentTime == maghrib) && currentTime.isBefore(isya) -> PrayerType.MAGHRIB
            else -> PrayerType.ISYA
        }
    }

    fun getNextPrayer(currentTime: LocalTime): Pair<PrayerType, LocalTime> {
        return when {
            currentTime.isBefore(subuh) -> PrayerType.SUBUH to subuh
            currentTime.isBefore(dzuhur) -> PrayerType.DZUHUR to dzuhur
            currentTime.isBefore(ashar) -> PrayerType.ASHAR to ashar
            currentTime.isBefore(maghrib) -> PrayerType.MAGHRIB to maghrib
            currentTime.isBefore(isya) -> PrayerType.ISYA to isya
            else -> PrayerType.SUBUH to subuh // Besok
        }
    }

    fun isCurrentlyOnTime(prayerType: PrayerType, currentTime: LocalTime): Boolean {
        return when (prayerType) {
            PrayerType.SUBUH -> (currentTime.isAfter(subuh) || currentTime == subuh) && currentTime.isBefore(terbit)
            PrayerType.DZUHUR -> (currentTime.isAfter(dzuhur) || currentTime == dzuhur) && currentTime.isBefore(ashar)
            PrayerType.ASHAR -> (currentTime.isAfter(ashar) || currentTime == ashar) && currentTime.isBefore(maghrib)
            PrayerType.MAGHRIB -> (currentTime.isAfter(maghrib) || currentTime == maghrib) && currentTime.isBefore(isya)
            PrayerType.ISYA -> (currentTime.isAfter(isya) || currentTime == isya) || currentTime.isBefore(subuh)
        }
    }
}