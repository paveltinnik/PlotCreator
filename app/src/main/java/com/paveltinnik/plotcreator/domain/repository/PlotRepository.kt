package com.paveltinnik.plotcreator.domain.repository

import com.paveltinnik.plotcreator.domain.model.SineWave

interface PlotRepository {

    fun changeSineParameter(
        amplitude: Float,
        phase: Float,
    )

    fun getSineWaves(): List<SineWave>
}