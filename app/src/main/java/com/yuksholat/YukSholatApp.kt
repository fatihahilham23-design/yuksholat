package com.yuksholat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.yuksholat.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class YukSholatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()

        // Inisialisasi Database Room
        val applicationScope = CoroutineScope(Dispatchers.IO)
        applicationScope.launch {
            AppDatabase.getDatabase(this@YukSholatApp, applicationScope)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_PRAYER_ALERTS,
                getString(R.string.channel_prayer_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.channel_prayer_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300, 200, 500)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_PRAYER_ALERTS = "prayer_alerts"
    }
}
