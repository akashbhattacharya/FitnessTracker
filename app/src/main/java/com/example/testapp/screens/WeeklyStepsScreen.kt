package com.example.testapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun WeeklyStepsScreen() {
    val data = listOf(100, 200, 300, 400, 500) // Sample data for the bars
    val maxBarHeight = 200.dp // Max height for the bars

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxBarHeight)
        ) {
            val barWidth = size.width / data.size
            val maxDataValue = data.maxOrNull() ?: 0
            val scaleY = maxBarHeight.toPx() / maxDataValue

            data.forEachIndexed { index, value ->
                val barHeight = value * scaleY
                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(index * barWidth, size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
    }
}
