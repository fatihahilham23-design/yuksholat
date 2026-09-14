package com.yuksholat.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.data.model.PrayerType
import com.yuksholat.ui.components.*
import com.yuksholat.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(PixelBgDark)) {
        val groundFraction = 0.78f
        val groundYDp = maxHeight * groundFraction
        // 1. Dynamic Pixel Sky & Platformer Ground Stage
        DynamicPixelSky(
            modifier = Modifier.fillMaxSize(),
            currentPrayer = state.currentPrayer
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ================= A. AREA ATAS (HUD) =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .background(PixelBgCard.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                    .border(1.dp, PixelGold, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kiri Atas: Avatar + Streak Badge
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PixelAvatar(size = 36.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = state.profile.name.ifEmpty { "PLAYER 1" },
                                style = PixelTypography.labelSmall.copy(
                            color = EarthCream,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = state.profile.cityName,
                            style = PixelTypography.bodySmall.copy(
                                color = EarthSand,
                                fontSize = 9.sp
                            )
                        )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "[RESET]",
                                    style = PixelTypography.labelSmall.copy(
                                        color = PixelRed,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.clickable { viewModel.resetUserProgress() }
                                )
                            }
                        }
                    }
                    if (state.currentStreak > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(PixelGoldDark, RoundedCornerShape(4.dp))
                                .border(1.dp, PixelGold, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "🔥 ${state.currentStreak} HARI",
                                style = PixelTypography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }
                }

                // Tengah Atas: Status Tier Saat Ini
                val currentTier = state.tierConfigs.firstOrNull { it.tierLevel == state.profile.currentTierLevel }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { viewModel.showTierInfo() }
                ) {
                    Text(
                        text = "TIER",
                        style = PixelTypography.labelSmall.copy(
                            color = PixelGoldDark,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = currentTier?.tierName ?: "Pemula Shalat",
                        style = PixelTypography.titleMedium.copy(
                            color = PixelGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1
                    )
                }

                // Kanan Atas: Total Poin & Koin 8-Bit
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0x55000000), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    PixelCoinIcon(size = 20.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${state.profile.totalPoints}",
                        style = PixelTypography.titleMedium.copy(
                            color = PixelGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            // ================= B. AREA TENGAH (PANGGUNG UTAMA) =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Ramadan Banner
                if (state.isRamadan) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 6.dp, start = 12.dp)
                            .background(PixelGold.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .border(1.dp, PixelGold, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🌙 RAMADAN HARI ${state.ramadanDay} — SISA ${state.ramadanDaysLeft} HARI",
                            style = PixelTypography.labelSmall.copy(
                                color = Color.Black,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }

                // Papan Kayu Hitung Mundur Melayang
                WoodenSignboard(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = if (state.isRamadan) 36.dp else 10.dp),
                    currentPrayer = state.currentPrayer,
                    nextPrayer = state.nextPrayer,
                    nextPrayerTime = state.nextPrayerTime,
                    countdownText = state.countdownFormatted,
                    isCurrentPrayerLogged = state.isCurrentPrayerLogged
                )

                // Status Animasi Shalat
                if (state.animationStatusText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color(0xCC000000), RoundedCornerShape(8.dp))
                            .border(2.dp, PixelGold, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = state.animationStatusText,
                            style = PixelTypography.titleMedium.copy(
                                color = PixelGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                // Efek Poin Melayang (+100 EXP!)
                if (state.showFloatingPoints) {
                    FloatingPointAnimation(
                        pointsEarned = state.lastPointsEarned,
                        isOnTime = state.lastIsOnTime,
                        onAnimationEnd = { viewModel.dismissFloatingPoints() }
                    )
                }
            }

            // ================= C. AREA BAWAH (KONTROL) =================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PixelBgDark.copy(alpha = 0.95f))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Checklist 5 Waktu Shalat Hari Ini
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PrayerType.entries.forEach { prayer ->
                        val isLogged = state.todayLogs.any { it.prayerName.equals(prayer.id, ignoreCase = true) }
                        val isCurrent = state.currentPrayer == prayer

                        val pillBg = when {
                            isLogged -> Color(0xFF1E2D24)
                            isCurrent -> PixelGoldDark
                            else -> PixelBgCardLight
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 1.dp)
                                .background(pillBg, RoundedCornerShape(4.dp))
                                .border(
                                    1.5.dp,
                                    if (isCurrent) PixelGold else Color.Transparent,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(vertical = 3.dp, horizontal = 2.dp)
                        ) {
                            Text(
                                text = prayer.displayName,
                                style = PixelTypography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = if (isLogged) "[?]" else state.prayerTimes?.getFormattedTime(prayer) ?: "--:--",
                                style = PixelTypography.labelSmall.copy(
                                    fontSize = 7.sp,
                                    color = if (isLogged) PixelGreen else EarthSand
                                )
                            )
                        }
                    }
                }

                // Daily Goal Progress
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GOAL:",
                        style = PixelTypography.labelSmall.copy(
                            fontSize = 8.sp,
                            color = PixelGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    PrayerType.entries.forEach { prayer ->
                        val logged = state.todayLogs.any { it.prayerName.equals(prayer.id, ignoreCase = true) }
                        Text(
                            text = if (logged) "●" else "○",
                            style = PixelTypography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = if (logged) PixelGreen else Color(0xFF546E7A)
                            )
                        )
                    }
                    Text(
                        text = "${state.dailyGoalProgress.coerceAtMost(5)}/5",
                        style = PixelTypography.labelSmall.copy(
                            fontSize = 8.sp,
                            color = if (state.dailyGoalCompleted) PixelGold else Color(0xFF90A4AE),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Tombol Raksasa "ABSEN SHALAT"
                val buttonText = when {
                    state.isAbsenInProgress -> "SEDANG SHALAT..."
                    state.isCurrentPrayerLogged -> "ABSEN LAGI (QADHA/SUNNAH)"
                    else -> "ABSEN SHALAT (${state.currentPrayer.displayName.uppercase()})"
                }

                val buttonColor = if (state.isCurrentPrayerLogged) PixelGold else PixelGreen
                val buttonShadow = if (state.isCurrentPrayerLogged) PixelGoldDark else PixelGreenDark

                RetroButton(
                    text = buttonText,
                    onClick = { viewModel.performAbsenShalat() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isAbsenInProgress,
                    height = 54.dp,
                    primaryColor = buttonColor,
                    darkShadowColor = buttonShadow,
                    textColor = Color.White
                )
            }
        }

        // ================= D. OVERLAY: MASJID & KARAKTER (anchored ke groundY) =================
        val characterStartX = 32f
        val characterTargetX = 210f
        val currentX = characterStartX + (characterTargetX - characterStartX) * state.characterXProgress

        // Masjid 8-Bit (posisi kanan, bottom art sejajar ground)
        Box(
            modifier = Modifier
                .offset(x = maxWidth - 170.dp - 8.dp, y = groundYDp - 154.dp)
                .size(width = 170.dp, height = 170.dp)
        ) {
            PixelMosque()
        }

        // Karakter Animasi (posisi kiri, bottom sejajar ground)
        if (!state.isInsideMosque) {
            Box(
                modifier = Modifier
                    .offset(x = currentX.dp, y = groundYDp - 52.dp)
                    .size(width = 52.dp, height = 52.dp)
            ) {
                PixelCharacter(
                    size = 52.dp,
                    walkFrame = state.characterWalkFrame,
                    facingRight = state.isFacingRight,
                    tierLevel = state.profile.currentTierLevel
                )
            }
        }

        // Dialog Transformasi Tier (Goku-style)
        if (state.showTransformationDialog) {
            TierTransformationDialog(
                tierLevel = state.transformationTierLevel,
                tierName = state.transformationTierName,
                tierDescription = state.transformationTierDescription,
                auraColorHex = state.transformationAuraColor,
                onDismiss = { viewModel.dismissTransformationDialog() }
            )
        }

        // Achievement Toast
        if (state.showAchievementToast) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { viewModel.dismissAchievementToast() },
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(PixelBgCard, RoundedCornerShape(12.dp))
                        .border(2.dp, PixelGold, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆 ACHIEVEMENT TERBUKA!",
                        style = PixelTypography.titleSmall.copy(
                            color = PixelGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    state.newlyUnlockedAchievements.forEach { ach ->
                        Text(
                            text = "${ach.iconEmoji()} ${ach.name}",
                            style = PixelTypography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = ach.description,
                            style = PixelTypography.bodySmall.copy(color = Color(0xFFB0BEC5), fontSize = 9.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    RetroButton(
                        text = "MENGERTI!",
                        onClick = { viewModel.dismissAchievementToast() },
                        modifier = Modifier.fillMaxWidth(),
                        height = 40.dp,
                        primaryColor = PixelGreen,
                        darkShadowColor = PixelGreenDark,
                        textColor = Color.White
                    )
                }
            }
            }

        // Onboarding First-Session Guide (render paling akhir = di atas semua layer)
        androidx.compose.animation.AnimatedVisibility(
            visible = state.showOnboardingGuide,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                RetroDialogBox(
                    title = "MISI PERTAMA: ABSEN SEKARANG!",
                    message = "Selamat datang di Yuk Sholat! Tekan tombol 'ABSEN SHALAT' di bawah untuk melakukan shalat pertamamu dan raih 100 Poin perdana!",
                    confirmText = "MULAI!",
                    onConfirm = { viewModel.dismissOnboardingGuide() },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Dialog Penjelasan Tier
        if (state.showTierInfoDialog) {
            TierInfoDialog(
                tiers = state.tierConfigs,
                onDismiss = { viewModel.dismissTierInfo() }
            )
        }

        // Dialog Kekecewaan (Missed Shalat)
        if (state.showDisappointmentDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { viewModel.dismissDisappointment() }
                , contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(PixelBgCard, RoundedCornerShape(12.dp))
                        .border(3.dp, PixelRed, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "?? KECEWA...",
                        style = PixelTypography.titleLarge.copy(color = PixelRed)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Kamu sudah melewatkan 3 waktu shalat hari ini. Kedisiplinan adalah kunci pahlawan sejati. Jangan menyerah, ayo absen shalat berikutnya!",
                        style = PixelTypography.bodyMedium.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    RetroButton(
                        text = "SAYA AKAN BERUSAHA!",
                        onClick = { viewModel.dismissDisappointment() },
                        primaryColor = PixelRed,
                        darkShadowColor = Color(0xFF7B1FA2)
                    )
                }
            }
        }

        // Dialog Input Nama (Pertama Kali)
        if (state.showNameInputDialog) {
            var nameInput by remember { mutableStateOf("") }
            
            LaunchedEffect(Unit) {
                delay(300) // Small delay to ensure UI is ready
                focusRequester.requestFocus()
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable(enabled = true, onClick = {}) // block clicks to background
                , contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(PixelBgCard, RoundedCornerShape(8.dp))
                        .border(4.dp, PixelGold, RoundedCornerShape(8.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SIAPA NAMAMU, PEJUANG?",
                        style = PixelTypography.titleMedium.copy(
                            color = PixelGold,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    TextField(
                        value = nameInput,
                        onValueChange = { if (it.length <= 12) nameInput = it },
                        placeholder = { 
                            Text(
                                "MASUKKAN NAMA...", 
                                style = PixelTypography.bodyMedium.copy(color = Color.Gray)
                            ) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .border(2.dp, PixelGoldDark),
                        textStyle = PixelTypography.bodyLarge.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Black,
                            unfocusedContainerColor = Color.Black,
                            cursorColor = PixelGold,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    RetroButton(
                        text = "MULAI PETUALANGAN!",
                        onClick = { if (nameInput.isNotBlank()) viewModel.setProfileName(nameInput) },
                        enabled = nameInput.isNotBlank(),
                        height = 50.dp,
                        primaryColor = if (nameInput.isNotBlank()) PixelGreen else Color.Gray,
                        darkShadowColor = if (nameInput.isNotBlank()) PixelGreenDark else Color.DarkGray
                    )
                }
            }
        }
    }
}
