package com.paveltinnik.plotcreator.domain.repository

import com.paveltinnik.plotcreator.domain.model.SineWave
import kotlinx.coroutines.flow.Flow

interface PlotRepository {

    fun changeSineParameter(sineWave: SineWave)

    fun setSineWaves(waves: List<SineWave>)

    val sineWaves: Flow<List<SineWave>>
}
