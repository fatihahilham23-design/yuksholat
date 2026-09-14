package com.yuksholat.ui.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.data.local.entity.Achievement
import com.yuksholat.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun iconFor(iconName: String): String = when (iconName) {
    "star" -> "\u2b50"
    "fire" -> "\uD83D\uDD25"
    "fire_big" -> "\uD83D\uDD25\uD83D\uDD25"
    "inferno" -> "\uD83C\uDF0B"
    "sword" -> "\u2694\uFE0F"
    "shield" -> "\uD83D\uDEE1\uFE0F"
    "crown" -> "\uD83D\uDC51"
    "diamond" -> "\uD83D\uDC8E"
    "clock" -> "\u23F0"
    "moon" -> "\uD83C\uDF19"
    "crescent" -> "\uD83C\uDF19"
    "gem" -> "\uD83D\uDC8E"
    else -> "\uD83C\uDFC6"
}

private fun unlockDateLabel(unlockedAt: Long): String {
    if (unlockedAt <= 0) return ""
    val date = Instant.ofEpochMilli(unlockedAt).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID")))
}

@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel,
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
        item {
            Text(
                text = "\uD83C\uDFC6 ACHIEVEMENTS",
                style = PixelTypography.titleLarge.copy(
                    color = PixelGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${state.unlockedCount} / ${state.totalCount} UNLOCKED",
                style = PixelTypography.labelSmall.copy(
                    color = EarthSand,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }

        state.achievements.chunked(2).forEach { row ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { ach ->
                        AchievementCard(
                            achievement = ach,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val unlocked = achievement.isUnlocked
    val borderColor = if (unlocked) PixelGold else PixelBgCardLight
    val cardBg = if (unlocked) PixelBgCard else PixelBgDark

    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(cardBg, RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = iconFor(achievement.iconName),
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (unlocked) achievement.name.uppercase() else "???",
                style = PixelTypography.labelSmall.copy(
                    color = if (unlocked) PixelGold else EarthSand,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (unlocked) achievement.description else "Terkunci. Terus shalat!",
                style = PixelTypography.bodyMedium.copy(
                    color = if (unlocked) Color.White else EarthBrown,
                    fontSize = 10.sp
                ),
                textAlign = TextAlign.Center
            )
            if (unlocked) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = unlockDateLabel(achievement.unlockedAt),
                    style = PixelTypography.labelSmall.copy(
                        color = PixelGreen,
                        fontSize = 9.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
