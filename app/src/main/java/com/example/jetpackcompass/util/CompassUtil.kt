package com.example.jetpackcompass.util

import kotlin.math.abs

object CompassUtil {
    fun isAngleBetween(angle1: Float, angle2: Float, angleTolerance: Float): Boolean {
        val diff = abs(angle1 - angle2)
        return diff <= angleTolerance || diff >= 360 - angleTolerance
    }
}