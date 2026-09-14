package com.yuksholat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.data.model.PrayerType
import com.yuksholat.ui.theme.*

/**
 * 8-Bit Wooden Signboard.
 * Papan kayu mengambang menampilkan hitung mundur waktu shalat berikutnya dan status shalat saat ini.
 */
@Composable
fun WoodenSignboard(
    modifier: Modifier = Modifier,
    currentPrayer: PrayerType,
    nextPrayer: PrayerType,
    nextPrayerTime: String,
    countdownText: String,
    isCurrentPrayerLogged: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .shadow(6.dp, RoundedCornerShape(8.dp))
            .background(PixelWoodDark, RoundedCornerShape(8.dp))
            .border(3.dp, PixelWood, RoundedCornerShape(8.dp))
            .padding(4.dp)
            .background(PixelWoodLight, RoundedCornerShape(4.dp))
            .border(2.dp, PixelWoodDark, RoundedCornerShape(4.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Baris Atas: Shalat Saat Ini & Status Absen
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SHALAT: ${currentPrayer.displayName.uppercase()}",
                style = PixelTypography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.weight(1f),
                maxLines = 1
            )

            val statusText = if (isCurrentPrayerLogged) "[OK]" else "[!]"
            val statusColor = if (isCurrentPrayerLogged) PixelGreen else PixelRed

            Text(
                text = statusText,
                style = PixelTypography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Baris Tengah: Hitung Mundur Shalat Berikutnya (horizontal layout)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x33000000), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = countdownText,
                style = PixelTypography.bodyMedium.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = EarthCream
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${nextPrayer.displayName} $nextPrayerTime",
                style = PixelTypography.labelSmall.copy(
                    fontSize = 8.sp,
                    color = EarthSand
                ),
                maxLines = 1
            )
        }
    }
}
