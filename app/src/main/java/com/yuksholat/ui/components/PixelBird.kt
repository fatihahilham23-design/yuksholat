package com.yuksholat.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yuksholat.ui.theme.*

@Composable
fun PixelBird(
    modifier: Modifier = Modifier,
    facingRight: Boolean = true,
    birdSize: Dp = 24.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wing_flap")
    val wingUp by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wing_position"
    )

    Canvas(modifier = modifier.size(birdSize)) {
        val pw = size.width / 6f
        val ph = size.height / 4f

        fun pixel(x: Int, y: Int, color: Color, wCount: Int = 1, hCount: Int = 1) {
            val actualX = if (facingRight) x else (6 - x - wCount)
            drawRect(
                color = color,
                topLeft = Offset(actualX * pw, y * ph),
                size = Size(pw * wCount, ph * hCount)
            )
        }

        val body = PixelBlue
        val wingLight = EarthSand
        val eye = Color.Black
        val beak = PixelGoldDark

        // Body
        pixel(1, 1, body, 3, 2)
        pixel(2, 3, body, 2, 1)

        // Eye
        if (facingRight) pixel(3, 1, eye, 1, 1) else pixel(1, 1, eye, 1, 1)

        // Beak
        if (facingRight) pixel(4, 2, beak, 1, 1) else pixel(1, 2, beak, 1, 1)

        // Wing (animated up/down)
        val wingY = if (wingUp > 0.5f) 0 else 2
        pixel(2, wingY, wingLight, 2, 1)
    }
}
