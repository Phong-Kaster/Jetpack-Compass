package com.example.jetpackcompass.ui.compass

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompass.data.location.LocationDataSource
import com.example.jetpackcompass.data.location.model.LocationInfo
import com.example.jetpackcompass.data.sensor.CompassSensorDataSource
import com.example.jetpackcompass.domain.common.Constant
import com.example.jetpackcompass.domain.usecase.CalculateQiblaBearingUseCase
import com.example.jetpackcompass.util.CompassUtil.applyLowPassFilter
import com.example.jetpackcompass.util.CompassUtil.convertFromMagneticNorthToTrueNorth
import com.example.jetpackcompass.util.CompassUtil.resolveDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CompassViewModel(
    private val sensorDataSource: CompassSensorDataSource,
    private val locationDataSource: LocationDataSource,
    private val calculateQiblaBearingUseCase: CalculateQiblaBearingUseCase,
) : ViewModel() {

    private val  TAG = this.javaClass.simpleName
    private val _uiState = MutableStateFlow(CompassUiState())
    val uiState = _uiState.asStateFlow()


    /**
     * Start both sensors properly
     */
    private var latestLocation: LocationInfo? = null

    fun start() {
        locationDataSource.start()

        viewModelScope.launch {
            locationDataSource.location.collectLatest { location ->
                latestLocation = location

                Log.d(
                    TAG,
                    "📍 Location update → " +
                            "lat=${location?.latitude}, " +
                            "lng=${location?.longitude}, " +
                            "alt=${location?.altitude}"
                )
            }
        }

        sensorDataSource.start { magneticAzimuth ->

            val trueAzimuth = convertFromMagneticNorthToTrueNorth(
                magneticAzimuth,
                latestLocation
            )

            val filtered = applyLowPassFilter(
                previous = _uiState.value.azimuth,
                current = trueAzimuth,
                alpha = Constant.LOW_PASS_ALPHA
            )
            Log.d(TAG, "Filtered azimuth = $filtered°")

            val location = latestLocation
            val qiblaBearing = if (location != null) {
                calculateQiblaBearingUseCase.execute(
                    userLat = location.latitude,
                    userLng = location.longitude
                )
            } else 0f


            Log.d(TAG, "Magnetic azimuth = $magneticAzimuth°")
            Log.d(TAG, "True azimuth (after declination) = $trueAzimuth°")
            Log.d(TAG, "Qibla bearing = $qiblaBearing°")

            _uiState.value = _uiState.value.copy(
                azimuth = filtered,
                directionText = resolveDirection(filtered),
                qiblaBearing = qiblaBearing
            )
        }
    }



    fun stop() {
        locationDataSource.stop()
        sensorDataSource.stop()
    }



    override fun onCleared() {
        stop()
        super.onCleared()
    }

}
