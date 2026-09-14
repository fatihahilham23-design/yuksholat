package com.yuksholat.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yuksholat.data.local.AppDatabase
import com.yuksholat.data.repository.PrayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || 
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED || 
            action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val db = AppDatabase.getDatabase(context, scope)
                val repo = PrayerRepository(db)
                val profile = repo.getProfile()

                if (profile.alarmEnabled) {
                    val todayTimes = repo.calculatePrayerTimesForDate(profile, LocalDate.now())
                    val scheduler = PrayerAlarmScheduler(context)
                    scheduler.scheduleAllPrayerAlarms(todayTimes)
                }
            }
        }
    }
}