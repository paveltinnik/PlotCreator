package com.paveltinnik.plotcreator.presentation.plot

import com.paveltinnik.plotcreator.domain.model.SineWave
import kotlinx.coroutines.flow.StateFlow

interface PlotComponent {

    val model: StateFlow<PlotStore.State>

    fun changeSineParameters(sineWave: SineWave)

    fun changeAnimationStatus(isAnimated: Boolean)

    fun onClickBack()
}