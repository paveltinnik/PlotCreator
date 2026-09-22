package com.paveltinnik.plotcreator.domain.usecase

import com.paveltinnik.plotcreator.domain.model.SineWave
import com.paveltinnik.plotcreator.domain.repository.PlotRepository

class GetSineWaves(
    private val repository: PlotRepository
) {
    operator fun invoke(): List<SineWave> {
        return repository.getSineWaves()
    }
}