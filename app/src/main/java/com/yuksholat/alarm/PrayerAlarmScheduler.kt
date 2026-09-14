package com.yuksholat.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.yuksholat.data.model.PrayerTimesResult
import com.yuksholat.data.model.PrayerType
import java.time.LocalDateTime
import java.time.ZoneId

class PrayerAlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAllPrayerAlarms(prayerTimes: PrayerTimesResult) {
        val now = LocalDateTime.now()
        for (prayer in PrayerType.entries) {
            val prayerTime = prayerTimes.getTimeFor(prayer)
            var targetDateTime = LocalDateTime.of(prayerTimes.date, prayerTime)

            if (targetDateTime.isBefore(now)) {
                targetDateTime = targetDateTime.plusDays(1)
            }

            scheduleAlarm(prayer, targetDateTime)
        }
    }

    fun scheduleRamadanAlarms(prayerTimes: PrayerTimesResult) {
        val now = LocalDateTime.now()
        
        // Imsak: 10 minutes before Subuh
        val subuhTime = prayerTimes.getTimeFor(PrayerType.SUBUH)
        var imsakDateTime = LocalDateTime.of(prayerTimes.date, subuhTime).minusMinutes(10)
        if (imsakDateTime.isBefore(now)) {
            imsakDateTime = imsakDateTime.plusDays(1)
        }
        
        // Sahur end reminder: 5 minutes before Subuh  
        var sahurEndDateTime = LocalDateTime.of(prayerTimes.date, subuhTime).minusMinutes(5)
        if (sahurEndDateTime.isBefore(now)) {
            sahurEndDateTime = sahurEndDateTime.plusDays(1)
        }

        val imsakIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_NAME, "IMSAK")
        }
        val imsakPI = PendingIntent.getBroadcast(context, 900, imsakIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val imsakMillis = imsakDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        try { alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, imsakMillis, imsakPI) } catch (e: SecurityException) { e.printStackTrace() }

        val sahurIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_NAME, "SAHUR_END")
        }
        val sahurPI = PendingIntent.getBroadcast(context, 901, sahurIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val sahurMillis = sahurEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        try { alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, sahurMillis, sahurPI) } catch (e: SecurityException) { e.printStackTrace() }
    }

    fun cancelRamadanAlarms() {
        listOf(900, 901).forEach { code ->
            val intent = Intent(context, PrayerAlarmReceiver::class.java)
            val pi = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(pi)
        }
    }

    fun scheduleAlarm(prayerType: PrayerType, targetDateTime: LocalDateTime) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_NAME, prayerType.name)
        }

        val requestCode = prayerType.ordinal + 100
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancelAllAlarms() {
        for (prayer in PrayerType.entries) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java)
            val requestCode = prayer.ordinal + 100
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
        cancelRamadanAlarms()
    }

    companion object {
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
    }
}
