package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkColorScheme = darkColorScheme(
    primary = LightGreen,
    secondary = DarkGreen,
    tertiary = Brown,
    surface = Mud,
    background = Ceramic,
    primaryContainer = Ceramic,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onSurface = Color.White,
    onBackground = Color.Black,
)

private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    secondary = LightGreen,
    primaryContainer = Ceramic,
    tertiary = Brown,
    surface = Mud,
    background = Ceramic,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onSurface = Color.Black,
    onBackground = Color.Black,
)



@Composable
fun MyApplicationTheme(
   darkTheme: Boolean = isSystemInDarkTheme(),
   dynamicColor: Boolean = true,
   content: @Composable () -> Unit
) {
   val colorScheme = when {

       darkTheme -> DarkColorScheme
       else -> LightColorScheme
   }

   MaterialTheme(
       colorScheme = colorScheme,
       typography = Typography,
       content = content
   )
}