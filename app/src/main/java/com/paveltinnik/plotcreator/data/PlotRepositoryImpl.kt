package com.paveltinnik.plotcreator.data

import com.paveltinnik.plotcreator.domain.model.SineWave
import com.paveltinnik.plotcreator.domain.repository.PlotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

object PlotRepositoryImpl : PlotRepository {

    private val _wavesList = mutableListOf<SineWave>(
        SineWave(1, 1.0f, 0f),
        SineWave(2, 1.0f, 50f),
        SineWave(3, 1.0f, -120f),
    )
    private val wavesList: List<SineWave>
        get() = _wavesList.toList()

    private val wavesListChangeEvents = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    override fun changeSineParameter(sineWave: SineWave) {
        val index = _wavesList.indexOfFirst { it.id == sineWave.id }
        if (index != -1) {
            _wavesList[index] = sineWave
            wavesListChangeEvents.tryEmit(Unit)
        }
    }

    override val sineWaves: Flow<List<SineWave>> = flow {
        wavesListChangeEvents.collect {
            emit(wavesList)
        }
    }
}