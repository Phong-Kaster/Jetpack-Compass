package com.example.jetpackcompass.ui.compass

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcompass.util.CompassUtil
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompassScreen(
    uiState: CompassUiState,
) {
    Compass(
        angle = uiState.azimuth,
        directionText = uiState.directionText
    )
}

@Composable
fun Compass(
    angle: Float,
    directionText: String
) {
    val primaryAngle = (angle + 360).mod(360f)

    Box(
        modifier = Modifier
            .size(300.dp)
            .graphicsLayer { rotationZ = -angle }
            .background(Color.Black, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        CompassCanvas(primaryAngle)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${primaryAngle.toInt()}°",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = directionText,
                color = Color.White,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun CompassCanvas(primaryAngle: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2

        drawCircle(
            color = Color.Blue,
            radius = radius,
            center = Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
        )

        for (i in 0..359 step 5) {
            val angleInRad = Math.toRadians(i.toDouble())
            val lineLength = if (i % 30 == 0) 18.dp.toPx() else 8.dp.toPx()
            val strokeWidth = if (i % 30 == 0) 3.dp.toPx() else 2.dp.toPx()

            val startRadius = radius - lineLength - 1.dp.toPx()
            val endRadius = radius - 1.dp.toPx()

            val startX = centerX + startRadius * cos(angleInRad)
            val startY = centerY + startRadius * sin(angleInRad)
            val endX = centerX + endRadius * cos(angleInRad)
            val endY = centerY + endRadius * sin(angleInRad)

            drawLine(
                color = if (CompassUtil.isAngleBetween(
                        i.toFloat(),
                        primaryAngle,
                        angleTolerance = 2.5f
                    )
                ) Color.Yellow else Color.DarkGray,
                start = Offset(startX.toFloat(), startY.toFloat()),
                end = Offset(endX.toFloat(), endY.toFloat()),
                strokeWidth = strokeWidth
            )

            if (i % 30 == 0) {
                val textRadius = radius - 34.dp.toPx()
                val textX = centerX + textRadius * cos(angleInRad) - 10.dp.toPx()
                val textY = centerY + textRadius * sin(angleInRad) + 8.dp.toPx()

                drawContext.canvas.nativeCanvas.drawText(
                    "$i°",
                    textX.toFloat(),
                    textY.toFloat(),
                    android.graphics.Paint().apply {
                        textSize = 14.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        color = if (CompassUtil.isAngleBetween(
                                i.toFloat(),
                                primaryAngle,
                                angleTolerance = 15f
                            )
                        ) Color.Yellow.toArgb() else Color.LightGray.toArgb()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun CompassScreenPreview() {
    CompassScreen(
        uiState = CompassUiState(
            azimuth = 120f,
            directionText = "Southeast"
        )
    )
}
