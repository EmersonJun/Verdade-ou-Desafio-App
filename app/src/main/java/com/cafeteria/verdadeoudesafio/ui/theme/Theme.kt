package com.cafeteria.verdadeoudesafio.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.cafeteria.verdadeoudesafio.ui.theme.DarkBackground
import com.cafeteria.verdadeoudesafio.ui.theme.DarkCard
import com.cafeteria.verdadeoudesafio.ui.theme.NeonBlue
import com.cafeteria.verdadeoudesafio.ui.theme.NeonRed
import com.cafeteria.verdadeoudesafio.ui.theme.NeonRedGlow
private val DarkColorScheme = darkColorScheme(
    primary = NeonRed,
    secondary = NeonBlue,
    tertiary = NeonRedGlow,
    background = DarkBackground,
    surface = DarkCard
)

private val LightColorScheme = lightColorScheme(
    primary = NeonRed,
    secondary = NeonBlue,
    tertiary = NeonRedGlow,
    background = DarkBackground,
    surface = DarkCard
)

@Composable
fun VerdadeOuDesafioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}