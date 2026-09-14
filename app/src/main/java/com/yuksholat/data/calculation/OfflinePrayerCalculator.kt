package com.yuksholat.data.calculation

import com.yuksholat.data.model.PrayerTimesResult
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.*

/**
 * 100% Offline Astronomical Solar Calculation Engine.
 * Menghitung waktu shalat 5 waktu akurat berdasarkan koordinat dan tanggal tanpa koneksi internet.
 */
object OfflinePrayerCalculator {

    fun calculatePrayerTimes(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        timezoneOffset: Double,
        fajrAngle: Double = 20.0,
        ishaAngle: Double = 18.0
    ): PrayerTimesResult {
        val year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth

        // 1. Julian Date
        val jd = calculateJulianDate(year, month, day)
        val d = jd - 2451545.0 // Hari sejak epoch J2000.0

        // 2. Posisi Matahari
        val q = 280.459 + 0.98564736 * d
        val l = fixAngle(q)
        val m = fixAngle(357.529 + 0.98560028 * d)
        val lambda = fixAngle(l + 1.915 * sin(Math.toRadians(m)) + 0.020 * sin(Math.toRadians(2 * m)))

        val obliquity = 23.439 - 0.00000036 * d
        val sinDeclination = sin(Math.toRadians(obliquity)) * sin(Math.toRadians(lambda))
        val declination = Math.toDegrees(asin(sinDeclination))

        val cosAlpha = cos(Math.toRadians(lambda))
        val sinAlpha = cos(Math.toRadians(obliquity)) * sin(Math.toRadians(lambda))
        var rightAscension = Math.toDegrees(atan2(sinAlpha, cosAlpha)) / 15.0
        rightAscension = fixHours(rightAscension)

        val equationOfTime = (q / 15.0) - rightAscension

        // 3. Waktu Transit Matahari (Solar Noon)
        val solarNoon = fixHours(12.0 + timezoneOffset - (longitude / 15.0) - equationOfTime)

        // 4. Hitung sudut waktu untuk tiap shalat
        val fajrHourAngle = calculateHourAngle(latitude, declination, -fajrAngle)
        val sunriseHourAngle = calculateHourAngle(latitude, declination, -0.8333)
        val asrAltitude = Math.toDegrees(atan(1.0 / (1.0 + tan(Math.toRadians(abs(latitude - declination))))))
        val asrHourAngle = calculateHourAngle(latitude, declination, asrAltitude)
        val ishaHourAngle = calculateHourAngle(latitude, declination, -ishaAngle)

        // 5. Waktu dalam desimal jam (ditambah safety buffer 2 menit / ihtiyat)
        val ihtiyatHours = 2.0 / 60.0

        val subuhHours = fixHours(solarNoon - fajrHourAngle + ihtiyatHours)
        val terbitHours = fixHours(solarNoon - sunriseHourAngle)
        val dzuhurHours = fixHours(solarNoon + ihtiyatHours)
        val asharHours = fixHours(solarNoon + asrHourAngle + ihtiyatHours)
        val maghribHours = fixHours(solarNoon + sunriseHourAngle + ihtiyatHours)
        val isyaHours = fixHours(solarNoon + ishaHourAngle + ihtiyatHours)

        return PrayerTimesResult(
            date = date,
            subuh = decimalHoursToLocalTime(subuhHours),
            terbit = decimalHoursToLocalTime(terbitHours),
            dzuhur = decimalHoursToLocalTime(dzuhurHours),
            ashar = decimalHoursToLocalTime(asharHours),
            maghrib = decimalHoursToLocalTime(maghribHours),
            isya = decimalHoursToLocalTime(isyaHours)
        )
    }

    private fun calculateJulianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun calculateHourAngle(latitude: Double, declination: Double, altitudeAngle: Double): Double {
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)
        val altRad = Math.toRadians(altitudeAngle)

        val cosH = (sin(altRad) - (sin(latRad) * sin(decRad))) / (cos(latRad) * cos(decRad))
        val clampedCosH = cosH.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clampedCosH)) / 15.0
    }

    private fun fixAngle(angle: Double): Double {
        var a = angle % 360.0
        if (a < 0) a += 360.0
        return a
    }

    private fun fixHours(hour: Double): Double {
        var h = hour % 24.0
        if (h < 0) h += 24.0
        return h
    }

    private fun decimalHoursToLocalTime(decimalHours: Double): LocalTime {
        val totalSeconds = (decimalHours * 3600.0).roundToInt()
        val normalizedSeconds = ((totalSeconds % 86400) + 86400) % 86400
        val hours = normalizedSeconds / 3600
        val minutes = (normalizedSeconds % 3600) / 60
        val seconds = normalizedSeconds % 60
        return LocalTime.of(hours, minutes, seconds)
    }
}