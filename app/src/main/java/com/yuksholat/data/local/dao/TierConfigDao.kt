package com.yuksholat.data.local.dao

import androidx.room.*
import com.yuksholat.data.local.entity.TierConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface TierConfigDao {
    @Query("SELECT * FROM tier_configs ORDER BY tierLevel ASC")
    fun getAllTiersFlow(): Flow<List<TierConfig>>

    @Query("SELECT * FROM tier_configs ORDER BY tierLevel ASC")
    suspend fun getAllTiers(): List<TierConfig>

    @Query("SELECT * FROM tier_configs WHERE :points >= minPoints AND :points < maxPoints LIMIT 1")
    suspend fun getTierForPoints(points: Int): TierConfig?

    @Query("SELECT * FROM tier_configs WHERE tierLevel = :level LIMIT 1")
    suspend fun getTierByLevel(level: Int): TierConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tiers: List<TierConfig>)
}