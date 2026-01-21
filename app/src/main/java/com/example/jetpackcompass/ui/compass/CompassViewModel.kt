package com.example.jetpackcompass.ui.compass

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.jetpackcompass.data.sensor.CompassSensorDataSource
import com.example.jetpackcompass.domain.usecase.GetCompassReadingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompassViewModel(
    private val sensorDataSource: CompassSensorDataSource,
    private val getCompassReadingUseCase: GetCompassReadingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompassUiState())
    val uiState = _uiState.asStateFlow()


    private val lowPassAlpha = 0.1f

    fun start() {
        sensorDataSource.start { newAzimuth ->
            val filtered = getCompassReadingUseCase.applyLowPassFilter(
                previous = _uiState.value.azimuth,
                current = newAzimuth,
                alpha = lowPassAlpha
            )

            _uiState.value = _uiState.value.copy(
                azimuth = filtered,
                directionText = resolveDirection(filtered)
            )
        }
    }

    fun stop() {
        sensorDataSource.stop()
    }

    private fun resolveDirection(angle: Float): String {
        val normalized = (angle + 360).mod(360f)
        return when (normalized) {
            in 22.5f..67.5f -> "Northeast"
            in 67.5f..112.5f -> "East"
            in 112.5f..157.5f -> "Southeast"
            in 157.5f..202.5f -> "South"
            in 202.5f..247.5f -> "Southwest"
            in 247.5f..292.5f -> "West"
            in 292.5f..337.5f -> "Northwest"
            else -> "North"
        }
    }

    override fun onCleared() {
        stop()
        super.onCleared()
    }
}
