package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = MedicalTealLight,
    onPrimary = MedicalDarkBlue,
    primaryContainer = MedicalTeal,
    onPrimaryContainer = Color.White,
    secondary = MedicalCyan,
    onSecondary = Color.Black,
    secondaryContainer = MedicalNavy,
    onSecondaryContainer = Color.White,
    tertiary = DNAVioletLight,
    onTertiary = Color.Black,
    background = SurfaceDark,
    onBackground = TextPrimaryDark,
    surface = CardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardDarkElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = MedicalTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCECE8),
    onPrimaryContainer = MedicalDarkBlue,
    secondary = MedicalCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6F5EE),
    onSecondaryContainer = Color(0xFF003830),
    tertiary = DNAViolet,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = CardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CardLightElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our precision medicine branding by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

