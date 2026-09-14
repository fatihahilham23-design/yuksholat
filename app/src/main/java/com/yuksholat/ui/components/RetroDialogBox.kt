package com.yuksholat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.ui.theme.*

@Composable
fun RetroDialogBox(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    onConfirm: (() -> Unit)? = null,
    confirmText: String = "OK",
    onDismiss: (() -> Unit)? = null,
    dismissText: String = "BATAL"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(8.dp))
            .background(PixelBgCard, RoundedCornerShape(8.dp))
            .border(3.dp, PixelGold, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = "? $title",
                style = PixelTypography.titleMedium.copy(
                    color = PixelGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message,
                style = PixelTypography.bodyMedium.copy(
                    color = Color.White,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            )

            if (onConfirm != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (onDismiss != null) {
                        RetroButton(
                            text = dismissText,
                            onClick = onDismiss,
                            modifier = Modifier.width(90.dp),
                            height = 36.dp,
                            primaryColor = Color(0xFF616161),
                            darkShadowColor = Color(0xFF424242),
                            textColor = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    RetroButton(
                        text = confirmText,
                        onClick = onConfirm,
                        modifier = Modifier.width(90.dp),
                        height = 36.dp,
                        primaryColor = PixelGreen,
                        darkShadowColor = PixelGreenDark,
                        textColor = Color.White
                    )
                }
            }
        }
    }
}
