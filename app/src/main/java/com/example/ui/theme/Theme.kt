package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val JarkaLightColorScheme = lightColorScheme(
    primary = VerdeBosqueAndino,
    onPrimary = BlancoPuro,
    primaryContainer = VerdeMentaSuave,
    onPrimaryContainer = VerdeBosqueAndino,
    secondary = VerdeHojaCultivo,
    onSecondary = BlancoPuro,
    secondaryContainer = DoradoSuave,
    onSecondaryContainer = TextoPrincipal,
    tertiary = AzulCieloNasa,
    onTertiary = BlancoPuro,
    tertiaryContainer = AzulCieloSuave,
    onTertiaryContainer = TextoPrincipal,
    background = FondoTierraClara,
    onBackground = TextoPrincipal,
    surface = TarjetaBlanca,
    onSurface = TextoPrincipal,
    surfaceVariant = VerdeMentaSuave,
    onSurfaceVariant = TextoSecundario,
    outline = BordeSutil,
    outlineVariant = BordeSutil,
    error = UrgenciaRojo,
    onError = BlancoPuro,
    errorContainer = UrgenciaRojoFondo,
    onErrorContainer = UrgenciaRojo
)

private val JarkaDarkColorScheme = darkColorScheme(
    primary = VerdeMentaSuave,
    onPrimary = VerdeBosqueAndino,
    primaryContainer = VerdeHojaCultivo,
    onPrimaryContainer = VerdeMentaSuave,
    secondary = DoradoAltiplano,
    onSecondary = TextoPrincipal,
    secondaryContainer = VerdeBosqueAndino,
    onSecondaryContainer = TextoClaro,
    tertiary = AzulCieloNasa,
    onTertiary = BlancoPuro,
    tertiaryContainer = SuperficieOscura,
    onTertiaryContainer = TextoClaro,
    background = FondoOscuroAndino,
    onBackground = TextoClaro,
    surface = SuperficieOscura,
    onSurface = TextoClaro,
    surfaceVariant = SuperficieOscura,
    onSurfaceVariant = VerdeMentaSuave,
    outline = BordeOscuro,
    outlineVariant = BordeOscuro,
    error = UrgenciaRojoFondo,
    onError = UrgenciaRojo,
    errorContainer = UrgenciaRojo,
    onErrorContainer = BlancoPuro
)

val JarkaShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun JarkaYapuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) JarkaDarkColorScheme else JarkaLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JarkaTypography,
        shapes = JarkaShapes,
        content = content
    )
}
