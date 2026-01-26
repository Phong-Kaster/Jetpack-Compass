// data/location/LocationDataSource.kt
package com.example.jetpackcompass.data.location

import com.example.jetpackcompass.data.location.model.LocationInfo
import kotlinx.coroutines.flow.StateFlow

interface LocationDataSource {
    val location: StateFlow<LocationInfo?>
    fun start()
    fun stop()
}
