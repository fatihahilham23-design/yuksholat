package com.yuksholat.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.yuksholat.ui.theme.*

/**
 * 8-Bit Pixel Mosque.
 * Render masjid retro 8-bit lengkap dengan kubah hijau, bulan sabit emas, menara,
 * dan pintu melengkung tempat karakter masuk shalat.
 */
@Composable
fun PixelMosque(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val pw = w / 32f
        val ph = h / 32f

        fun pixel(x: Int, y: Int, color: Color, wCount: Int = 1, hCount: Int = 1) {
            drawRect(
                color = color,
                topLeft = Offset(x * pw, y * ph),
                size = Size(pw * wCount, ph * hCount)
            )
        }

        val wallColor = Color(0xFFE8D5B7)
        val wallDark = Color(0xFFC4A882)
        val domeGreen = Color(0xFF4A6741)
        val domeLight = Color(0xFF5B8C5A)
        val domeDark = Color(0xFF3E6B3E)
        val gold = PixelGold
        val doorDark = Color(0xFF1A1208)
        val doorGlow = Color(0xFFC9A06C)
        val highlight = Color(0xFF7A9468)

        // 1. Bulan Sabit Emas di Puncak Kubah (Crescent Moon)
        pixel(15, 1, gold, 2, 1)
        pixel(14, 2, gold, 1, 2)
        pixel(16, 2, gold, 1, 2)
        pixel(15, 3, gold, 1, 1)
        pixel(15, 4, gold, 2, 2) // Tiang

        // 2. Kubah Utama (Main Green Dome)
        pixel(13, 6, domeLight, 6, 1)
        pixel(11, 7, domeLight, 10, 2)
        pixel(9, 9, domeGreen, 14, 2)
        pixel(8, 11, domeDark, 16, 3)

        // Highlight Kubah
        pixel(12, 7, highlight, 2, 2)
        pixel(10, 9, highlight, 3, 2)

        // 3. Badan Masjid Utama (Main Mosque Building)
        pixel(6, 14, wallColor, 20, 14)
        pixel(6, 14, Color.White.copy(alpha = 0.12f), 2, 14)
        pixel(24, 14, Color.Black.copy(alpha = 0.18f), 2, 14)
        pixel(5, 14, wallDark, 1, 14)
        pixel(26, 14, wallDark, 1, 14)

        // Aksen Garis Emas
        pixel(6, 14, gold, 20, 1)

        // 4. Menara Kiri (Left Minaret)
        pixel(2, 6, gold, 2, 1) // Moon
        pixel(2, 7, domeGreen, 2, 2) // Dome kecil
        pixel(1, 9, wallColor, 4, 19) // Body
        pixel(1, 9, wallDark, 1, 19)
        pixel(2, 12, gold, 2, 1) // Balcony ring
        pixel(2, 18, gold, 2, 1)
        pixel(2, 14, doorDark, 2, 2) // Window

        // 5. Menara Kanan (Right Minaret)
        pixel(28, 6, gold, 2, 1) // Moon
        pixel(28, 7, domeGreen, 2, 2) // Dome kecil
        pixel(27, 9, wallColor, 4, 19) // Body
        pixel(30, 9, wallDark, 1, 19)
        pixel(28, 12, gold, 2, 1)
        pixel(28, 18, gold, 2, 1)
        pixel(28, 14, doorDark, 2, 2) // Window

        // 6. Pintu Masuk Lengkung Utama (Main Arched Entrance Door)
        pixel(13, 19, domeGreen, 6, 1) // Arch top
        pixel(12, 20, doorDark, 8, 8)  // Door interior
        pixel(14, 21, doorGlow, 4, 7)  // Glowing carpet/interior light
        pixel(11, 20, wallDark, 1, 8)  // Frame
        pixel(20, 20, wallDark, 1, 8)  // Frame

        // 7. Jendela Lengkung Samping
        pixel(8, 17, domeGreen, 3, 1)
        pixel(8, 18, doorDark, 3, 4)
        pixel(9, 19, doorGlow, 1, 3)

        pixel(21, 17, domeGreen, 3, 1)
        pixel(21, 18, doorDark, 3, 4)
        pixel(22, 19, doorGlow, 1, 3)

        // 8. Anak Tangga Masjid (Steps)
        pixel(10, 27, EarthSand, 12, 1)
        pixel(8, 28, PixelWoodDark, 16, 1)
    }
}
