package com.yuksholat.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.audio.RetroSoundSynthesizer
import com.yuksholat.ui.theme.*

/**
 * 3D Beveled 8-Bit Retro Button.
 * Tombol dengan efek kedalaman piksel (press-down animation) dan audio feedback klik retro.
 */
@Composable
fun RetroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 56.dp,
    primaryColor: Color = PixelGold,
    darkShadowColor: Color = PixelGoldDark,
    textColor: Color = Color.Black,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val topOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) 4.dp else 0.dp,
        label = "button_press"
    )

    Box(
        modifier = modifier
            .height(height)
            .padding(top = topOffset)
    ) {
        // Base Shadow Layer (3D Effect)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = if (isPressed && enabled) 0.dp else 4.dp)
                .background(if (enabled) darkShadowColor else Color(0xFF424242), RoundedCornerShape(8.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
        )

        // Top Face Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) {
                        Brush.verticalGradient(listOf(primaryColor, primaryColor.copy(alpha = 0.85f)))
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFF757575), Color(0xFF616161)))
                    },
                    RoundedCornerShape(8.dp)
                )
                .border(2.dp, if (enabled) EarthCream else Color(0xFF9E9E9E), RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled
                ) {
                    RetroSoundSynthesizer.playButtonClick()
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = PixelTypography.titleMedium.copy(
                        color = if (enabled) textColor else Color(0xFFBDBDBD),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}
