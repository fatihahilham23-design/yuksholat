package com.yuksholat.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.data.model.CityPreset
import com.yuksholat.ui.components.RetroButton
import com.yuksholat.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showCityPicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PixelBgDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "?? PENGATURAN & LOKASI",
                style = PixelTypography.titleLarge.copy(
                    color = PixelGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            )
        }

        // 1. Lokasi & Kota (100% Offline)
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
                    Text(
                        text = "LOKASI WAKTU SHALAT (OFFLINE)",
                        style = PixelTypography.labelSmall.copy(
                            color = PixelGoldDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${state.profile.cityName} (${state.selectedCity.provinceOrCountry})",
                        style = PixelTypography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "Lat: ${state.profile.latitude}, Long: ${state.profile.longitude}, UTC+${state.profile.timezoneOffset.toInt()}",
                        style = PixelTypography.bodySmall.copy(
                            color = EarthSand,
                            fontSize = 10.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RetroButton(
                        text = "GANTI KOTA",
                        onClick = { showCityPicker = !showCityPicker },
                        modifier = Modifier.fillMaxWidth(),
                        height = 42.dp,
                        primaryColor = PixelWoodLight,
                        darkShadowColor = PixelWoodDark,
                        textColor = Color.White
                    )

                    if (showCityPicker) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .background(PixelBgDark, RoundedCornerShape(6.dp))
                                .border(1.dp, PixelWood, RoundedCornerShape(6.dp))
                                .padding(6.dp)
                        ) {
                            state.availableCities.forEach { city ->
                                val isSelected = city.cityName == state.profile.cityName
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectCity(city)
                                            showCityPicker = false
                                        }
                                        .background(
                                            if (isSelected) PixelGold.copy(alpha = 0.2f) else Color.Transparent,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${city.cityName} (${city.provinceOrCountry})",
                                        style = PixelTypography.bodySmall.copy(
                                            color = if (isSelected) PixelGold else Color.White,
                                            fontSize = 11.sp
                                        )
                                    )
                                    if (isSelected) {
                                        Text(
                                            text = "[OK]",
                                            style = PixelTypography.labelSmall.copy(
                                                color = PixelGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Notifikasi & Suara
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(8.dp))
                    .background(PixelBgCard, RoundedCornerShape(8.dp))
                    .border(2.dp, PixelGreen, RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "SUARA & NOTIFIKASI ALARM",
                        style = PixelTypography.labelSmall.copy(
                            color = PixelGreenDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Toggle Sound FX
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Efek Suara 8-Bit",
                                style = PixelTypography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "Suara koin EXP, level-up, klik tombol",
                                style = PixelTypography.bodySmall.copy(
                                    color = EarthSand,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Switch(
                            checked = state.profile.soundEnabled,
                            onCheckedChange = { viewModel.toggleSound(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PixelGold,
                                checkedTrackColor = PixelGoldDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Toggle Background Alarm
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Alarm Jadwal Shalat",
                                style = PixelTypography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "Alarm berbunyi walau app ditutup",
                                style = PixelTypography.bodySmall.copy(
                                    color = EarthSand,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Switch(
                            checked = state.profile.alarmEnabled,
                            onCheckedChange = { viewModel.toggleAlarm(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PixelGreen,
                                checkedTrackColor = PixelGreenDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RetroButton(
                            text = "TEST KOIN",
                            onClick = { viewModel.testSoundEffect() },
                            modifier = Modifier.weight(1f),
                            height = 38.dp,
                            primaryColor = PixelGold,
                            darkShadowColor = PixelGoldDark,
                            textColor = Color.Black
                        )
                        RetroButton(
                            text = "TEST LEVEL UP",
                            onClick = { viewModel.testFanfare() },
                            modifier = Modifier.weight(1f),
                            height = 38.dp,
                            primaryColor = PixelGreen,
                            darkShadowColor = PixelGreenDark,
                            textColor = Color.White
                        )
                    }
                }
            }
        }

        // 3. Info Aplikasi
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .background(PixelBgCard, RoundedCornerShape(8.dp))
                    .border(2.dp, EarthBrown, RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "TENTANG APLIKASI",
                        style = PixelTypography.labelSmall.copy(
                            color = EarthSand,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pixel Prayer Tracker v1.0.0",
                        style = PixelTypography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "100% Offline ? Standar Astronomis Kemenag RI (Subuh 20?, Isya 18?) ? Room Database ? 8-Bit Retro Gamification",
                        style = PixelTypography.bodySmall.copy(
                            color = EarthSand,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Created by iamganteng",
                        style = PixelTypography.labelSmall.copy(
                            color = PixelGold,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Right
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
