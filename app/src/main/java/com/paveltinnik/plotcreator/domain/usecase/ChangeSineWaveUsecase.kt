package com.paveltinnik.plotcreator.domain.usecase

import com.paveltinnik.plotcreator.data.PlotRepositoryImpl
import com.paveltinnik.plotcreator.domain.model.SineWave

data class ChangeSineWaveUsecase(
    private val repository: PlotRepositoryImpl,
) {

    operator fun invoke(sineWave: SineWave) {
        repository.changeSineParameter(sineWave)
    }
}