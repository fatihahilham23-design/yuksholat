package com.yuksholat.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.yuksholat.MainActivity
import com.yuksholat.YukSholatApp
import com.yuksholat.audio.RetroSoundSynthesizer
import com.yuksholat.data.local.AppDatabase
import com.yuksholat.data.model.PrayerType
import com.yuksholat.data.repository.PrayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME) ?: return
        if (prayerName == "IMSAK") {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, YukSholatApp.CHANNEL_PRAYER_ALERTS)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🌙 Waktu Imsak!")
                .setContentText("10 menit lagi waktu Subuh. Persiapkan diri untuk berpuasa!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 300, 200, 300))
                .build()
            notificationManager.notify(900, notification)
            return
        }

        if (prayerName == "SAHUR_END") {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, YukSholatApp.CHANNEL_PRAYER_ALERTS)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("🍽️ Sahur Habis 5 Menit Lagi!")
                .setContentText("Segera makan sahur, waktu Subuh hampir tiba!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 200, 100, 200))
                .build()
            notificationManager.notify(901, notification)
            return
        }
        val prayerType = PrayerType.fromId(prayerName)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("TARGET_PRAYER", prayerType.name)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerType.ordinal + 200,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, YukSholatApp.CHANNEL_PRAYER_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentTitle("Waktunya Shalat ${prayerType.displayName}!")
            .setContentText("Raih +100 Poin dan tingkatkan Tier-mu sekarang!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 300, 200, 300, 200, 500))
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_agenda,
                "Absen Sekarang",
                pendingIntent
            )
            .build()

        notificationManager.notify(prayerType.ordinal + 1, notification)

        // Mainkan suara chime 8-bit
        RetroSoundSynthesizer.playPrayerChime()

        // Jadwalkan ulang shalat untuk hari berikutnya
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val db = AppDatabase.getDatabase(context, scope)
            val repo = PrayerRepository(db)
            val profile = repo.getProfile()
            val tomorrow = LocalDate.now().plusDays(1)
            val tomorrowTimes = repo.calculatePrayerTimesForDate(profile, tomorrow)
            val tomorrowTarget = LocalDateTime.of(tomorrow, tomorrowTimes.getTimeFor(prayerType))
            
            val scheduler = PrayerAlarmScheduler(context)
            scheduler.scheduleAlarm(prayerType, tomorrowTarget)
        }
    }
}