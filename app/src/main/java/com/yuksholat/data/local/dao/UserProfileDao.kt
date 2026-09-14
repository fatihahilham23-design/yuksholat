package com.yuksholat.data.local.dao

import androidx.room.*
import com.yuksholat.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)

    @Query("UPDATE user_profile SET totalPoints = :points, currentTierLevel = :tier, lastTierLevel = :oldTier WHERE id = 1")
    suspend fun updatePointsAndTier(points: Int, tier: Int, oldTier: Int)

    @Query("UPDATE user_profile SET cityName = :cityName, latitude = :lat, longitude = :lng, timezoneOffset = :tz WHERE id = 1")
    suspend fun updateLocation(cityName: String, lat: Double, lng: Double, tz: Double)

    @Query("UPDATE user_profile SET soundEnabled = :sound, alarmEnabled = :alarm WHERE id = 1")
    suspend fun updateSoundAndAlarm(sound: Boolean, alarm: Boolean)

    @Query("UPDATE user_profile SET name = :name WHERE id = 1")
    suspend fun updateName(name: String)

    @Query("UPDATE user_profile SET totalPoints = 0, currentTierLevel = 1, lastTierLevel = 1, currentStreak = 0, bestStreak = 0, lastStreakDate = '', currentRamadanPrayers = 0 WHERE id = 1")
    suspend fun resetPoints()

    @Query("UPDATE user_profile SET onboardingCompleted = :completed WHERE id = 1")
    suspend fun setOnboardingCompleted(completed: Boolean)

    @Query("UPDATE user_profile SET currentStreak = :streak, bestStreak = :bestStreak, lastStreakDate = :lastDate, streakFreezeCount = :freezeCount, currentStreak = :streak WHERE id = 1")
    suspend fun updateStreak(streak: Int, bestStreak: Int, lastDate: String, freezeCount: Int)

    @Query("UPDATE user_profile SET lastTierLevel = :tierLevel WHERE id = 1")
    suspend fun updateLastTierLevel(tierLevel: Int)

    @Query("UPDATE user_profile SET hasCompletedFirstLevelUp = 1 WHERE id = 1")
    suspend fun setFirstLevelUpCompleted()

    @Query("UPDATE user_profile SET ramadanStarted = :started, ramadanGoalPrayers = :goal, currentRamadanPrayers = :current WHERE id = 1")
    suspend fun updateRamadan(started: Boolean, goal: Int, current: Int)

    @Query("UPDATE user_profile SET currentRamadanPrayers = :count WHERE id = 1")
    suspend fun updateRamadanPrayers(count: Int)
}
