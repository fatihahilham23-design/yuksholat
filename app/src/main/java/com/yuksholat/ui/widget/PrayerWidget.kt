package com.yuksholat.ui.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.yuksholat.MainActivity
import com.yuksholat.data.model.PrayerType
import com.yuksholat.data.util.RamadanDetector
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Duration

class PrayerWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val now = LocalTime.now()
        val today = LocalDate.now()
        val db = com.yuksholat.data.local.AppDatabase.getDatabase(context, kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO))
        val repo = com.yuksholat.data.repository.PrayerRepository(db)
        val times = repo.calculatePrayerTimesForDate(repo.getProfile(), today)

        val (nextP, nextPTime) = times.getNextPrayer(now)
        val nextTime = times.getTimeFor(nextP)
        var targetDateTime = LocalDateTime.of(today, nextTime)
        if (targetDateTime.isBefore(LocalDateTime.now())) {
            targetDateTime = targetDateTime.plusDays(1)
        }
        val duration = Duration.between(LocalDateTime.now(), targetDateTime)
        val seconds = duration.seconds.coerceAtLeast(0)
        val countdownH = seconds / 3600
        val countdownM = (seconds % 3600) / 60

        val todayLogs = repo.getPrayerLogsToday(today)

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .cornerRadius(16.dp)
                    .background(ColorProvider(Color(0xFF1A1208)))
                    .clickable(actionStartActivity<MainActivity>())
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🕌 YUK SHOLAT",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(Color(0xFFD4A017))
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "🕐 ${String.format("%02d:%02d", now.hour, now.minute)}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(Color.White)
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "${nextP.displayName} dalam ${countdownH}j ${countdownM}m",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(Color(0xFF5B8C5A))
                    )
                )
                Spacer(modifier = GlanceModifier.height(6.dp))
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PrayerType.entries.forEach { prayer ->
                        val logged = todayLogs.any { it.prayerName == prayer.id }
                        Text(
                            text = if (logged) "●" else "○",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = ColorProvider(
                                    if (logged) Color(0xFF5B8C5A) else Color(0xFFC4A882)
                                )
                            ),
                            modifier = GlanceModifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                if (RamadanDetector.isRamadan()) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "🌙 Ramadan Hari ${RamadanDetector.ramadanDayNumber()}",
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = ColorProvider(Color(0xFFD4A017))
                        )
                    )
                }
            }
        }
    }
}
