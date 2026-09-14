package com.yuksholat.ui.screens.splash

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuksholat.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val splashQuotes = listOf(
    "Shalat tepat waktu membuka pintu rezeki",
    "Subuh adalah awal pertempuran hari",
    "Masjid adalah rumah Allah di bumi",
    "Shalat adalah tiang agama",
    "Jangan tunda shalat, waktu tidak menunggu",
    "Konsisten kecil lebih baik dari semangat sesaat",
    "Setiap shalat adalah undangan Allah",
    "Istiqomah lebih baik dari seribu keindahan",
    "Shalat menghilangkan kegelisahan hati",
    "Hari ini lebih baik dari kemarin",
    "Subuh dan Isya waktu yang dilupakan",
    "Bersyukur dengan shalat 5 waktu",
    "Mulai hari dengan Bismillah dan Subuh",
    "Shalat membawa keberkahan dalam hidup",
    "Jadikan shalat kebiasaan, bukan beban",
    "Ketenangan ada dalam ruku dan sujud",
    "Jaga shalatmu sebelum kamu dishalatkan",
    "Waktu shalat adalah waktu terbaik",
    "Allah menunggu hambanya di setiap waktu",
    "Shalat adalah hadiah terbaik untuk diri"
)

@Composable
fun WavingCharacter(
    modifier: Modifier = Modifier,
    charSize: Dp = 120.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveFrame by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_frame"
    )

    Canvas(modifier = modifier.size(charSize)) {
        val w = size.width
        val h = size.height
        val pw = w / 16f
        val ph = h / 24f

        fun pixel(x: Int, y: Int, color: Color, wc: Int = 1, hc: Int = 1) {
            drawRect(
                color = color,
                topLeft = Offset(x * pw, y * ph),
                size = Size(pw * wc, ph * hc)
            )
        }

        val skin = SkinTone
        val baju = EarthMoss
        val bajuDark = Color(0xFF3E6B3E)
        val pants = Color(0xFF2A1F12)
        val waving = waveFrame > 0.5f

        pixel(5, 1, HairBlack, 6, 2)
        pixel(5, 3, skin, 6, 4)
        pixel(6, 4, Color.Black, 1, 1)
        pixel(9, 4, Color.Black, 1, 1)
        pixel(7, 6, Color(0xFFC0392B), 2, 1)
        pixel(5, 8, baju, 6, 8)
        pixel(5, 8, bajuDark, 1, 8)
        pixel(2, 9, baju, 3, 6)
        pixel(2, 15, skin, 3, 2)
        if (waving) {
            pixel(11, 3, baju, 3, 3)
            pixel(12, 1, skin, 2, 2)
            pixel(11, 0, skin, 1, 1)
        } else {
            pixel(11, 9, baju, 3, 6)
            pixel(11, 15, skin, 3, 2)
        }
        pixel(5, 16, pants, 6, 3)
        pixel(5, 19, pants, 2, 3)
        pixel(9, 19, pants, 2, 3)
        pixel(5, 22, Color(0xFF6B4226), 2, 1)
        pixel(9, 22, Color(0xFF6B4226), 2, 1)
    }
}

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var quoteIndex by remember { mutableStateOf((0 until splashQuotes.size).random()) }
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 3000, easing = LinearEasing),
        label = "splash_progress"
    )

    LaunchedEffect(Unit) {
        var finished = false
        launch {
            while (!finished) {
                delay(2500)
                quoteIndex = (0 until splashQuotes.size).random()
            }
        }
        delay(3000)
        finished = true
        onSplashFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PixelBgDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🕌 YUK SHOLAT",
            style = PixelTypography.displayLarge.copy(
                color = PixelGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Menjaga Shalat, Menguatkan Iman",
            style = PixelTypography.bodyMedium.copy(
                color = EarthSand,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(EarthCream, RoundedCornerShape(12.dp))
                .border(3.dp, PixelGold, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "\"${splashQuotes[quoteIndex]}\"",
                    style = PixelTypography.bodyMedium.copy(
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "👋 Pejuang Shalat",
                    style = PixelTypography.labelSmall.copy(
                        color = PixelGoldDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        WavingCharacter(charSize = 180.dp)
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(16.dp)
                .background(Color.Black, RoundedCornerShape(4.dp))
                .border(2.dp, PixelGold, RoundedCornerShape(4.dp))
                .padding(2.dp)
        ) {
            val filledWidth = progress
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(filledWidth)
                    .background(PixelGold, RoundedCornerShape(2.dp))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Memuat... ${(progress * 100).toInt()}%",
            style = PixelTypography.labelSmall.copy(
                color = EarthSand,
                fontSize = 10.sp
            )
        )
    }
}
