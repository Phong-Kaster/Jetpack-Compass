package com.example.jetpackcompass.domain.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.jetpackcompass.R

enum class CompassDesign(
    @StringRes val titleId: Int,
    @DrawableRes val previewId: Int,
    @DrawableRes val northIcon: Int,
    @DrawableRes val dialIcon: Int,
    @DrawableRes val needle: Int,
    @DrawableRes val qiblaIcon: Int,
    @DrawableRes val lightId: Int = 0,
    val isPremium: Boolean = false,
) {
    Default(
        titleId = R.string.silver,
        previewId = R.drawable.compass8,
        northIcon = R.drawable.north8,
        dialIcon = R.drawable.dial8,
        needle = R.drawable.needle8,
        qiblaIcon = R.drawable.qibla5
    ),
    Royalty(
        titleId = R.string.royalty,
        previewId = R.drawable.compass_royalty,
        northIcon = R.drawable.north_royalty,
        dialIcon = R.drawable.dial_royalty,
        needle = R.drawable.needle_royalty,
        qiblaIcon = R.drawable.qibla_royalty,
        lightId = R.drawable.light_royalty,

        isPremium = true,
    ),
    Modern(
        titleId = R.string.modern,
        previewId = R.drawable.compass4,
        northIcon = R.drawable.north4,
        dialIcon = R.drawable.dial4,
        needle = 0,
        qiblaIcon = R.drawable.qibla4,
        isPremium = true,
    ),

    ;

    companion object {
        fun valueOfOrDefault(value: String?): CompassDesign {
            if (value == null) return Default
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                Default
            }
        }
    }
}