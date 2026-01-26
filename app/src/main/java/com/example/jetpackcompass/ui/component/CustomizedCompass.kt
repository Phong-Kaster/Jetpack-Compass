package com.example.jetpackcompass.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcompass.domain.enums.CompassDesign
import com.example.jetpackcompass.util.CompassUtil.normalize180
import com.example.jetpackcompass.util.CompassUtil.normalizeAngle
import com.example.jetpackcompass.util.CompassUtil.shortestAngleDelta

@Composable
fun CustomizedCompass(
    azimuth: Float,
    qiblaBearing: Float,
    compassDesign: CompassDesign = CompassDesign.Default,
    modifier: Modifier = Modifier,
) {
    // Visual angles (bounded)
    var dialAngle by remember { mutableStateOf(0f) }
    var qiblaAngle by remember { mutableStateOf(0f) }

    // Target angles
    val targetDial = normalize180(-azimuth)
    val targetQibla = normalize180(qiblaBearing - azimuth)

    // Smooth shortest-path animation
    val animatedDial by animateFloatAsState(
        targetValue = targetDial,
        animationSpec = tween(200),
        label = "dial"
    )

    val animatedQibla by animateFloatAsState(
        targetValue = targetQibla,
        animationSpec = tween(200),
        label = "qibla"
    )

    // Update bounded state ONLY
    LaunchedEffect(targetDial) {
        dialAngle = targetDial
    }

    LaunchedEffect(targetQibla) {
        qiblaAngle = targetQibla
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(350.dp)
    ) {

        Image(
            painter = painterResource(compassDesign.dialIcon),
            contentDescription = "Dial",
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { rotationZ = animatedDial }
        )

        Image(
            painter = painterResource(compassDesign.northIcon),
            contentDescription = "North",
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { rotationZ = animatedDial }
        )

        if (compassDesign.needle != 0) {
            Image(
                painter = painterResource(compassDesign.needle),
                contentDescription = "Needle",
                modifier = Modifier.matchParentSize()
            )
        }

        Image(
            painter = painterResource(compassDesign.qiblaIcon),
            contentDescription = "Qibla",
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { rotationZ = animatedQibla }
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