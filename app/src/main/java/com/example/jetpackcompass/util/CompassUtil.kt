package com.example.jetpackcompass.util

import android.hardware.GeomagneticField
import com.example.jetpackcompass.data.location.model.LocationInfo
import kotlin.math.abs

object CompassUtil {
    fun isAngleBetween(angle1: Float, angle2: Float, angleTolerance: Float): Boolean {
        val diff = abs(angle1 - angle2)
        return diff <= angleTolerance || diff >= 360 - angleTolerance
    }

    fun convertFromMagneticNorthToTrueNorth(
        magneticAzimuth: Float,
        location: LocationInfo?
    ): Float {
        if (location == null) return magneticAzimuth

        val geomagneticField = GeomagneticField(
            location.latitude.toFloat(),
            location.longitude.toFloat(),
            location.altitude.toFloat(),
            System.currentTimeMillis()
        )

        val declination = geomagneticField.declination
        return (magneticAzimuth + declination + 360) % 360
    }

    fun resolveDirection(angle: Float): String {
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

    /**
     * Why compass sensors NEED this - Magnetometer output looks like this in real life: 58°, 61°, 55°, 63°, 57°, 60°, 56° ...
     * But the real direction is probably: ≈ 58°
     * this function reduce shutter like this
     */
    fun applyLowPassFilter(
        previous: Float,
        current: Float,
        alpha: Float
    ): Float {
        return previous + alpha * (current - previous)
    }

    /**
     * Return true if device points to Qibla/Mecca directly
     */
    fun currentDirectionPointToMecca(qiblaDirection: Float): Boolean {
        val tolerance = 3f
        val normalized = (qiblaDirection + 360) % 360

        return normalized <= tolerance || normalized >= 360f - tolerance
    }


}