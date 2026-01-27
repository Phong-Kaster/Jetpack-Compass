package com.example.jetpackcompass.data.repository

import com.example.jetpackcompass.data.location.LocationDataSource
import com.example.jetpackcompass.data.location.model.LocationInfo
import com.example.jetpackcompass.data.sensor.CompassSensorDataSource
import com.example.jetpackcompass.domain.common.Constant
import com.example.jetpackcompass.domain.model.CompassState
import com.example.jetpackcompass.domain.usecase.CalculateQiblaBearingUseCase
import com.example.jetpackcompass.ui.compass.CompassUiState
import com.example.jetpackcompass.util.CompassUtil.applyLowPassFilter
import com.example.jetpackcompass.util.CompassUtil.convertFromMagneticNorthToTrueNorth
import com.example.jetpackcompass.util.CompassUtil.resolveDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class CompassRepository(
    private val sensorDataSource: CompassSensorDataSource,
    private val locationDataSource: LocationDataSource,
    private val calculateQiblaBearingUseCase: CalculateQiblaBearingUseCase,
) {

    private val _state = MutableStateFlow(CompassState())
    val state: StateFlow<CompassState> = _state.asStateFlow()

    private var latestLocation: LocationInfo? = null
    private var sensorStarted = false
    private var locationStarted = false

    /************************
     * star all data sources to provide data to compass
     */
    fun start(scope: CoroutineScope) {
        if (!locationStarted) {
            locationStarted = true
            locationDataSource.start()

            scope.launch {
                locationDataSource.location
                    .filterNotNull()
                    .distinctUntilChanged { old, new ->
                        old.latitude == new.latitude && old.longitude == new.longitude
                    }
                    .collect { location ->
                        latestLocation = location
                    }
            }
        }

        if (!sensorStarted) {
            sensorStarted = true
            sensorDataSource.start { magneticAzimuth ->
                // chuyển đổi từ Magnetic North sang True North
                val trueAzimuth = convertFromMagneticNorthToTrueNorth(
                    magneticAzimuth = magneticAzimuth,
                    location = latestLocation
                )

                // áp dụng bộ lọc giảm nhiễu, tránh để kim la bàn nhảy lung tung
                val filtered = applyLowPassFilter(
                    previous = _state.value.azimuth,
                    current = trueAzimuth,
                    alpha = Constant.LOW_PASS_ALPHA
                )


                // Qibla bearing trả lời cho
                // cau hỏi là "Nếu tôi đang quay mặt về Bắc cực (True North), thì tôi cần quay bao nhiêu độ để hướng về phía Qibla?"
                // Output range: 0° … 360°
                // Ví dụ: Qibla bearing = 120° nghĩa là từ True North, cần quay 120° theo chiều kim đồng hồ để hướng về Qibla
                val qiblaBearing = latestLocation?.let {
                    calculateQiblaBearingUseCase.execute(
                        userLat = it.latitude,
                        userLng = it.longitude
                    )
                }

                _state.value = _state.value.copy(
                    azimuth = filtered,
                    directionText = resolveDirection(filtered),
                    qiblaBearing = qiblaBearing
                )
            }
        }
    }

    /************************
     * stop all data sources
     */
    fun stop() {
        sensorDataSource.stop()
        locationDataSource.stop()
        sensorStarted = false
        locationStarted = false
    }
}
