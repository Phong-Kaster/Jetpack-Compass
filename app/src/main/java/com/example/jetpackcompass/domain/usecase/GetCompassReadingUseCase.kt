package com.example.jetpackcompass.domain.usecase

class GetCompassReadingUseCase {

    fun applyLowPassFilter(
        previous: Float,
        current: Float,
        alpha: Float
    ): Float {
        return previous + alpha * (current - previous)
    }
}
