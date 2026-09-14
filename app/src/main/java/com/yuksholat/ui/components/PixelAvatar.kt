package com.yuksholat.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yuksholat.ui.theme.*

/**
 * 8-Bit Pixel Art Avatar.
 * Sesuai spesifikasi PRD: Pas foto berlatar merah (red background), kemeja putih (white shirt),
 * jas hitam (black suit), dan dasi hitam (black tie).
 */
@Composable
fun PixelAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .border(2.dp, PixelGold, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = this.size.width
            val h = this.size.height
            val pw = w / 16f
            val ph = h / 16f

            // 1. Red ID Photo Background (Latar Pas Foto Merah)
            drawRect(
                color = PasFotoRed,
                topLeft = Offset.Zero,
                size = this.size
            )

            // Grid pixel drawing helper
            fun pixel(x: Int, y: Int, color: Color, wCount: Int = 1, hCount: Int = 1) {
                drawRect(
                    color = color,
                    topLeft = Offset(x * pw, y * ph),
                    size = Size(pw * wCount, ph * hCount)
                )
            }

            // 2. Rambut Hitam Retro (Hair)
            pixel(5, 2, HairBlack, 6, 2)
            pixel(4, 3, HairBlack, 8, 2)
            pixel(4, 5, HairBlack, 2, 2)
            pixel(10, 5, HairBlack, 2, 2)

            // 3. Wajah / Kulit (Skin)
            pixel(6, 4, SkinTone, 4, 1)
            pixel(5, 5, SkinTone, 6, 4)
            pixel(6, 9, SkinTone, 4, 1)

            // 4. Mata Pixel (Eyes)
            pixel(6, 6, Color.Black, 1, 1)
            pixel(9, 6, Color.Black, 1, 1)

            // 5. Mulut / Senyum (Mouth)
            pixel(7, 8, Color(0xFFC0392B), 2, 1)

            // 6. Kemeja Putih (White Shirt)
            pixel(6, 10, ShirtWhite, 4, 3)
            pixel(7, 13, ShirtWhite, 2, 3)

            // 7. Jas Hitam (Black Suit)
            pixel(2, 10, SuitBlack, 4, 6)
            pixel(10, 10, SuitBlack, 4, 6)
            pixel(3, 10, SuitGray, 2, 1)
            pixel(11, 10, SuitGray, 2, 1)

            // 8. Dasi Hitam (Black Tie)
            pixel(7, 10, TieBlack, 2, 1) // knot
            pixel(7, 11, TieBlack, 2, 4) // tie body
            pixel(8, 15, TieBlack, 1, 1) // tie tip
        }
    }
}
