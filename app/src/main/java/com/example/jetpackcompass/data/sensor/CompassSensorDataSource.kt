package com.example.jetpackcompass.data.sensor

interface CompassSensorDataSource {
    fun start(onReading: (Float) -> Unit)
    fun stop()
}
