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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yuksholat.ui.theme.*

@Composable
fun TierLevelUpDialog(
    tierLevel: Int,
    tierName: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(16.dp, RoundedCornerShape(12.dp))
                .background(PixelBgDark, RoundedCornerShape(12.dp))
                .border(4.dp, PixelGold, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "? LEVEL UP! ?",
                    style = PixelTypography.displaySmall.copy(
                        color = PixelGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SELAMAT! KAMU NAIK TIER",
                    style = PixelTypography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tier Badge Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PixelBgCard, RoundedCornerShape(8.dp))
                        .border(2.dp, PixelWoodLight, RoundedCornerShape(8.dp))
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TIER LEVEL $tierLevel",
                            style = PixelTypography.labelSmall.copy(
                                color = PixelGoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tierName,
                            style = PixelTypography.titleLarge.copy(
                                color = PixelGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Terus tingkatkan kedisiplinan shalatmu untuk membuka gelar tertinggi!",
                    style = PixelTypography.bodySmall.copy(
                        color = EarthSand,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                RetroButton(
                    text = "KLAIM & LANJUTKAN",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    primaryColor = PixelGreen,
                    darkShadowColor = PixelGreenDark,
                    textColor = Color.White
                )
            }
        }
    }
}
