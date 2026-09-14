package com.yuksholat.data.repository

import com.yuksholat.data.calculation.OfflinePrayerCalculator
import com.yuksholat.data.local.AppDatabase
import com.yuksholat.data.local.entity.Achievement
import com.yuksholat.data.local.entity.PrayerLog
import com.yuksholat.data.local.entity.TierConfig
import com.yuksholat.data.local.entity.UserProfile
import com.yuksholat.data.model.PrayerTimesResult
import com.yuksholat.data.model.PrayerType
import com.yuksholat.data.util.RamadanDetector
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class RecordPrayerResult(
    val pointsEarned: Int,
    val isOnTime: Boolean,
    val newTotalPoints: Int,
    val oldTierLevel: Int,
    val newTierLevel: Int,
    val didLevelUp: Boolean,
    val newTierName: String,
    val newStreak: Int,
    val streakBroken: Boolean,
    val dailyGoalProgress: Int,
    val dailyGoalCompleted: Boolean,
    val unlockedAchievements: List<Achievement> = emptyList()
)

class PrayerRepository(
    private val database: AppDatabase
) {
    private val userProfileDao = database.userProfileDao()
    private val prayerLogDao = database.prayerLogDao()
    private val tierConfigDao = database.tierConfigDao()
    private val achievementDao = database.achievementDao()

    val profileFlow: Flow<UserProfile?> = userProfileDao.getProfileFlow()
    val allLogsFlow: Flow<List<PrayerLog>> = prayerLogDao.getAllLogsFlow()
    val allTiersFlow: Flow<List<TierConfig>> = tierConfigDao.getAllTiersFlow()
    val totalLogsCountFlow: Flow<Int> = prayerLogDao.getTotalLogsCountFlow()
    val onTimeLogsCountFlow: Flow<Int> = prayerLogDao.getOnTimeLogsCountFlow()
    val achievementsFlow: Flow<List<Achievement>> = achievementDao.getAllAchievementsFlow()
    val unlockedCountFlow: Flow<Int> = achievementDao.getUnlockedCountFlow()

    fun getLogsForDateFlow(date: LocalDate): Flow<List<PrayerLog>> {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return prayerLogDao.getLogsForDateFlow(dateString)
    }

    suspend fun getProfile(): UserProfile {
        return userProfileDao.getProfile() ?: UserProfile().also {
            userProfileDao.insertOrUpdate(it)
        }
    }

    fun calculatePrayerTimesForDate(profile: UserProfile, date: LocalDate): PrayerTimesResult {
        return OfflinePrayerCalculator.calculatePrayerTimes(
            date = date,
            latitude = profile.latitude,
            longitude = profile.longitude,
            timezoneOffset = profile.timezoneOffset,
            fajrAngle = profile.fajrAngle,
            ishaAngle = profile.ishaAngle
        )
    }

    suspend fun recordPrayer(
        prayerType: PrayerType,
        targetDate: LocalDate = LocalDate.now(),
        currentTime: LocalTime = LocalTime.now()
    ): RecordPrayerResult {
        val profile = getProfile()
        val prayerTimes = calculatePrayerTimesForDate(profile, targetDate)

        val isToday = targetDate == LocalDate.now()
        val isOnTime = if (isToday) {
            prayerTimes.isCurrentlyOnTime(prayerType, currentTime)
        } else {
            false
        }

        val pointsEarned = if (isOnTime) prayerType.basePoints else prayerType.latePoints
        val dateString = targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

        val existingLog = prayerLogDao.getLog(dateString, prayerType.id)
        if (existingLog == null) {
            prayerLogDao.insertLog(
                PrayerLog(
                    date = dateString,
                    prayerName = prayerType.id,
                    logTimestamp = System.currentTimeMillis(),
                    isOnTime = isOnTime,
                    pointsEarned = pointsEarned
                )
            )
        }

        val oldPoints = profile.totalPoints
        val oldTierLevel = profile.currentTierLevel
        var newPoints = oldPoints + pointsEarned

        // Daily goal bonus: +50 if all 5 prayers logged today
        val todayLogs = prayerLogDao.getLogsForDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
        val dailyGoalCompleted = todayLogs.size >= 5
        var goalBonus = 0
        if (dailyGoalCompleted && todayLogs.size == 5) {
            goalBonus = 50
            newPoints += goalBonus
        }

        val tierConfigs = tierConfigDao.getAllTiers().ifEmpty { TierConfig.DEFAULT_TIERS }
        val matchingTier = tierConfigs.lastOrNull { newPoints >= it.minPoints } ?: tierConfigs.first()
        val newTierLevel = matchingTier.tierLevel
        val didLevelUp = newTierLevel > oldTierLevel

        userProfileDao.updatePointsAndTier(newPoints, newTierLevel, oldTierLevel)

        val (newStreak, streakBroken) = updateStreakInternal()

        var ramadanCount = profile.currentRamadanPrayers
        if (RamadanDetector.isRamadan(targetDate)) {
            ramadanCount += 1
            userProfileDao.updateRamadanPrayers(ramadanCount)
        }

        val unlocked = checkAndUnlockAchievements(newStreak, newPoints, newTierLevel, ramadanCount)

        return RecordPrayerResult(
            pointsEarned = pointsEarned + goalBonus,
            isOnTime = isOnTime,
            newTotalPoints = newPoints,
            oldTierLevel = oldTierLevel,
            newTierLevel = newTierLevel,
            didLevelUp = didLevelUp,
            newTierName = matchingTier.tierName,
            newStreak = newStreak,
            streakBroken = streakBroken,
            dailyGoalProgress = todayLogs.size.coerceAtMost(5),
            dailyGoalCompleted = dailyGoalCompleted,
            unlockedAchievements = unlocked
        )
    }

    private suspend fun updateStreakInternal(): Pair<Int, Boolean> {
        val profile = getProfile()
        val dates = prayerLogDao.getLoggedDates().mapNotNull {
            try { LocalDate.parse(it) } catch (e: Exception) { null }
        }.sortedDescending()

        if (dates.isEmpty()) return Pair(0, false)

        val today = LocalDate.now()
        val hasLoggedToday = dates.contains(today)

        var streak: Int
        var streakBroken = false
        var freezeCount = profile.streakFreezeCount

        if (hasLoggedToday) {
            var checkDate = today
            streak = 0
            for (date in dates) {
                if (date == checkDate) {
                    streak++
                    checkDate = checkDate.minusDays(1)
                } else if (date.isBefore(checkDate)) {
                    break
                }
            }
        } else {
            val lastDate = if (profile.lastStreakDate.isNotEmpty()) {
                try { LocalDate.parse(profile.lastStreakDate) } catch (e: Exception) { null }
            } else null

            if (lastDate != null && lastDate == today.minusDays(1) && profile.currentStreak > 0) {
                streak = profile.currentStreak
                streakBroken = false
            } else if (profile.currentStreak > 0 && freezeCount > 0) {
                freezeCount -= 1
                streak = profile.currentStreak
                streakBroken = false
            } else {
                streak = 0
                streakBroken = profile.currentStreak > 0
            }
        }

        val bestStreak = maxOf(profile.bestStreak, streak)
        val lastDateStr = if (hasLoggedToday) today.format(DateTimeFormatter.ISO_LOCAL_DATE) else profile.lastStreakDate
        userProfileDao.updateStreak(streak, bestStreak, lastDateStr, freezeCount)

        return Pair(streak, streakBroken)
    }

    suspend fun refreshStreak(): Int {
        return updateStreakInternal().first
    }

    suspend fun updateLocation(cityName: String, lat: Double, lng: Double, tz: Double) {
        userProfileDao.updateLocation(cityName, lat, lng, tz)
    }

    suspend fun updateSoundAndAlarm(sound: Boolean, alarm: Boolean) {
        userProfileDao.updateSoundAndAlarm(sound, alarm)
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        userProfileDao.setOnboardingCompleted(completed)
    }

    suspend fun updateName(name: String) {
        userProfileDao.updateName(name)
    }

    suspend fun updateLastTierLevel(tierLevel: Int) {
        userProfileDao.updateLastTierLevel(tierLevel)
    }

    suspend fun setFirstLevelUpCompleted() {
        userProfileDao.setFirstLevelUpCompleted()
    }

    suspend fun startRamadanMode() {
        val profile = getProfile()
        userProfileDao.updateRamadan(true, 150, 0)
    }

    suspend fun resetProgress() {
        userProfileDao.resetPoints()
        prayerLogDao.deleteAllLogs()
    }

    suspend fun calculateStreak(): Int {
        val dates = prayerLogDao.getLoggedDates().mapNotNull {
            try { LocalDate.parse(it) } catch (e: Exception) { null }
        }.sortedDescending()

        if (dates.isEmpty()) return 0

        var streak = 0
        var checkDate = LocalDate.now()

        val hasLoggedToday = dates.contains(checkDate)
        if (!hasLoggedToday) {
            checkDate = checkDate.minusDays(1)
        }

        for (date in dates) {
            if (date == checkDate) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else if (date.isBefore(checkDate)) {
                break
            }
        }
        return streak
    }

    suspend fun getDailyGoalProgress(date: LocalDate = LocalDate.now()): Int {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return prayerLogDao.getLogsForDate(dateString).size.coerceAtMost(5)
    }

    suspend fun getPrayerLogsToday(date: LocalDate = LocalDate.now()): List<PrayerLog> {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return prayerLogDao.getLogsForDate(dateString)
    }

    private suspend fun checkAndUnlockAchievements(
        streak: Int,
        totalPoints: Int,
        tierLevel: Int,
        ramadanCount: Int
    ): List<Achievement> {
        val all = try { achievementDao.getAllAchievements() } catch (e: Exception) { return emptyList() }
        if (all.isEmpty()) {
            achievementDao.insertAll(Achievement.DEFAULT_ACHIEVEMENTS)
            return emptyList()
        }

        val totalLogs = try { prayerLogDao.getLoggedDates().size } catch (e: Exception) { 0 }
        val now = System.currentTimeMillis()
        val unlocked = mutableListOf<Achievement>()

        for (ach in all) {
            if (ach.isUnlocked) continue
            val met = when (ach.requirementType) {
                "STREAK_DAYS" -> streak >= ach.requirementValue
                "PRAYERS_TOTAL" -> totalLogs >= ach.requirementValue
                "TIER_LEVEL" -> tierLevel >= ach.requirementValue
                "RAMADAN_TOTAL" -> ramadanCount >= ach.requirementValue
                else -> false
            }
            if (met) {
                achievementDao.unlockAchievement(ach.id, now)
                unlocked.add(ach.copy(isUnlocked = true, unlockedAt = now))
            }
        }
        return unlocked
    }
}
