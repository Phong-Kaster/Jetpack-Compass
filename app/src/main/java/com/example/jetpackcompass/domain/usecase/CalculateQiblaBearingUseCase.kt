package com.example.jetpackcompass.domain.usecase

class CalculateQiblaBearingUseCase {

    fun execute(
        userLat: Double,
        userLng: Double
    ): Float {
        // Mecca position
        val meccaLat = Math.toRadians(21.42664)
        val meccaLng = Math.toRadians(39.82563)

        val lat = Math.toRadians(userLat)
        val lng = Math.toRadians(userLng)

        val dLng = meccaLng - lng

        val y = Math.sin(dLng)
        val x = Math.cos(lat) * Math.tan(meccaLat) -
                Math.sin(lat) * Math.cos(dLng)

        val bearing = Math.toDegrees(Math.atan2(y, x))
        return ((bearing + 360) % 360).toFloat()
    }
}
