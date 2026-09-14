package com.yuksholat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PixelDarkColorScheme = darkColorScheme(
    primary = PixelGold,
    onPrimary = Color.Black,
    primaryContainer = PixelWoodDark,
    onPrimaryContainer = PixelGold,
    secondary = PixelGreen,
    onSecondary = Color.Black,
    tertiary = PixelRed,
    background = PixelBgDark,
    onBackground = EarthCream,
    surface = PixelBgCard,
    onSurface = EarthCream,
    surfaceVariant = PixelBgCardLight,
    onSurfaceVariant = EarthSand,
    outline = EarthBrown
)

@Composable
fun YukSholatTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PixelDarkColorScheme,
        typography = PixelTypography,
        content = content
    )
}
