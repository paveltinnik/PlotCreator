package com.paveltinnik.plotcreator.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SineWave(
    val id: Int,
    val amplitude: Float,
    val frequency: Float = 1.0f,
    val phase: Float = 0f,
    val isVisible: Boolean = true,
) : Parcelable
