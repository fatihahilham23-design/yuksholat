package com.yuksholat.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yuksholat.ui.theme.PixelGold
import com.yuksholat.ui.theme.PixelGoldDark

@Composable
fun PixelCoinIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val pw = w / 8f
        val ph = h / 8f

        fun pixel(x: Int, y: Int, color: Color, wc: Int = 1, hc: Int = 1) {
            drawRect(
                color = color,
                topLeft = Offset(x * pw, y * ph),
                size = Size(pw * wc, ph * hc)
            )
        }

        // Coin Outer Circle
        pixel(2, 0, PixelGoldDark, 4, 1)
        pixel(1, 1, PixelGoldDark, 6, 1)
        pixel(0, 2, PixelGoldDark, 8, 4)
        pixel(1, 6, PixelGoldDark, 6, 1)
        pixel(2, 7, PixelGoldDark, 4, 1)

        // Coin Inner Body
        pixel(2, 1, PixelGold, 4, 1)
        pixel(1, 2, PixelGold, 6, 4)
        pixel(2, 6, PixelGold, 4, 1)

        // Coin Shine & Center Mark
        pixel(2, 2, Color(0xFFFFF9C4), 1, 2)
        pixel(3, 3, PixelGoldDark, 2, 2)
    }
}
