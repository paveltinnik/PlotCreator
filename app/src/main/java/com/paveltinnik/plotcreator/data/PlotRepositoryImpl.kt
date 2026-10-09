package com.paveltinnik.plotcreator.data

import com.paveltinnik.plotcreator.domain.model.SineWave
import com.paveltinnik.plotcreator.domain.repository.PlotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

object PlotRepositoryImpl : PlotRepository {

    private val _wavesList = mutableListOf<SineWave>(
        SineWave(id = 1, amplitude = 1.0f, frequency = 1.0f, phase = 0f),
        SineWave(id = 2, amplitude = 1.0f, frequency = 1.0f, phase = 120f),
        SineWave(id = 3, amplitude = 1.0f, frequency = 1.0f, phase = -120f),
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

    override fun setSineWaves(waves: List<SineWave>) {
        _wavesList.clear()
        _wavesList.addAll(waves)
        wavesListChangeEvents.tryEmit(Unit)
    }

    override val sineWaves: Flow<List<SineWave>> = flow {
        wavesListChangeEvents.collect {
            emit(wavesList)
        }
    }
}
