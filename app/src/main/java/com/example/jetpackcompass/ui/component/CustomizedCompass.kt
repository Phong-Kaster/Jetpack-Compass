package com.example.jetpackcompass.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcompass.domain.enums.CompassDesign

@Composable
fun CustomizedCompass(
    azimuth: Float,
    qiblaBearing: Float,
    compassDesign: CompassDesign = CompassDesign.Default,
    modifier: Modifier = Modifier,
) {
    // Device direction
    val animatedAzimuth by animateFloatAsState(
        targetValue = -azimuth,
        animationSpec = tween(durationMillis = 300),
        label = "dialRotation"
    )

    // Qibla direction
    val animatedQibla by animateFloatAsState(
        targetValue = qiblaBearing - azimuth,
        animationSpec = tween(durationMillis = 300),
        label = "qiblaRotation"
    )


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(350.dp)
    ) {
        // 🧭 Dial (rotates)
        Image(
            painter = painterResource(compassDesign.dialIcon),
            contentScale = ContentScale.Fit,
            contentDescription = "Dial",
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    rotationZ = animatedAzimuth
                },
        )

        // 🧭 North icon (rotates with dial)
        Image(
            painter = painterResource(compassDesign.northIcon),
            contentScale = ContentScale.Fit,
            contentDescription = "northIcon",
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    rotationZ = animatedAzimuth
                },
        )

        // Needle (fixed)
        if (compassDesign.needle != 0) {
            Image(
                painter = painterResource(compassDesign.needle),
                contentScale = ContentScale.Fit,
                contentDescription = "Needle",
                modifier = Modifier
                    .matchParentSize(),
            )
        }


        // 🕋 Qibla icon (relative rotation)
        Image(
            painter = painterResource(compassDesign.qiblaIcon),
            contentDescription = "Qibla",
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    rotationZ = animatedQibla
                }
        )
    }

}

@Preview
@Composable
private fun PreviewCustomizedCompass() {
    CustomizedCompass(
        azimuth = 0f,
        qiblaBearing = 298f,
        modifier = Modifier,
    )
}