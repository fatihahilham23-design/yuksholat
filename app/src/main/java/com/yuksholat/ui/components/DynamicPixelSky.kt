package com.yuksholat.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.yuksholat.data.model.PrayerType
import com.yuksholat.ui.theme.*
import java.time.LocalTime

data class SkyConfig(
    val colors: List<Color>,
    val isNight: Boolean,
    val isSunVisible: Boolean,
    val isMoonVisible: Boolean
)

@Composable
fun DynamicPixelSky(
    modifier: Modifier = Modifier,
    currentTime: LocalTime = LocalTime.now(),
    currentPrayer: PrayerType = PrayerType.DZUHUR
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sky_anim")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 35000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud_anim"
    )
    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_anim"
    )

    val hour = currentTime.hour

    val (skyColors, isNight, isSunVisible, isMoonVisible) = when {
        hour in 4..5 -> SkyConfig(listOf(SkySubuhTop, SkySubuhBottom), true, false, true)
        hour in 6..11 -> SkyConfig(listOf(SkyMorningTop, SkyMorningBottom), false, true, false)
        hour in 12..14 -> SkyConfig(listOf(SkyDzuhurTop, SkyDzuhurBottom), false, true, false)
        hour in 15..17 -> SkyConfig(listOf(SkyAsharTop, SkyAsharBottom), false, true, false)
        hour == 18 -> SkyConfig(listOf(SkyMaghribTop, SkyMaghribMiddle, SkyMaghribBottom), true, false, true)
        else -> SkyConfig(listOf(SkyIsyaTop, SkyIsyaBottom), true, false, true)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Langit Gradasi
            drawRect(
                brush = Brush.verticalGradient(skyColors),
                topLeft = Offset.Zero,
                size = Size(w, h)
            )

            // 2. Bintang Berkedip
            if (isNight) {
                val stars = listOf(
                    Offset(w * 0.12f, h * 0.10f),
                    Offset(w * 0.28f, h * 0.18f),
                    Offset(w * 0.45f, h * 0.08f),
                    Offset(w * 0.62f, h * 0.15f),
                    Offset(w * 0.78f, h * 0.06f),
                    Offset(w * 0.88f, h * 0.22f),
                    Offset(w * 0.20f, h * 0.28f),
                    Offset(w * 0.70f, h * 0.32f),
                    Offset(w * 0.52f, h * 0.24f)
                )

                stars.forEachIndexed { i, pos ->
                    val alphaMod = if (i % 2 == 0) starAlpha else (1.3f - starAlpha).coerceIn(0.2f, 1f)
                    val starColor = Color.White.copy(alpha = alphaMod)
                    drawRect(starColor, Offset(pos.x - 3f, pos.y - 3f), Size(6f, 6f))
                    drawRect(starColor, Offset(pos.x - 6f, pos.y - 1f), Size(12f, 2f))
                    drawRect(starColor, Offset(pos.x - 1f, pos.y - 6f), Size(2f, 12f))
                }
            }

            // 3. Matahari Piksel (Lebih Detail)
            if (isSunVisible) {
                val sunX = w * 0.75f
                val sunY = h * 0.18f
                val sunSize = 40f
                
                // Outer Glow
                drawRect(PixelGold.copy(alpha = 0.3f), Offset(sunX - 8f, sunY - 8f), Size(sunSize + 16f, sunSize + 16f))
                // Base
                drawRect(PixelGold, Offset(sunX, sunY), Size(sunSize, sunSize))
                // Inner highlight
                drawRect(EarthCream, Offset(sunX + 6f, sunY + 6f), Size(sunSize - 12f, sunSize - 12f))
                
                // Rays
                val rayColor = PixelGold.copy(alpha = 0.8f)
                val rs = 8f
                drawRect(rayColor, Offset(sunX - 12f, sunY + 16f), Size(rs, rs))
                drawRect(rayColor, Offset(sunX + sunSize + 4f, sunY + 16f), Size(rs, rs))
                drawRect(rayColor, Offset(sunX + 16f, sunY - 12f), Size(rs, rs))
                drawRect(rayColor, Offset(sunX + 16f, sunY + sunSize + 4f), Size(rs, rs))
            }

            // 4. Bulan Sabit Piksel (Lebih Detail)
            if (isMoonVisible) {
                val moonX = w * 0.80f
                val moonY = h * 0.15f
                val mSize = 36f
                
                // Moon Base
                drawRect(EarthCream, Offset(moonX, moonY), Size(mSize, mSize))
                // Cutout to make it crescent
                drawRect(skyColors.first(), Offset(moonX + 10f, moonY - 6f), Size(mSize, mSize + 12f))
                
                // Crater dots
                drawRect(PixelGold.copy(alpha = 0.4f), Offset(moonX + 4f, moonY + 8f), Size(4f, 4f))
                drawRect(PixelGold.copy(alpha = 0.4f), Offset(moonX + 2f, moonY + 20f), Size(4f, 4f))
            }

            // 5. Awan Bergerak (Lebih Banyak & Bervariasi)
            fun drawCloud(baseX: Float, baseY: Float, scale: Float = 1.0f, speedMod: Float = 1.0f) {
                val cloudColor = if (isNight) Color(0x88FFFFFF) else Color(0xEEFFFFFF)
                val cx = (baseX + cloudOffset * speedMod) % (w + 200f) - 100f
                val s = scale * 6f
                
                // Pixelated cloud shape
                drawRect(cloudColor, Offset(cx + 4 * s, baseY), Size(8 * s, 3 * s))
                drawRect(cloudColor, Offset(cx, baseY + s), Size(16 * s, 4 * s))
                drawRect(cloudColor, Offset(cx + 2 * s, baseY - s), Size(6 * s, 2 * s))
                drawRect(cloudColor, Offset(cx + 8 * s, baseY - 2 * s), Size(4 * s, 2 * s))
            }

            drawCloud(w * 0.05f, h * 0.42f, 1.2f, 1.0f)
            drawCloud(w * 0.40f, h * 0.50f, 0.8f, 0.7f)
            drawCloud(w * 0.75f, h * 0.40f, 1.0f, 1.2f)
            drawCloud(w * 0.20f, h * 0.55f, 0.6f, 0.5f)
            drawCloud(w * 0.90f, h * 0.46f, 0.9f, 0.9f)

            // 6. Tanah Platformer 8-Bit
            val groundY = h * 0.78f
            val groundH = h - groundY

            drawRect(PixelDirtBase, Offset(0f, groundY), Size(w, groundH))

            val dirtTileSize = 16f
            for (gx in 0..(w / dirtTileSize).toInt()) {
                for (gy in 0..(groundH / dirtTileSize).toInt()) {
                    if ((gx + gy) % 3 == 0) {
                        drawRect(
                            PixelDirtTop,
                            Offset(gx * dirtTileSize, groundY + gy * dirtTileSize),
                            Size(dirtTileSize - 2f, dirtTileSize - 2f)
                        )
                    }
                }
            }

            drawRect(PixelGrassBase, Offset(0f, groundY), Size(w, 14f))
            drawRect(PixelGrassTop, Offset(0f, groundY), Size(w, 8f))

            for (gx in 0..(w / 8f).toInt()) {
                val bladeH = if (gx % 2 == 0) 6f else 10f
                drawRect(PixelGrassTop, Offset(gx * 8f, groundY - bladeH + 4f), Size(6f, bladeH))
            }

            // Static trees (rooted in the ground, no movement)
            val treePositions = listOf(0.08f, 0.32f, 0.68f)
            treePositions.forEachIndexed { i, baseXPct ->
                val tx = baseXPct * w
                val treeH = h * 0.12f + (i * 10f)
                val trunkW = treeH * 0.15f
                val trunkH = treeH * 0.35f
                val ty = groundY
                // Trunk
                drawRect(PixelWoodDark, Offset(tx + trunkW * 0.3f, ty - trunkH), Size(trunkW, trunkH))
                drawRect(Color(0xFF3D2314), Offset(tx + trunkW * 0.3f, ty - trunkH), Size(3f, trunkH))
                // Leaves (proportional)
                val leafW = treeH * 0.45f
                val leafH = treeH * 0.22f
                drawRect(PixelGrassBase, Offset(tx, ty - trunkH - leafH), Size(leafW, leafH))
                drawRect(PixelGrassTop, Offset(tx + leafW * 0.08f, ty - trunkH - leafH * 1.8f), Size(leafW * 0.83f, leafH))
                drawRect(PixelGrassTop, Offset(tx + leafW * 0.25f, ty - trunkH - leafH * 2.5f), Size(leafW * 0.5f, leafH * 0.67f))
                // Leaf highlights
                drawRect(EarthMoss.copy(alpha = 0.5f), Offset(tx + leafW * 0.15f, ty - trunkH - leafH * 1.2f), Size(leafH * 0.3f, leafH * 0.3f))
            }

            // Flying birds (daytime only)
            if (!isNight) {
                val birdConfigs = listOf(
                    Triple(0.15f, h * 0.28f, 0.8f),
                    Triple(0.55f, h * 0.22f, 1.1f),
                    Triple(0.85f, h * 0.32f, 0.6f)
                )
                birdConfigs.forEachIndexed { i, (baseXPct, birdY, speed) ->
                    val bx = ((baseXPct * w + cloudOffset * speed * 0.5f) % (w + 150f)) - 40f
                    val birdColor = PixelBlue
                    val wingColor = EarthSand
                    // Body
                    drawRect(birdColor, Offset(bx, birdY), Size(10f, 4f))
                    // Wing (animated up/down via cloudOffset)
                    val wingUp = ((cloudOffset * 0.02f + i * 20f) % 60f) < 30f
                    if (wingUp) {
                        drawRect(wingColor, Offset(bx + 2f, birdY - 4f), Size(6f, 4f))
                    } else {
                        drawRect(wingColor, Offset(bx + 2f, birdY + 4f), Size(6f, 4f))
                    }
                }
            }
        }
    }
}
