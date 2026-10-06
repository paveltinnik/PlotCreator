package com.paveltinnik.plotcreator.domain.repository

import com.paveltinnik.plotcreator.domain.model.SineWave
import kotlinx.coroutines.flow.Flow

interface PlotRepository {

    fun changeSineParameter(sineWave: SineWave)

    val sineWaves: Flow<List<SineWave>>
}