package com.miga.piggy.utils.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Core colors from the Figma design
object PiggyColors {
    // Light theme colors
    val Purple100 = Color(0xFFE8E1FF)
    val Purple200 = Color(0xFFD1C4E9)
    val Purple300 = Color(0xFFB39DDB)
    val Purple500 = Color(0xFF9C27B0)
    val Purple700 = Color(0xFF7B1FA2)
    val Purple900 = Color(0xFF4A148C)

    val Pink100 = Color(0xFFFCE4EC)
    val Pink200 = Color(0xFFF8BBD9)
    val Pink300 = Color(0xFFF48FB1)
    val Pink500 = Color(0xFFE91E63)
    val Pink700 = Color(0xFFC2185B)

    val Blue100 = Color(0xFFE3F2FD)
    val Blue200 = Color(0xFFBBDEFB)
    val Blue300 = Color(0xFF90CAF9)
    val Blue500 = Color(0xFF2196F3)
    val Blue700 = Color(0xFF1976D2)

    val Orange100 = Color(0xFFFFF3E0)
    val Orange300 = Color(0xFFFFB74D)
    val Orange500 = Color(0xFFFF9800)

    val Green100 = Color(0xFFE8F5E8)
    val Green300 = Color(0xFF81C784)
    val Green500 = Color(0xFF4CAF50)

    val Red300 = Color(0xFFE57373)
    val Red500 = Color(0xFFF44336)

    // Background gradients
    val LightBackground = Color(0xFFF5F3FF)
    val DarkBackground = Color(0xFF1A1B23)
    val DarkSurface = Color(0xFF252631)
    val DarkCard = Color(0xFF2A2B38)
}

// Gradient definitions
object PiggyGradients {
    val IncomeGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF667eea),
            Color(0xFF764ba2)
        )
    )

    val ExpenseGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFf093fb),
            Color(0xFFf5576c)
        )
    )

    val CardGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFffecd2),
            Color(0xFFfcb69f)
        )
    )

    val DarkCardGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF667eea),
            Color(0xFF764ba2)
        )
    )

    val WaterGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4FC3F7),
            Color(0xFF29B6F6)
        )
    )

    val PowerGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFFB74D),
            Color(0xFFFF9800)
        )
    )

    val WifiGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF81C784),
            Color(0xFF4CAF50)
        )
    )

    val PhoneGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF42A5F5),
            Color(0xFF1E88E5)
        )
    )
}

// Light theme color scheme
private val LightColorScheme = lightColorScheme(
    primary = PiggyColors.Purple700,
    onPrimary = Color.White,
    secondary = PiggyColors.Pink500,
    onSecondary = Color.White,
    tertiary = PiggyColors.Blue500,
    onTertiary = Color.White,
    background = PiggyColors.LightBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF4F0FF),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    error = PiggyColors.Red500,
    onError = Color.White
)

// Dark theme color scheme
private val DarkColorScheme = darkColorScheme(
    primary = PiggyColors.Purple300,
    onPrimary = Color(0xFF1C1B1F),
    secondary = PiggyColors.Pink300,
    onSecondary = Color(0xFF1C1B1F),
    tertiary = PiggyColors.Blue300,
    onTertiary = Color(0xFF1C1B1F),
    background = PiggyColors.DarkBackground,
    onBackground = Color(0xFFE6E1E5),
    surface = PiggyColors.DarkSurface,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = PiggyColors.DarkCard,
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    error = PiggyColors.Red300,
    onError = Color(0xFF1C1B1F)
)

// Custom typography
private val PiggyTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp
    )
)

// Custom shapes
private val PiggyShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun PiggyTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PiggyTypography,
        shapes = PiggyShapes,
        content = content
    )
}