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
import kotlin.random.Random

@Composable
fun PixelCharacter(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    walkFrame: Int = 0,
    isPraying: Boolean = false,
    facingRight: Boolean = true,
    tierLevel: Int = 1
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_scale"
    )

    val particleAngles = (0 until 8).map { i ->
        infiniteTransition.animateFloat(
            initialValue = i * 45f,
            targetValue = i * 45f + 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle_angle_$i"
        )
    }

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val pw = w / 16f
        val ph = h / 16f

        fun pixel(gridX: Int, gridY: Int, color: Color, wCount: Int = 1, hCount: Int = 1) {
            val actualX = if (facingRight) gridX else (16 - gridX - wCount)
            drawRect(
                color = color,
                topLeft = Offset(actualX * pw, gridY * ph),
                size = Size(pw * wCount, ph * hCount)
            )
        }

        // Dragon Ball Style Fire Aura (Behind Character)
        if (tierLevel > 1) {
            val auraColor = when (tierLevel) {
                2 -> Color(0xFF42A5F5)
                3 -> Color(0xFFFFEB3B)
                4 -> Color(0xFFEF5350)
                5 -> Color(0xFFCE93D8)
                else -> Color.White
            }

            // Large pulsing outer glow circle
            val outerRadius = (minOf(w, h) / 2f) * auraScale
            drawCircle(
                color = auraColor.copy(alpha = 0.12f),
                radius = outerRadius,
                center = Offset(w / 2f, h / 2f)
            )

            // Animated energy particles orbiting
            for (i in 0 until 8) {
                val angle = particleAngles[i].value
                val rad = Math.toRadians(angle.toDouble())
                val orbitRadius = (minOf(w, h) / 2f * 0.6f) * auraScale
                val px = w / 2f + (orbitRadius * kotlin.math.cos(rad)).toFloat()
                val py = h / 2f + (orbitRadius * kotlin.math.sin(rad)).toFloat()
                val alpha = (0.3f * auraScale).coerceIn(0f, 1f)
                val particleSize = (auraScale * 1.5f).coerceAtLeast(1f)
                pixel(
                    (px / pw).toInt().coerceIn(0, 15),
                    (py / ph).toInt().coerceIn(0, 15),
                    auraColor.copy(alpha = alpha),
                    particleSize.toInt(),
                    particleSize.toInt()
                )
            }

            // Core Glow
            pixel(4, 4, auraColor.copy(alpha = 0.2f), 8, 10)
        }

        val kopiahWhite = Color(0xFFFFFFFF)
        val kopiahBlack = Color(0xFF212121)
        val skin = SkinTone
        val bajuKoko = EarthMoss
        val bajuAccent = Color(0xFF6B8C63)
        val pants = Color(0xFF2A1F12)
        val sandals = Color(0xFF6B4226)

        if (isPraying) {
            pixel(2, 10, kopiahWhite, 4, 2)
            pixel(4, 11, skin, 3, 2)
            pixel(6, 9, bajuKoko, 6, 4)
            pixel(10, 11, pants, 4, 3)
            pixel(13, 13, skin, 2, 1)
            return@Canvas
        }

        // 1. Kopiah
        pixel(5, 1, kopiahBlack, 6, 2)
        pixel(6, 1, PixelGold, 4, 1)

        // 2. Wajah & Rambut
        pixel(5, 3, HairBlack, 6, 1)
        pixel(5, 4, skin, 6, 3)
        pixel(5, 6, Color.Black.copy(alpha = 0.12f), 6, 1)
        pixel(5, 4, Color.White.copy(alpha = 0.1f), 6, 1)

        // 3. Mata
        if (facingRight) {
            pixel(8, 5, Color.Black, 1, 1)
            pixel(10, 5, Color.Black, 1, 1)
        } else {
            pixel(5, 5, Color.Black, 1, 1)
            pixel(7, 5, Color.Black, 1, 1)
        }

        // 4. Baju Koko (dengan shading)
        pixel(5, 7, bajuKoko, 6, 5)
        pixel(5, 7, Color.White.copy(alpha = 0.15f), 1, 5)
        pixel(10, 7, Color.Black.copy(alpha = 0.25f), 1, 5)
        pixel(7, 7, bajuAccent, 2, 5)

        // Inner body glow for red tier
        if (tierLevel >= 4) {
            pixel(5, 7, Color(0xFFEF5350).copy(alpha = 0.25f), 6, 5)
            pixel(7, 7, Color(0xFFEF5350).copy(alpha = 0.25f), 2, 5)
        }

        // Lengan
        pixel(4, 8, bajuKoko, 1, 3)
        pixel(4, 11, skin, 1, 1)
        pixel(11, 8, bajuKoko, 1, 3)
        pixel(11, 11, skin, 1, 1)

        // 5. Celana & Kaki Berjalan
        pixel(5, 12, pants, 6, 2)

        when (walkFrame % 4) {
            0 -> {
                pixel(5, 14, pants, 2, 1)
                pixel(9, 14, pants, 2, 1)
                pixel(5, 15, sandals, 2, 1)
                pixel(9, 15, sandals, 2, 1)
            }
            1 -> {
                pixel(4, 14, pants, 2, 1)
                pixel(10, 13, pants, 2, 1)
                pixel(3, 15, sandals, 3, 1)
                pixel(10, 14, sandals, 2, 1)
            }
            2 -> {
                pixel(6, 14, pants, 2, 1)
                pixel(8, 14, pants, 2, 1)
                pixel(6, 15, sandals, 2, 1)
                pixel(8, 15, sandals, 2, 1)
            }
            3 -> {
                pixel(10, 14, pants, 2, 1)
                pixel(4, 13, pants, 2, 1)
                pixel(10, 15, sandals, 3, 1)
                pixel(4, 14, sandals, 2, 1)
            }
        }
    }
}

@Composable
fun PowerUpParticles(
    modifier: Modifier = Modifier,
    particleColor: Color = Color.White,
    isActive: Boolean = false,
    particleCount: Int = 20
) {
    if (!isActive) return

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val progresses = (0 until particleCount).map { i ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200 + i * 50, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particle_$i"
        )
    }

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = minOf(size.width, size.height) / 2f

        repeat(particleCount) { i ->
            val angle = (i * 360f / particleCount)
            val rad = Math.toRadians(angle.toDouble())
            val progress = progresses[i].value
            val radius = maxRadius * progress
            val px = cx + (radius * kotlin.math.cos(rad)).toFloat()
            val py = cy + (radius * kotlin.math.sin(rad)).toFloat()
            val alpha = 1f - progress
            drawRect(
                color = particleColor.copy(alpha = alpha),
                topLeft = Offset(px - 3f, py - 3f),
                size = Size(6f, 6f)
            )
        }
    }
}

