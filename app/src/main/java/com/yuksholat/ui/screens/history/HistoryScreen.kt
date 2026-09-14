package com.yuksholat.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.data.model.PrayerType
import com.yuksholat.ui.components.RetroButton
import com.yuksholat.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PixelBgDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Text(
                text = "?? RIWAYAT & STATISTIK",
                style = PixelTypography.titleLarge.copy(
                    color = PixelGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            )
        }

        // 1. Stats Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Streak Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(PixelBgCard, RoundedCornerShape(8.dp))
                        .border(2.dp, PixelGold, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "STREAK",
                            style = PixelTypography.labelSmall.copy(
                                color = PixelGoldDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${state.currentStreakDays} HARI ??",
                            style = PixelTypography.titleMedium.copy(
                                color = PixelGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                // Total Shalat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(PixelBgCard, RoundedCornerShape(8.dp))
                        .border(2.dp, PixelGreen, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "TOTAL SHALAT",
                            style = PixelTypography.labelSmall.copy(
                                color = PixelGreenDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${state.totalPrayersLogged} KALI",
                            style = PixelTypography.titleMedium.copy(
                                color = PixelGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                // Tepat Waktu %
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(PixelBgCard, RoundedCornerShape(8.dp))
                        .border(2.dp, PixelBlue, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "TEPAT WAKTU",
                            style = PixelTypography.labelSmall.copy(
                                color = PixelBlue,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${state.onTimePercentage}%",
                            style = PixelTypography.titleMedium.copy(
                                color = PixelBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }

        // 2. 7-Day Date Selector Bar
        item {
            val today = LocalDate.now()
            val dateList = (-6..0).map { today.plusDays(it.toLong()) }

            Column {
                Text(
                    text = "PILIH TANGGAL:",
                    style = PixelTypography.labelSmall.copy(
                        color = EarthSand,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dateList) { date ->
                        val isSelected = date == state.selectedDate
                        val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("id", "ID"))
                        val dayNum = date.dayOfMonth.toString()

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(46.dp)
                                .background(
                                    if (isSelected) PixelGold else PixelBgCard,
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    2.dp,
                                    if (isSelected) Color.White else PixelWoodDark,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.selectDate(date) }
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = dayName.uppercase(),
                                style = PixelTypography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    color = if (isSelected) Color.Black else EarthSand,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = dayNum,
                                style = PixelTypography.titleMedium.copy(
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. 5 Prayer Checklist Card for Selected Date
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .background(PixelBgCard, RoundedCornerShape(8.dp))
                    .border(2.dp, PixelGold, RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "JADWAL ${state.selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID")))}",
                            style = PixelTypography.titleSmall.copy(
                                color = PixelGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    PrayerType.entries.forEach { prayer ->
                        val log = state.selectedDateLogs.firstOrNull { it.prayerName.equals(prayer.id, ignoreCase = true) }
                        val isLogged = log != null
                        val scheduledTime = state.prayerTimes?.getFormattedTime(prayer) ?: "--:--"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    if (isLogged) Color(0xFF2A1F12) else PixelBgDark,
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isLogged) PixelGreen else PixelBgCardLight,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = prayer.displayName,
                                    style = PixelTypography.titleSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "Jadwal: $scheduledTime WIB",
                                    style = PixelTypography.bodySmall.copy(
                                        color = EarthSand,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            if (isLogged) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (log!!.isOnTime) "TEPAT WAKTU (+100)" else "QADHA (+30)",
                                        style = PixelTypography.labelSmall.copy(
                                            color = if (log.isOnTime) PixelGreen else PixelGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = "[SUDAH ABSEN]",
                                        style = PixelTypography.labelSmall.copy(
                                            color = PixelGreen,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            } else {
                                RetroButton(
                                    text = "+ CATAT",
                                    onClick = { viewModel.manualLogPrayer(prayer) },
                                    modifier = Modifier.width(72.dp),
                                    height = 26.dp,
                                    primaryColor = PixelWoodLight,
                                    darkShadowColor = PixelWoodDark,
                                    textColor = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
