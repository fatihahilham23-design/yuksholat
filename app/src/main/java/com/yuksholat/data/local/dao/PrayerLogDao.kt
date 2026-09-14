package com.yuksholat.data.local.dao

import androidx.room.*
import com.yuksholat.data.local.entity.PrayerLog
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerLogDao {
    @Query("SELECT * FROM prayer_logs ORDER BY logTimestamp DESC")
    fun getAllLogsFlow(): Flow<List<PrayerLog>>

    @Query("SELECT * FROM prayer_logs WHERE date = :date")
    fun getLogsForDateFlow(date: String): Flow<List<PrayerLog>>

    @Query("SELECT * FROM prayer_logs WHERE date = :date")
    suspend fun getLogsForDate(date: String): List<PrayerLog>

    @Query("SELECT * FROM prayer_logs WHERE date = :date AND prayerName = :prayerName LIMIT 1")
    suspend fun getLog(date: String, prayerName: String): PrayerLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PrayerLog): Long

    @Query("DELETE FROM prayer_logs WHERE id = :id")
    suspend fun deleteLog(id: Long)

    @Query("DELETE FROM prayer_logs")
    suspend fun deleteAllLogs()

    @Query("SELECT COUNT(*) FROM prayer_logs")
    fun getTotalLogsCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM prayer_logs WHERE isOnTime = 1")
    fun getOnTimeLogsCountFlow(): Flow<Int>

    @Query("SELECT DISTINCT date FROM prayer_logs ORDER BY date DESC")
    suspend fun getLoggedDates(): List<String>
}