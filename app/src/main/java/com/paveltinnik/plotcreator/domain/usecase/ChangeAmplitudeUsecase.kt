package com.paveltinnik.plotcreator.domain.usecase

import com.paveltinnik.plotcreator.domain.repository.PlotRepository

data class ChangeAmplitudeUsecase(
    private val repository: PlotRepository,
) {

    operator fun invoke(amplitude: Float, phase: Float) {
        repository.changeSineParameter(
            amplitude = amplitude,
            phase = phase
        )
    }
}