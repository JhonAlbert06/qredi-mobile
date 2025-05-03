package com.pixelbrew.qredi.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 🎨 Paleta pastel verde ajustada y optimizada
val GreenPrimary = Color(0xFFA2D9A0)       // Verde pastel más vivo (mejora contraste)
val GreenSecondary = Color(0xFFBEE8C3)     // Verde más claro
val GreenTertiary = Color(0xFFDFF5E1)      // Verde grisáceo muy suave
val GreenContainer = Color(0xFFF4FBF6)     // Casi blanco verdoso

val OnGreenDark = Color(0xFF233123)        // Más oscuro para texto sobre fondo verde
val OnGreenLight = Color(0xFFFFFFFF)       // Blanco para texto sobre primarios oscuros

// 🌙 Tema Oscuro
val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenTertiary,
    background = Color(0xFF121212),        // Mantengo negro profundo
    surface = Color(0xFF1C1C1C),           // Un gris oscuro menos duro
    onPrimary = OnGreenLight,
    onSecondary = OnGreenLight,
    onTertiary = OnGreenLight,
    onBackground = Color.White,
    onSurface = Color.White
)

// ☀️ Tema Claro
val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenTertiary,
    background = Color(0xFFF9FAF9),        // Muy claro, casi blanco → mejor contraste
    surface = GreenContainer,               // Mantengo claro para cards/dialogs
    onPrimary = OnGreenDark,
    onSecondary = OnGreenDark,
    onTertiary = OnGreenDark,
    onBackground = Color(0xFF222222),      // Más suave que negro puro
    onSurface = Color(0xFF222222)          // Igual que onBackground
)

@Composable
fun QrediTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    //darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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