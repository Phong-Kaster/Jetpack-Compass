package com.example.jetpackcompass.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.jetpackcompass.data.location.model.LocationInfo
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationSensorManager(
    context: Context
) : LocationDataSource {

    private val TAG = this.javaClass.simpleName
    private val client = LocationServices.getFusedLocationProviderClient(context)

    private val _location = MutableStateFlow<LocationInfo?>(null)
    override val location = _location.asStateFlow()

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            Log.d(TAG, "lastLocation is ${result.lastLocation}")
            val loc = result.lastLocation ?: return
            _location.value = LocationInfo(
                latitude = loc.latitude,
                longitude = loc.longitude,
                altitude = loc.altitude
            )
        }
    }

    private val request = LocationRequest
        .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
        .build()

    @SuppressLint("MissingPermission")
    override fun start() {
        client.requestLocationUpdates(
            request,
            callback,
            null
        )
    }

    override fun stop() {
        client.removeLocationUpdates(callback)
    }

}