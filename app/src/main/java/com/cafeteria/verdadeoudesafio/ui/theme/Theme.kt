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

// ============================================
// CORES DO JOGO VERDADE OU DESAFIO
// ============================================
// Cores já definidas em Color.kt:
// val NeonRed = Color(0xFFFF0040)
// val NeonRedGlow = Color(0xFFFF1744)
// val DarkBackground = Color(0xFF0A0A0A)
// val DarkCard = Color(0xFF1A1A1A)
// val NeonBlue = Color(0xFF00D4FF)

// ============================================
// ESQUEMA DE CORES PADRÃO
// ============================================
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
    dynamicColor: Boolean = false, // Desativado para manter as cores do jogo
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