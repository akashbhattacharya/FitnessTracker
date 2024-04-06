package com.example.testapp.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun TestAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) {
            darkColors(
                primary = Color.DarkGray,
                onPrimary = Color.White,
                background = Color.Black,
                onBackground = Color.White
            )
        } else {
            lightColors(
                primary = Color.Blue,
                onPrimary = Color.White,
                background = Color.White,
                onBackground = Color.Black
            )
        },
        content = content
    )
}
