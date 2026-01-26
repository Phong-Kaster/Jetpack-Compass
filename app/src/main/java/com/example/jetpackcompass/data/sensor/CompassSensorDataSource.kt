package com.example.jetpackcompass.data.sensor

/**
 * There are 2 types about north direction: MAGNETIC NORTH & TRUE NORTH
 *
 */
interface CompassSensorDataSource {
    fun start(onReading: (Float) -> Unit)
    fun stop()
}
