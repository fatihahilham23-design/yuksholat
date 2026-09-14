package com.yuksholat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.ui.components.RetroButton
import com.yuksholat.ui.navigation.AppNavigation
import com.yuksholat.ui.screens.splash.SplashScreen
import com.yuksholat.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YukSholatTheme {
                var showSplash by remember { mutableStateOf(true) }
                var showExitDialog by remember { mutableStateOf(false) }

                val backCallback = remember {
                    object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            if (!showSplash) {
                                showExitDialog = true
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    onBackPressedDispatcher.addCallback(this@MainActivity, backCallback)
                }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AnimatedVisibility(
                        visible = !showSplash,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        AppNavigation()
                    }
                    AnimatedVisibility(
                        visible = showSplash,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        SplashScreen(
                            onSplashFinished = { showSplash = false }
                        )
                    }

                    // Exit Confirmation Dialog
                    if (showExitDialog) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.82f)
                                    .background(PixelBgCard, RoundedCornerShape(8.dp))
                                    .border(3.dp, PixelGold, RoundedCornerShape(8.dp))
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "YAKIN KELUAR?",
                                        style = PixelTypography.titleMedium.copy(
                                            color = PixelGold,
                                            fontWeight = FontWeight.ExtraBold,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Progress shalat hari ini tetap tersimpan.",
                                        style = PixelTypography.bodyMedium.copy(
                                            color = EarthCream,
                                            textAlign = TextAlign.Center,
                                            fontSize = 9.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        RetroButton(
                                            text = "BATAL",
                                            onClick = { showExitDialog = false },
                                            modifier = Modifier.weight(1f),
                                            height = 40.dp,
                                            primaryColor = Color(0xFF616161),
                                            darkShadowColor = Color(0xFF424242),
                                            textColor = Color.White
                                        )
                                        RetroButton(
                                            text = "KELUAR",
                                            onClick = { finish() },
                                            modifier = Modifier.weight(1f),
                                            height = 40.dp,
                                            primaryColor = PixelRed,
                                            darkShadowColor = Color(0xFF7B1FA2),
                                            textColor = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
