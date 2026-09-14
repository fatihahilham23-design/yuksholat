package com.yuksholat.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yuksholat.data.local.dao.AchievementDao
import com.yuksholat.data.local.dao.PrayerLogDao
import com.yuksholat.data.local.dao.TierConfigDao
import com.yuksholat.data.local.dao.UserProfileDao
import com.yuksholat.data.local.entity.Achievement
import com.yuksholat.data.local.entity.PrayerLog
import com.yuksholat.data.local.entity.TierConfig
import com.yuksholat.data.local.entity.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserProfile::class, PrayerLog::class, TierConfig::class, Achievement::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun prayerLogDao(): PrayerLogDao
    abstract fun tierConfigDao(): TierConfigDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // New columns for UserProfile
                db.execSQL("ALTER TABLE user_profile ADD COLUMN currentStreak INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN bestStreak INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN lastStreakDate TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN streakFreezeCount INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN lastTierLevel INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN ramadanStarted INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN ramadanGoalPrayers INTEGER NOT NULL DEFAULT 150")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN currentRamadanPrayers INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE user_profile ADD COLUMN hasCompletedFirstLevelUp INTEGER NOT NULL DEFAULT 0")

                // Create achievements table
                db.execSQL("CREATE TABLE IF NOT EXISTS achievements (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, iconName TEXT NOT NULL, requirementType TEXT NOT NULL, requirementValue INTEGER NOT NULL, isUnlocked INTEGER NOT NULL DEFAULT 0, unlockedAt INTEGER NOT NULL DEFAULT 0)")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "yuksholat.db"
                )
                .addCallback(DatabaseCallback(scope))
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        database.userProfileDao().insertOrUpdate(UserProfile())
                        database.tierConfigDao().insertAll(TierConfig.DEFAULT_TIERS)
                        database.achievementDao().insertAll(Achievement.DEFAULT_ACHIEVEMENTS)
                    }
                }
            }
        }
    }
}
