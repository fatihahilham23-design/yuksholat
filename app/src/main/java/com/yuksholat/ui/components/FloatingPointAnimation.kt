package com.yuksholat.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.ui.theme.PixelGold
import com.yuksholat.ui.theme.PixelGreen
import com.yuksholat.ui.theme.PixelTypography

@Composable
fun FloatingPointAnimation(
    pointsEarned: Int,
    isOnTime: Boolean,
    onAnimationEnd: () -> Unit
) {
    var animState by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animState = true
    }

    val offsetY by animateFloatAsState(
        targetValue = if (animState) -80f else 0f,
        animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
        finishedListener = { onAnimationEnd() },
        label = "floating_y"
    )

    val alpha by animateFloatAsState(
        targetValue = if (animState) 0f else 1f,
        animationSpec = keyframes {
            durationMillis = 1600
            1f at 0
            1f at 800
            0f at 1600
        },
        label = "floating_alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (animState) 1.25f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "floating_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = offsetY.dp)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "+$pointsEarned EXP!",
                style = PixelTypography.displayMedium.copy(
                    color = PixelGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (14 * scale).sp
                )
            )
            Text(
                text = if (isOnTime) "TEPAT WAKTU! ???" else "QADHA / TELAT (+30)",
                style = PixelTypography.bodyMedium.copy(
                    color = if (isOnTime) PixelGreen else PixelGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
        }
    }
}
