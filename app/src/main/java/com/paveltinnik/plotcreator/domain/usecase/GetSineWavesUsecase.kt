package com.paveltinnik.plotcreator.domain.usecase

import com.paveltinnik.plotcreator.data.PlotRepositoryImpl
import com.paveltinnik.plotcreator.domain.model.SineWave
import kotlinx.coroutines.flow.Flow

class GetSineWavesUsecase(
    private val repository: PlotRepositoryImpl,
) {

    operator fun invoke(): Flow<List<SineWave>> = repository.sineWaves
}