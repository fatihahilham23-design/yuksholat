package com.yuksholat.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yuksholat.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class SparkleParticle(
    val angleDeg: Float,
    val maxDistance: Float,
    val size: Float,
    val delayFactor: Float,
    val color: Color
)

@Composable
fun TierTransformationDialog(
    tierLevel: Int,
    tierName: String,
    tierDescription: String,
    auraColorHex: String,
    onDismiss: () -> Unit
) {
    val auraColor = Color(AndroidColor.parseColor(auraColorHex))

    val chargePalette = listOf(
        Color(0xFF000000),
        Color(0xFF42A5F5),
        Color(0xFFFFEB3B),
        Color(0xFFEF5350),
        Color(0xFFCE93D8),
        auraColor
    )

    var phase by remember { mutableStateOf(0) }

    val chargeProgress by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "charge_progress"
    )

    val flashIntensity by animateFloatAsState(
        targetValue = if (phase == 1) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = 300
            0f at 0
            1f at 50
            0.8f at 150
            0.2f at 300
        },
        label = "flash_intensity"
    )

    val revealProgress by animateFloatAsState(
        targetValue = if (phase >= 2) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = 1000
            0f at 0
            0.2f at 150
            0.6f at 400
            1f at 700
            1f at 1000
        },
        label = "reveal_progress"
    )

    LaunchedEffect(Unit) {
        delay(800)
        phase = 1
        delay(300)
        phase = 2
    }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val textPulseScale by pulseTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val textRotation by pulseTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_rotation"
    )

    val shakeX = remember { Animatable(0f, Float.VectorConverter) }
    val shakeY = remember { Animatable(0f, Float.VectorConverter) }

    LaunchedEffect(phase) {
        if (phase == 1) {
            while (true) {
                shakeX.snapTo((Random.nextFloat() - 0.5f) * 40f)
                shakeY.snapTo((Random.nextFloat() - 0.5f) * 40f)
                delay(30)
            }
        }
    }

    val particles = remember(tierLevel) {
        List(40) { i ->
            SparkleParticle(
                angleDeg = Random.nextFloat() * 360f,
                maxDistance = Random.nextFloat() * 200f + 100f,
                size = Random.nextFloat() * 10f + 4f,
                delayFactor = Random.nextFloat() * 0.4f,
                color = if (i % 3 == 0) Color.White else auraColor
            )
        }
    }

    val buttonAlpha by animateFloatAsState(
        targetValue = if (phase >= 2 && revealProgress >= 0.8f) 1f else 0f,
        animationSpec = tween(300),
        label = "button_alpha"
    )

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (phase) {
                0 -> ChargePhase(chargeProgress, tierName, textPulseScale, textRotation, chargePalette)
                1 -> FlashPhase(flashIntensity, shakeX.value, shakeY.value)
                2 -> RevealPhase(
                    tierLevel, tierName, tierDescription, auraColor,
                    revealProgress, particles, buttonAlpha, onDismiss
                )
            }
        }
    }
}

@Composable
private fun ChargePhase(
    progress: Float,
    tierName: String,
    pulseScale: Float,
    rotation: Float,
    palette: List<Color>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(interpolateChargeColor(progress, palette)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "KAMU NAIK KE TIER",
                style = PixelTypography.titleLarge.copy(
                    color = Color.White.copy(alpha = 0.7f + progress * 0.3f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tierName.uppercase(),
                modifier = Modifier.graphicsLayer(
                    scaleX = pulseScale,
                    scaleY = pulseScale,
                    rotationZ = rotation
                ),
                style = PixelTypography.displayLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FlashPhase(
    flashIntensity: Float,
    shakeXVal: Float,
    shakeYVal: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .graphicsLayer {
                translationX = shakeXVal
                translationY = shakeYVal
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "!!",
            modifier = Modifier.graphicsLayer(
                scaleX = 0.8f + flashIntensity * 0.4f,
                scaleY = 0.8f + flashIntensity * 0.4f
            ),
            style = PixelTypography.displayLarge.copy(
                color = Color.Black.copy(alpha = flashIntensity),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 96.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RevealPhase(
    tierLevel: Int,
    tierName: String,
    tierDescription: String,
    auraColor: Color,
    revealProgress: Float,
    particles: List<SparkleParticle>,
    buttonAlpha: Float,
    onDismiss: () -> Unit
) {
    val contentScale = 0.5f + revealProgress * 0.5f
    val contentAlpha = ((revealProgress - 0.1f) / 0.9f).coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            auraColor.copy(alpha = 0.12f * revealProgress),
                            PixelBgDark,
                            Color(0xFF0A0804)
                        ),
                        radius = 800f
                    )
                )
        )

        Canvas(modifier = Modifier.matchParentSize()) {
            particles.forEach { p ->
                val localProgress = if (revealProgress > p.delayFactor) {
                    ((revealProgress - p.delayFactor) / (1f - p.delayFactor)).coerceIn(0f, 1f)
                } else 0f
                if (localProgress <= 0f) return@forEach

                val dist = p.maxDistance * localProgress
                val x = size.width / 2f + cos(Math.toRadians(p.angleDeg.toDouble())).toFloat() * dist
                val y = size.height / 2f + sin(Math.toRadians(p.angleDeg.toDouble())).toFloat() * dist
                val alpha = if (localProgress < 0.5f) localProgress * 2f else (1f - localProgress) * 2f
                val radius = p.size * (0.5f + localProgress * 0.5f)

                drawCircle(
                    color = p.color.copy(alpha = alpha * 0.8f),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = contentScale,
                    scaleY = contentScale,
                    alpha = contentAlpha
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(auraColor.copy(alpha = 0.12f * revealProgress), RoundedCornerShape(12.dp))
                    .border(2.dp, auraColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TIER $tierLevel",
                        style = PixelTypography.labelLarge.copy(
                            color = auraColor,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = tierName.uppercase(),
                        style = PixelTypography.displayLarge.copy(
                            color = auraColor,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PixelCharacter(
                size = 144.dp,
                tierLevel = tierLevel
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = tierDescription,
                style = PixelTypography.bodyLarge.copy(
                    color = EarthSand,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.graphicsLayer { alpha = buttonAlpha }) {
                RetroButton(
                    text = "LANJUTKAN",
                    onClick = onDismiss,
                    modifier = Modifier.wrapContentWidth(),
                    primaryColor = PixelGold,
                    darkShadowColor = PixelGoldDark,
                    textColor = Color.Black
                )
            }
        }
    }
}

private fun interpolateChargeColor(progress: Float, palette: List<Color>): Color {
    if (progress <= 0f) return palette.first()
    if (progress >= 1f) return palette.last()
    val segment = progress * (palette.size - 1)
    val idx = segment.toInt().coerceIn(0, palette.size - 2)
    val t = segment - idx
    return blendColors(palette[idx], palette[idx + 1], t)
}

private fun blendColors(a: Color, b: Color, t: Float): Color {
    return Color(
        red = a.red + (b.red - a.red) * t,
        green = a.green + (b.green - a.green) * t,
        blue = a.blue + (b.blue - a.blue) * t,
        alpha = a.alpha + (b.alpha - a.alpha) * t
    )
}
