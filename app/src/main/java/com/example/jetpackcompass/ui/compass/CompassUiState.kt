package com.example.jetpackcompass.ui.compass

import com.example.jetpackcompass.util.CompassUtil.normalize180
import com.example.jetpackcompass.util.CompassUtil.normalizeAngle

data class CompassUiState(
    val hasLocationPermission: Boolean = false,
    val isGpsEnabled: Boolean = false,

    val azimuth: Float = 0f, // goc hien tai cua thiet bi so voi phia Bac thuc 90 do N/ E/ S/ W
    val directionText: String = "North", // ten cua goc hien tai

    val qiblaBearing: Float? = null, // Huong tuyet doi tu TRUE NORTH toi Qibla, duoc tinh dua vao vi tri hien tai cua thiet bi
) {
    // Relative Qibla angle nghia la thiet bi can xoay bao nhieu do de huong ve Qibla
    val relativeQiblaAngle: Float?
        get() = qiblaBearing?.let { bearing ->
            normalize180(angle = bearing - azimuth)
        }

}