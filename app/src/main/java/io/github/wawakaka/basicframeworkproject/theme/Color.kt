package io.github.wawakaka.basicframeworkproject.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Primary colors
val PrimaryLight = Color(0xFF0066CC)
val Primary = Color(0xFF0052A3)
val PrimaryDark = Color(0xFF003D7A)

// Secondary colors
val SecondaryLight = Color(0xFF7B68EE)
val Secondary = Color(0xFF6A5ACD)
val SecondaryDark = Color(0xFF483D8B)

// Tertiary colors
val TertiaryLight = Color(0xFF20B2AA)
val Tertiary = Color(0xFF008080)
val TertiaryDark = Color(0xFF006666)

// Surface and background colors
val Surface = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF121212)
val Background = Color(0xFFFFFFFF)
val BackgroundDark = Color(0xFF1A1A1A)

// Error colors
val ErrorLight = Color(0xFFB3261E)
val Error = Color(0xFFC5032B)
val OnError = Color(0xFFFFFFFF)

// Other utility colors
val Outline = Color(0xFF79747E)
val OutlineDark = Color(0xFF938F99)

val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    background = Background,
    surface = Surface,
    error = Error,
    onError = OnError,
    outline = Outline
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = ErrorLight,
    onError = Color(0xFF601E16),
    outline = OutlineDark
)
