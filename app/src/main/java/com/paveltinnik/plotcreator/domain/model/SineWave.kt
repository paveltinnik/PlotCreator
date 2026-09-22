package com.paveltinnik.plotcreator.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SineWave(
    val id: Int,
    val amplitude: Float,
    val phase: Float,
) : Parcelable
