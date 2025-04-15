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

// 🎨 Definición de colores pastel verdes
val GreenPrimary = Color(0xFFA8D5BA)       // Verde pastel suave
val GreenSecondary = Color(0xFFBEE3C1)     // Verde más claro
val GreenTertiary = Color(0xFFD4ECDD)      // Verde grisáceo muy suave
val GreenContainer = Color(0xFFEAF7EF)     // Casi blanco verdoso

val OnGreenDark = Color(0xFF1E2F23)        // Para texto en fondos claros
val OnGreenLight = Color(0xFFFFFFFF)       // Para texto en fondos oscuros

// 🌙 Tema Oscuro
val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenTertiary,
    background = Color(0xFF121212),
    surface = Color(0xFF000000),
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
    background = Color(0xFFFFFFFF),
    surface = GreenContainer,
    onPrimary = OnGreenDark,
    onSecondary = OnGreenDark,
    onTertiary = OnGreenDark,
    onBackground = Color.Black,
    onSurface = Color.Black
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