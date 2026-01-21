package com.example.jetpackcompass.ui.compass

data class CompassUiState(
    val hasLocationPermission: Boolean = false,
    val isGpsEnabled: Boolean = false,

    val azimuth: Float = 0f,
    val directionText: String = "North"
)