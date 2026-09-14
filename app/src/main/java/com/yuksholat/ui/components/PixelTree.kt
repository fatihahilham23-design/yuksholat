package com.yuksholat.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.yuksholat.ui.theme.*

@Composable
fun PixelTree(
    modifier: Modifier = Modifier,
    tall: Boolean = false
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val pw = size.width / 16f
        val ph = size.height / 24f

        fun pixel(x: Int, y: Int, color: Color, wCount: Int = 1, hCount: Int = 1) {
            drawRect(
                color = color,
                topLeft = Offset(x * pw, y * ph),
                size = Size(pw * wCount, ph * hCount)
            )
        }

        val top = if (tall) 2 else 8
        val trunkTop = top + 11
        val trunkH = 24 - trunkTop

        pixel(7, top, PixelGrassTop, 2, 2)
        pixel(6, top + 2, PixelGrassTop, 4, 2)
        pixel(5, top + 4, PixelGrassBase, 6, 2)
        pixel(4, top + 6, PixelGrassBase, 8, 3)
        pixel(6, top + 6, PixelGrassTop, 4, 2)
        pixel(7, trunkTop, PixelWood, 2, trunkH)
        pixel(8, trunkTop, PixelWoodDark, 1, trunkH)
    }
}
