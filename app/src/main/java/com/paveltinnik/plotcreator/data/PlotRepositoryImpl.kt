package com.paveltinnik.plotcreator.data

import com.paveltinnik.plotcreator.domain.model.SineWave
import com.paveltinnik.plotcreator.domain.repository.PlotRepository

class PlotRepositoryImpl() : PlotRepository {

    private val _waves = mutableListOf<SineWave>()
    val waves = _waves.toList()

    init {
        for (i in 0 until 3) {
            _waves.add(
                i, SineWave(
                    id = i,
                    amplitude = 0.5f,
                    phase = 0.5f
                )
            )
        }
    }

    override fun changeSineParameter(amplitude: Float, phase: Float) {
        TODO("Not yet implemented")
    }

    override fun getSineWaves(): List<SineWave> {
        TODO("Not yet implemented")
    }
}