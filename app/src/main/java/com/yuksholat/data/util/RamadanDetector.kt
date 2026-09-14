package com.yuksholat.data.util

import java.time.LocalDate

object RamadanDetector {
    val RAMADAN_DATES = mapOf(
        2025 to LocalDate.of(2025, 3, 1),
        2026 to LocalDate.of(2026, 2, 18),
        2027 to LocalDate.of(2027, 2, 8),
        2028 to LocalDate.of(2028, 1, 28),
        2029 to LocalDate.of(2029, 1, 17),
        2030 to LocalDate.of(2030, 1, 6)
    )

    fun isRamadan(date: LocalDate = LocalDate.now()): Boolean {
        val ramadanStart = RAMADAN_DATES[date.year] ?: return false
        val ramadanEnd = ramadanStart.plusDays(30)
        return !date.isBefore(ramadanStart) && date.isBefore(ramadanEnd)
    }

    fun ramadanDayNumber(date: LocalDate = LocalDate.now()): Int {
        val ramadanStart = RAMADAN_DATES[date.year] ?: return 0
        return if (isRamadan(date)) (date.toEpochDay() - ramadanStart.toEpochDay() + 1).toInt() else 0
    }

    fun daysLeftInRamadan(date: LocalDate = LocalDate.now()): Int {
        val ramadanStart = RAMADAN_DATES[date.year] ?: return 0
        val ramadanEnd = ramadanStart.plusDays(30)
        return if (isRamadan(date)) (ramadanEnd.toEpochDay() - date.toEpochDay()).toInt() else 0
    }
}
