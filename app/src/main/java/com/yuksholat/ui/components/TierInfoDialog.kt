package com.yuksholat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.data.local.entity.TierConfig
import com.yuksholat.ui.theme.*

@Composable
fun TierInfoDialog(
    tiers: List<TierConfig>,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f)
                .background(PixelBgCard, RoundedCornerShape(12.dp))
                .border(3.dp, PixelGold, RoundedCornerShape(12.dp))
                .padding(16.dp)
                .clickable(enabled = false) {}, // block dismiss clicks
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "?? DAFTAR TIER PAHLAWAN ??",
                style = PixelTypography.titleLarge.copy(
                    color = PixelGold,
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tiers) { tier ->
                    val color = Color(android.graphics.Color.parseColor(tier.badgeColorHex))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .border(1.dp, color, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "LV.${tier.tierLevel}",
                                    style = PixelTypography.labelSmall.copy(
                                        color = color,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = tier.tierName,
                                    style = PixelTypography.titleMedium.copy(
                                        color = color,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                            Text(
                                text = "${tier.minPoints} - ${if(tier.maxPoints > 100000) "???" else tier.maxPoints} PTS",
                                style = PixelTypography.labelSmall.copy(color = Color.Gray)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tier.description,
                                style = PixelTypography.bodySmall.copy(
                                    color = Color.White,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            RetroButton(
                text = "PAHAM!",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                height = 42.dp
            )
        }
    }
}
